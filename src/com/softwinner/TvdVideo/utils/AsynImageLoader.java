package com.softwinner.TvdVideo.utils;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class AsynImageLoader {
	private static final String TAG = "AsynImageLoader";
	private Map<String, SoftReference<Bitmap>> caches;
	private List<Task> taskQueue;
	private boolean isRunning = false;

	public AsynImageLoader() {
		caches = new HashMap<String, SoftReference<Bitmap>>();
		taskQueue = new ArrayList<AsynImageLoader.Task>();
		isRunning = true;
		new Thread(runnable).start();
	}

	public void showImageAsyn(ImageView imageView, String url, int resId) {
		imageView.setTag(url);
		Bitmap bitmap = loadImageAsyn(url, getImageCallback(imageView, resId));
		if (bitmap == null) {
			imageView.setImageResource(resId);
		} else {
			imageView.setImageBitmap(bitmap);
		}
	}

	public Bitmap loadImageAsyn(String path, ImageCallback callback) {
		if (caches.containsKey(path)) {
			SoftReference<Bitmap> rf = caches.get(path);
			Bitmap bitmap = rf.get();
			if (bitmap == null) {
				caches.remove(path);
			} else {
				Log.i(TAG, "return image in cache" + path);
				return bitmap;
			}
		} else {
			Task task = new Task();
			task.path = path;
			task.callback = callback;
			Log.i(TAG, "new Task ," + path);
			if (!taskQueue.contains(task)) {
				taskQueue.add(task);
				synchronized (runnable) {
					runnable.notify();
				}
			}
		}
		return null;
	}

	private ImageCallback getImageCallback(final ImageView imageView, final int resId) {
		return new ImageCallback() {

			@Override
			public void loadImage(String path, Bitmap bitmap) {
				if (path.equals(imageView.getTag().toString())) {
					imageView.setImageBitmap(bitmap);
				} else {
					imageView.setImageResource(resId);
				}
			}
		};
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Task task = (Task) msg.obj;
			task.callback.loadImage(task.path, task.bitmap);
		}

	};

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			while (isRunning) {
				while (taskQueue.size() > 0) {
					Task task = taskQueue.remove(0);
					task.bitmap = VideoUtils.createVideoThumbnail(task.path);
					caches.put(task.path, new SoftReference<Bitmap>(task.bitmap));
					if (handler != null) {
						Message msg = handler.obtainMessage();
						msg.obj = task;
						handler.sendMessage(msg);
					}
				}
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	public interface ImageCallback {
		void loadImage(String path, Bitmap bitmap);
	}

	class Task {
		String path;
		Bitmap bitmap;
		ImageCallback callback;

		@Override
		public boolean equals(Object o) {
			Task task = (Task) o;
			return task.path.equals(path);
		}
	}
}
