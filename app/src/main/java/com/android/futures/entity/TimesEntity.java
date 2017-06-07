package com.android.futures.entity;

/**
 * 分时图的每分钟数据
 * 
 * @author nanjingbiao
 * 
 */
public class TimesEntity {
	private String time;// 时间
	private double weightedIndex;// 大盘加权指数
	private double nonWeightedIndex;// 大盘不加权指数
	private int buy;// 买入量
	private int sell;// 卖出量
	private int volume;// 交易量
	private float buyRatio;// 买入率
	private float sellRatio;// 卖出率

	public TimesEntity() {
		super();
	}

	public TimesEntity(String time, double weightedIndex, double nonWeightedIndex, int buy,
			int sell, int volume) {
		super();
		this.time = time;
		this.weightedIndex = weightedIndex;
		this.nonWeightedIndex = nonWeightedIndex;
		this.buy = buy;
		this.sell = sell;
		this.volume = volume;
		this.buyRatio = buy * 10.0f / 10.0f / (buy + sell);
		this.sellRatio = sell * 10.0f / 10.0f / (buy + sell);
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public double getWeightedIndex() {
		return weightedIndex;
	}

	public void setWeightedIndex(double weightedIndex) {
		this.weightedIndex = weightedIndex;
	}

	public double getNonWeightedIndex() {
		return nonWeightedIndex;
	}

	public void setNonWeightedIndex(double nonWeightedIndex) {
		this.nonWeightedIndex = nonWeightedIndex;
	}

	public int getBuy() {
		return buy;
	}

	public void setBuy(int buy) {
		this.buy = buy;
	}

	public int getSell() {
		return sell;
	}

	public void setSell(int sell) {
		this.sell = sell;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public float getBuyRatio() {
		return buyRatio;
	}

	public void setBuyRatio(float buyRatio) {
		this.buyRatio = buyRatio;
	}

	public float getSellRatio() {
		return sellRatio;
	}

	public void setSellRatio(float sellRatio) {
		this.sellRatio = sellRatio;
	}

}
