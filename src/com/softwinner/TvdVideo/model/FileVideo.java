package com.softwinner.TvdVideo.model;

public class FileVideo {
	private String mTilte;
	private String mSize;
	private String mTotalTime;
	private int mId;
	private String url;

	public FileVideo(){
		
	}
	
	public FileVideo(String mTilte, String mSize, String mTotalTime, int mId, String url) {
		super();
		this.mTilte = mTilte;
		this.mSize = mSize;
		this.mTotalTime = mTotalTime;
		this.mId = mId;
		this.url = url;
	}

	public String getmTilte() {
		return mTilte;
	}

	public void setmTilte(String mTilte) {
		this.mTilte = mTilte;
	}

	public String getmSize() {
		return mSize;
	}

	public void setmSize(String mSize) {
		this.mSize = mSize;
	}

	public String getmTotalTime() {
		return mTotalTime;
	}

	public void setmTotalTime(String mTotalTime) {
		this.mTotalTime = mTotalTime;
	}

	public int getmId() {
		return mId;
	}

	public void setmId(int mId) {
		this.mId = mId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
