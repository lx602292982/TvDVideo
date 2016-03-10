package com.softwinner.TvdVideo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.softwinner.TvdVideo.adapter.VideoAdapter;
import com.softwinner.TvdVideo.model.FileVideo;
import com.softwinner.TvdVideo.utils.VideoData;
import com.softwinner.TvdVideo.utils.VideoUtils;
import com.softwinner.TvdVideo.view.SurfaceView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity implements OnClickListener {
	private ListView mVideoList;
	private VideoAdapter adapter;
	private LinearLayout mVideoInfoList;
	private RadioGroup mBtnSelector;
	private ImageView mSpende;
	private ImageView mNextSong;
	private ImageView mOnSong;
	private SeekBar mSeekBar;
	private SurfaceView mSurfaceView;
	private TextView mMusicTotalTime;
	private TextView mMusicCurrentTime;
	private ImageView logo;
	private MediaPlayer mPlayer = null;
	private Handler mSeekBarSyncHandler = new Handler();
	private Runnable runnable;
	private LinearLayout mController;
	private int mPosition = 0;
	private int currentPosition = 0;
	private boolean PasueFlag = false;
	private List<FileVideo> mVideo = new ArrayList<FileVideo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		registerListener();
	}

	@SuppressLint("CutPasteId")
	private void initView() {
		mVideoList = (ListView) findViewById(R.id.swipe_list);
		mBtnSelector = (RadioGroup) findViewById(R.id.btn_select);
		mVideoInfoList = (LinearLayout) findViewById(R.id.video_list);
		mSpende = (ImageView) findViewById(R.id.image_spende);
		mNextSong = (ImageView) findViewById(R.id.image_next);
		mOnSong = (ImageView) findViewById(R.id.image_on);
		mSeekBar = (SeekBar) findViewById(R.id.seekBar);
		logo = (ImageView) findViewById(R.id.logo);
		mSurfaceView = (SurfaceView) findViewById(R.id.surface);
		mMusicTotalTime = (TextView) findViewById(R.id.music_total_time);
		mMusicCurrentTime = (TextView) findViewById(R.id.music_current_time);
		mController = (LinearLayout) findViewById(R.id.controller);
		// 把输送给surfaceView视频画面，直接显示在屏幕上，不要维持它自身的缓冲区
		mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceView.getHolder().setKeepScreenOn(true);// 使屏幕保持高亮状态
		new VideoData(FileVideoCallback, getApplicationContext()).run();
		mBtnSelector.check(mBtnSelector.getChildAt(0).getId());
		mSeekBar.setOnSeekBarChangeListener(chenageSeekBar);
		mController.getBackground().setAlpha(120);
		mSurfaceView.setOnClickListener(this);
		mOnSong.setOnClickListener(this);
		mSpende.setOnClickListener(this);
		mNextSong.setOnClickListener(this);
		mPlayer = new MediaPlayer();
		runnable = new Runnable() {
			@Override
			public void run() {
				mhandler.sendMessage(Message.obtain(mhandler, 1));
			}
		};
	}

	private Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				final int max = mPlayer.getDuration();
				mSeekBar.setMax(max);
				mMusicTotalTime.setText(VideoUtils.getTime(max));
				if (mPlayer != null && mPlayer.isPlaying()) {
					currentPosition = mPlayer.getCurrentPosition();
					mMusicCurrentTime.setText(VideoUtils.getTime(currentPosition));
					mSeekBar.setProgress(currentPosition);
					mSeekBarSyncHandler.postDelayed(runnable, 1000);
				}
			}
		};
	};

	private SeekBar.OnSeekBarChangeListener chenageSeekBar = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			int progress = seekBar.getProgress();
			currentPosition = seekBar.getProgress();
			if (mPlayer != null) {
				mPlayer.seekTo(progress);
				int value = progress * mPlayer.getDuration() / seekBar.getMax();
				mMusicCurrentTime.setText(VideoUtils.getTime(value));
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

		}
	};

	private void registerListener() {
		mBtnSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (mBtnSelector.getChildAt(0).getId() == checkedId) {
					mVideoInfoList.setVisibility(View.VISIBLE);
				} else if (mBtnSelector.getChildAt(1).getId() == checkedId) {
					mVideoInfoList.setVisibility(View.GONE);
				}
			}
		});
		mVideoList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mPosition = position;
				mSpende.setVisibility(View.GONE);
				initData(position);
			}
		});
	}

	private void initData(int position) {
		adapter.setPositionSelector(mPosition);
		if (PasueFlag) {
			if (mPlayer != null) {
				mPlayer.seekTo(currentPosition);
				mSeekBarSyncHandler.postDelayed(runnable, 1000);
				mPlayer.start();
			}
			PasueFlag = false;
		} else {
			if (mVideo.size() != 0) {
				mPlayer.reset();
				try {
					mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					mPlayer.setDataSource(mVideo.get(position).getUrl());
					mPlayer.setOnPreparedListener(MediaPlayerPreparedListener);
					mPlayer.prepareAsync();
					mPlayer.setOnCompletionListener(MediaPlayerCompletionListener);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	MediaPlayer.OnPreparedListener MediaPlayerPreparedListener = new OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer mp) {
			mPlayer.setDisplay(mSurfaceView.getHolder());
			mPlayer.seekTo(0);
			PasueFlag = false;
			mSeekBarSyncHandler.postDelayed(runnable, 1000);
			mPlayer.start();
		}
	};

	MediaPlayer.OnCompletionListener MediaPlayerCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			setNextSong();
		}
	};

	public VideoData.Callback FileVideoCallback = new VideoData.Callback() {
		@Override
		public void onSuccess(List<FileVideo> mVideoInfo) {
			if (mVideoInfo.size() != 0) {
				mController.setVisibility(View.VISIBLE);
				mVideoList.setVisibility(View.VISIBLE);
				logo.setVisibility(View.GONE);
				mVideoList.setBackgroundResource(R.color.gray);
				adapter = new VideoAdapter(MainActivity.this, mVideoInfo);
				mVideoList.setAdapter(adapter);
				mVideo = mVideoInfo;
				initData(mPosition);
			} else {
				mController.setVisibility(View.GONE);
				mVideoList.setVisibility(View.GONE);
				logo.setVisibility(View.VISIBLE);
				mSurfaceView.setBackgroundColor(Color.parseColor("#262626"));
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_TRADITIONAL);
				builder.setTitle("提示").setCancelable(false).setMessage("没有本地视频文件")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
								finish();
							}
						}).create().show();
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image_spende:
			setPlay();
			break;
		case R.id.surface:
			if (PasueFlag == false) {
				setSpende();
			} else {
				setPlay();
			}
			break;
		case R.id.image_next:
			setOnSong();
			break;
		case R.id.image_on:
			setNextSong();
			break;
		default:
			break;
		}
	}

	public void setPlay() {
		mSpende.setVisibility(View.GONE);
		initData(mPosition);
	}

	public void setSpende() {
		mSpende.setVisibility(View.VISIBLE);
		if (mPlayer != null && mPlayer.isPlaying()) {
			currentPosition = mPlayer.getCurrentPosition();
			mSeekBarSyncHandler.removeCallbacks(runnable);
			mPlayer.pause();
			PasueFlag = true;
		}
	}

	public void setOnSong() {
		if (mPosition > 0) {
			mPosition -= 1;
		} else {
			mPosition = mVideo.size() - 1;
		}
		if (PasueFlag==true) {
			PasueFlag = false;
			mSpende.setVisibility(View.GONE);
		}
		initData(mPosition);
	}

	public void setNextSong() {
		if (mPosition < mVideo.size() - 1) {
			mPosition += 1;
		} else {
			mPosition = 0;
		}
		if (PasueFlag==true) {
			PasueFlag = false;
			mSpende.setVisibility(View.GONE);
		}
		initData(mPosition);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mSpende.setVisibility(View.GONE);
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		if (mPlayer != null) {
			mSeekBarSyncHandler.removeCallbacks(runnable);
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
		super.onBackPressed();
	}

}
