package com.softwinner.TvdVideo.view;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class CustomToast {
	public static void show(Context context,int res) {
		show(context, context.getResources().getString(res));
	}
	public static void show(Context context,String text) {
		Toast toast = Toast.makeText(context, text ,Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}
