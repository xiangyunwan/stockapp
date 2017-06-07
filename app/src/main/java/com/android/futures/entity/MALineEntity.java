package com.android.futures.entity;

import java.util.List;

public class MALineEntity {

	/** 线表示数据 */
	private List<Float> lineData;

	/** 线的标题 */
	private String title;

	/** 线表示颜色 */
	private int lineColor;

	public MALineEntity() {
		super();
	}

	public MALineEntity(List<Float> lineData, String title, int lineColor) {
		this.lineData = lineData;
		this.title = title;
		this.lineColor = lineColor;
	}

	public List<Float> getLineData() {
		return lineData;
	}

	public void setLineData(List<Float> lineData) {
		this.lineData = lineData;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getLineColor() {
		return lineColor;
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}

}
