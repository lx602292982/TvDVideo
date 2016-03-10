package com.softwinner.TvdVideo.view;

import android.content.Context;
import android.util.AttributeSet;

public class SurfaceView extends android.view.SurfaceView {

	  public SurfaceView(Context context) {
	        super(context);
	    }

	    public SurfaceView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	    }

	    public SurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
	        super(context, attrs, defStyleAttr);
	    }

	    @Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	        int width = getDefaultSize(getWidth(), widthMeasureSpec);
	        int height = getDefaultSize(getHeight(), heightMeasureSpec);
	        setMeasuredDimension(width, height);
	    }
}
