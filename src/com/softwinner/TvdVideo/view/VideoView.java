/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.softwinner.TvdVideo.view;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

/**
 * Displays a video file. The VideoView class can load images from various
 * sources (such as resources or content providers), takes care of computing its
 * measurement from the video so that it can be used in any layout manager, and
 * provides various display options such as scaling and tinting.
 */
public class VideoView extends SurfaceView {
	private String TAG = "VideoView";
	private int mZoomMode = 0;

	// all video zoom mode
	private static final int ZOOM_FULL_SCREEN_SCREEN_RATIO = 0;
	private static final int ZOOM_FULL_SCREEN_VIDEO_RATIO = 1;
	private static final int ZOOM_ORIGIN_SIZE = 2;

	// All the stuff we need for playing and showing a video
	private int mVideoWidth;
	private int mVideoHeight;
	// preparing

	Dialog mErrorDialog;

	public VideoView(Context context) {
		super(context);
	}

	public VideoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
		int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
		Log.d(TAG, "#################onMeasure(), width = " + width + ", height = " + height + " mZoomMode = "
				+ mZoomMode);
		if (mVideoWidth > 0 && mVideoHeight > 0) {
			setvideoSize(width, height);
		} else {
			setMeasuredDimension(width, height);
		}
	}

	private void setvideoSize(int width, int height) {
		switch (mZoomMode) {
		case ZOOM_FULL_SCREEN_SCREEN_RATIO: {
			int tempWidth, tempHeight;
			if (mVideoWidth * height > width * mVideoHeight) {
				tempHeight = width * mVideoHeight / mVideoWidth;
				height = tempHeight + (height - tempHeight - 10);
			} else if (mVideoWidth * height < width * mVideoHeight) {
				tempWidth = height * mVideoWidth / mVideoHeight;
				width = tempWidth + (width - tempWidth - 50);
			}
			break;
		}

		case ZOOM_FULL_SCREEN_VIDEO_RATIO: {
			if (mVideoWidth * height > width * mVideoHeight) {
				height = width * mVideoHeight / mVideoWidth;
			} else if (mVideoWidth * height < width * mVideoHeight) {
				width = height * mVideoWidth / mVideoHeight;
			}
			break;
		}

		case ZOOM_ORIGIN_SIZE: {
			if (width > mVideoWidth) {
				width = mVideoWidth;
			}
			if (height > mVideoHeight) {
				height = mVideoHeight;
			}

			break;
		}
		default:
			break;
		}
		Log.d(TAG, "#################setvideoSize(), result: width = " + width + ", height = " + height);
		setMeasuredDimension(width, height);
	}

	/**
	 * switch another screen size to show video.
	 * <p>
	 * 
	 * @param mode
	 *            the zoom's index in the zoom list.
	 */
	public void setZoomMode(int mode) {
		if (mode == mZoomMode) {
			return;
		}
		mZoomMode = mode;
		if (mVideoWidth > 0 && mVideoHeight > 0) {
			// getHolder().setFixedSize(mVideoWidth, mVideoHeight);
			if (mVideoWidth < 3840 || mVideoHeight < 2160) {
				getHolder().setFixedSize(mVideoWidth, mVideoHeight);
			} else {
				// getHolder().setFixedSize(1920, 1080); //delete by liuanlong
			}
			requestLayout();
		}
	}

	/**
	 * get screen size to show video.
	 * <p>
	 * 
	 */
	public int getZoomMode() {
		return mZoomMode;
	}

}
