package com.android.futures.entity;

public class AppInfo {
	private boolean shouldUpdate;// 是否需要更新应用
	private String appUri;// 新版本应用地址

	public AppInfo() {
		super();
	}

	public AppInfo(boolean shouldUpdate, String appUri) {
		super();
		this.shouldUpdate = shouldUpdate;
		this.appUri = appUri;
	}

	public boolean isShouldUpdate() {
		return shouldUpdate;
	}

	public void setShouldUpdate(boolean shouldUpdate) {
		this.shouldUpdate = shouldUpdate;
	}

	public String getAppUri() {
		return appUri;
	}

	public void setAppUri(String appUri) {
		this.appUri = appUri;
	}

}
