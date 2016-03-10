package com.softwinner.TvdVideo.adapter;

import java.util.List;

import com.softwinner.TvdVideo.R;
import com.softwinner.TvdVideo.model.FileVideo;
import com.softwinner.TvdVideo.utils.AsynImageLoader;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VideoAdapter extends BaseAdapter {
	private Context context;
	private List<FileVideo> list;
	private int prositionSelector = -1;
	private AsynImageLoader imageLoader;

	public VideoAdapter(Context context, List<FileVideo> mList) {
		this.context = context;
		this.list = mList;
		this.imageLoader = new AsynImageLoader();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public FileVideo getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setPositionSelector(int position) {
		this.prositionSelector = position;
		notifyDataSetChanged();
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
			holder = new ViewHolder();
			holder.mThumbnail = (ImageView) convertView.findViewById(R.id.iv_video);
			holder.mTitle = (TextView) convertView.findViewById(R.id.tv_title);
			holder.mTotaltime = (TextView) convertView.findViewById(R.id.tv_size);
			holder.mMain = (LinearLayout) convertView.findViewById(R.id.main);
			holder.imagePlay = (ImageView) convertView.findViewById(R.id.image_play);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (prositionSelector == position) {
			holder.mMain.setBackgroundColor(Color.parseColor("#28457d"));
			holder.imagePlay.setVisibility(View.GONE);
		} else {
			holder.mMain.setBackgroundColor(Color.parseColor("#262626"));
			holder.imagePlay.setVisibility(View.VISIBLE);
		}
		FileVideo video = list.get(position);
		if (null != video) {
			imageLoader.showImageAsyn(holder.mThumbnail, video.getUrl(), R.drawable.ic_loading);
			holder.mTitle.setText(video.getmTilte());
			holder.mTotaltime.setText(video.getmTotalTime());
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView mThumbnail;
		TextView mTitle;
		TextView mTotaltime;
		LinearLayout mMain;
		ImageView imagePlay;
	}

}
