package com.android.futures.entity;

public class GameLogEntity {
	private String time;// 购买时间，如2013-09-18 13:55
	private int type;// 购买类型，1代表买涨，0代表买平，-1代表买跌
	private int price;// 单注价格
	private int count;// 购买数量
	private int result;// 游戏结果，1代表赢，2代表输

	public GameLogEntity() {
		super();
	}

	public GameLogEntity(String time, int type, int price, int count, int result) {
		super();
		this.time = time;
		this.type = type;
		this.price = price;
		this.count = count;
		this.result = result;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}
