package com.android.futures.entity;

public class AnnouncementEntity {
	private String title;// 公告标题
	private String date;// 公告日期
	private String content;// 公告内容

	public AnnouncementEntity() {
		super();
	}

	public AnnouncementEntity(String title, String date, String content) {
		super();
		this.title = title;
		this.date = date;
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
