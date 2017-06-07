package com.android.futures.entity;

public class FutruesEntity {
	private String name;// 期货名称
	private String code;// 期货代码
	private double price;// 当前价格
	private double changeAmount;// 涨跌额
	private double changeRate;// 涨跌幅

	public FutruesEntity() {
		super();
	}

	public FutruesEntity(String name, String code, double price, double changeAmount,
			double changeRate) {
		super();
		this.name = name;
		this.code = code;
		this.price = price;
		this.changeAmount = changeAmount;
		this.changeRate = changeRate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getChangeAmount() {
		return changeAmount;
	}

	public void setChangeAmount(double changeAmount) {
		this.changeAmount = changeAmount;
	}

	public double getChangeRate() {
		return changeRate;
	}

	public void setChangeRate(double changeRate) {
		this.changeRate = changeRate;
	}

}
