package com.android.futures.entity;

public class PayLogEntity {
	private String time;// 交易时间，如：2013-09-18 13:45
	private double money;// 交易金额
	private int type;// 交易类型，1代表支付，2代表提现消除

	public PayLogEntity() {
		super();
	}

	public PayLogEntity(String time, double money, int type) {
		super();
		this.time = time;
		this.money = money;
		this.type = type;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
