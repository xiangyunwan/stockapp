package com.android.futures.view;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.android.futures.entity.TimesEntity;

public class TimesView extends GridChart {
	private final int DATA_MAX_COUNT = 4 * 60;
	private List<TimesEntity> timesList;

	private float uperBottom;
	private float uperHeight;
	private float lowerBottom;
	private float lowerHeight;
	private float dataSpacing;

	private double initialWeightedIndex;
	private float uperHalfHigh;
	private float lowerHigh;
	private float uperRate;
	private float lowerRate;

	private boolean showDetails;
	private float touchX;

	public TimesView(Context context) {
		super(context);
		init();
	}

	public TimesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TimesView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		super.setShowLowerChartTabs(false);
		super.setShowTopTitles(false);

		timesList = null;
		uperBottom = 0;
		uperHeight = 0;
		lowerBottom = 0;
		lowerHeight = 0;
		dataSpacing = 0;

		initialWeightedIndex = 0;
		uperHalfHigh = 0;
		lowerHigh = 0;
		uperRate = 0;
		lowerRate = 0;
		showDetails = false;
		touchX = 0;

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (timesList == null || timesList.size() <= 0) {
			return;
		}
		uperBottom = UPER_CHART_BOTTOM - 2;
		uperHeight = getUperChartHeight() - 4;
		lowerBottom = getHeight() - 3;
		lowerHeight = getLowerChartHeight() - 2;
		dataSpacing = (getWidth() - 4) * 10.0f / 10.0f / DATA_MAX_COUNT;

		if (uperHalfHigh > 0) {
			uperRate = uperHeight / uperHalfHigh / 2.0f;
		}
		if (lowerHigh > 0) {
			lowerRate = lowerHeight / lowerHigh;
		}

		// 绘制上部曲线及下部线条
		drawLines(canvas);

		// 绘制坐标标题
		drawTitles(canvas);

		// 绘制点击时的详细信息
		drawDetails(canvas);
	}

	private void drawDetails(Canvas canvas) {
		if (showDetails) {
			float width = getWidth();
			float left = 5.0f;
			float top = 4.0f;
			float right = 3.0f + 6.5f * DEFAULT_AXIS_TITLE_SIZE;
			float bottom = 7.0f + 4 * DEFAULT_AXIS_TITLE_SIZE;
			if (touchX < width / 2.0f) {
				right = width - 4.0f;
				left = width - 4.0f - 6.5f * DEFAULT_AXIS_TITLE_SIZE;
			}

			// 绘制点击线条及详情区域
			Paint paint = new Paint();
			paint.setColor(Color.LTGRAY);
			paint.setAlpha(150);
			canvas.drawLine(touchX, 2.0f, touchX, UPER_CHART_BOTTOM, paint);
			canvas.drawLine(touchX, lowerBottom - lowerHeight, touchX, lowerBottom, paint);
			canvas.drawRect(left, top, right, bottom, paint);

			Paint borderPaint = new Paint();
			borderPaint.setColor(Color.WHITE);
			borderPaint.setStrokeWidth(2);
			canvas.drawLine(left, top, left, bottom, borderPaint);
			canvas.drawLine(left, top, right, top, borderPaint);
			canvas.drawLine(right, bottom, right, top, borderPaint);
			canvas.drawLine(right, bottom, left, bottom, borderPaint);

			// 绘制详情文字
			Paint textPaint = new Paint();
			textPaint.setTextSize(DEFAULT_AXIS_TITLE_SIZE);
			textPaint.setColor(Color.WHITE);
			textPaint.setFakeBoldText(true);
			try {
				TimesEntity fenshiData = timesList.get((int) ((touchX - 2) / dataSpacing));
				canvas.drawText("时间: " + fenshiData.getTime(), left + 1, top
						+ DEFAULT_AXIS_TITLE_SIZE, textPaint);

				canvas.drawText("价格:", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 2.0f, textPaint);
				double price = fenshiData.getWeightedIndex();
				if (price >= initialWeightedIndex) {
					textPaint.setColor(Color.RED);
				} else {
					textPaint.setColor(Color.GREEN);
				}
				canvas.drawText(new DecimalFormat("#.##").format(price), left + 1
						+ DEFAULT_AXIS_TITLE_SIZE * 2.5f, top + DEFAULT_AXIS_TITLE_SIZE * 2.0f,
						textPaint);

				textPaint.setColor(Color.WHITE);
				canvas.drawText("涨跌:", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 3.0f, textPaint);
				double change = (fenshiData.getWeightedIndex() - initialWeightedIndex)
						/ initialWeightedIndex;
				if (change >= 0) {
					textPaint.setColor(Color.RED);
				} else {
					textPaint.setColor(Color.GREEN);
				}
				canvas.drawText(new DecimalFormat("#.##%").format(change), left + 1
						+ DEFAULT_AXIS_TITLE_SIZE * 2.5f, top + DEFAULT_AXIS_TITLE_SIZE * 3.0f,
						textPaint);

				textPaint.setColor(Color.WHITE);
				canvas.drawText("成交:", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 4.0f, textPaint);
				textPaint.setColor(Color.YELLOW);
				canvas.drawText(String.valueOf(fenshiData.getVolume()), left + 1
						+ DEFAULT_AXIS_TITLE_SIZE * 2.5f, top + DEFAULT_AXIS_TITLE_SIZE * 4.0f,
						textPaint);

			} catch (Exception e) {
				canvas.drawText("时间: --", left + 1, top + DEFAULT_AXIS_TITLE_SIZE, textPaint);
				canvas.drawText("价格: --", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 2.0f, textPaint);
				canvas.drawText("涨跌: --", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 3.0f, textPaint);
				canvas.drawText("成交: --", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 4.0f, textPaint);
			}
		}

	}

	private void drawTitles(Canvas canvas) {
		// 绘制Y轴titles
		float viewWidth = getWidth();
		Paint paint = new Paint();
		paint.setTextSize(DEFAULT_AXIS_TITLE_SIZE);

		paint.setColor(Color.GREEN);
		canvas.drawText(new DecimalFormat("#.##").format(initialWeightedIndex - uperHalfHigh), 2,
				uperBottom, paint);
		String text = new DecimalFormat("#.##%").format(-uperHalfHigh / initialWeightedIndex);
		canvas.drawText(text, viewWidth - 5 - text.length() * DEFAULT_AXIS_TITLE_SIZE / 2.0f,
				uperBottom, paint);

		canvas.drawText(
				new DecimalFormat("#.##").format(initialWeightedIndex - uperHalfHigh * 0.5f), 2,
				uperBottom - getLatitudeSpacing(), paint);
		text = new DecimalFormat("#.##%").format(-uperHalfHigh * 0.5f / initialWeightedIndex);
		canvas.drawText(text, viewWidth - 5 - text.length() * DEFAULT_AXIS_TITLE_SIZE / 2.0f,
				uperBottom - getLatitudeSpacing(), paint);

		paint.setColor(Color.WHITE);
		canvas.drawText(new DecimalFormat("#.##").format(initialWeightedIndex), 2, uperBottom
				- getLatitudeSpacing() * 2, paint);
		text = "0.00%";
		canvas.drawText(text, viewWidth - 6 - text.length() * DEFAULT_AXIS_TITLE_SIZE / 2.0f,
				uperBottom - getLatitudeSpacing() * 2, paint);

		paint.setColor(Color.RED);
		canvas.drawText(
				new DecimalFormat("#.##").format(uperHalfHigh * 0.5f + initialWeightedIndex), 2,
				uperBottom - getLatitudeSpacing() * 3, paint);
		text = new DecimalFormat("#.##%").format(uperHalfHigh * 0.5f / initialWeightedIndex);
		canvas.drawText(text, viewWidth - 6 - text.length() * DEFAULT_AXIS_TITLE_SIZE / 2.0f,
				uperBottom - getLatitudeSpacing() * 3, paint);

		canvas.drawText(new DecimalFormat("#.##").format(uperHalfHigh + initialWeightedIndex), 2,
				DEFAULT_AXIS_TITLE_SIZE, paint);
		text = new DecimalFormat("#.##%").format(uperHalfHigh / initialWeightedIndex);
		canvas.drawText(text, viewWidth - 6 - text.length() * DEFAULT_AXIS_TITLE_SIZE / 2.0f,
				DEFAULT_AXIS_TITLE_SIZE, paint);

		// 绘制X轴Titles
		canvas.drawText("09:30", 2, uperBottom + DEFAULT_AXIS_TITLE_SIZE, paint);
		canvas.drawText("11:30/13:00", viewWidth / 2.0f - DEFAULT_AXIS_TITLE_SIZE * 2.5f,
				uperBottom + DEFAULT_AXIS_TITLE_SIZE, paint);
		canvas.drawText("15:00", viewWidth - 2 - DEFAULT_AXIS_TITLE_SIZE * 2.5f, uperBottom
				+ DEFAULT_AXIS_TITLE_SIZE, paint);
	}

	private void drawLines(Canvas canvas) {
		float x = 0;
		float uperWhiteY = 0;
		float uperYellowY = 0;
		Paint paint = new Paint();
		for (int i = 0; i < timesList.size() && i < DATA_MAX_COUNT; i++) {
			TimesEntity fenshiData = timesList.get(i);

			// 绘制上部表中曲线
			float endWhiteY = (float) (uperBottom - (fenshiData.getNonWeightedIndex()
					+ uperHalfHigh - initialWeightedIndex)
					* uperRate);
			float endYelloY = (float) (uperBottom - (fenshiData.getWeightedIndex() + uperHalfHigh - initialWeightedIndex)
					* uperRate);

			if (i != 0) {
				paint.setColor(Color.WHITE);
				canvas.drawLine(x, uperWhiteY, 3 + dataSpacing * i, endWhiteY, paint);
				paint.setColor(Color.YELLOW);
				canvas.drawLine(x, uperYellowY, 3 + dataSpacing * i, endYelloY, paint);
			}

			x = 3 + dataSpacing * i;
			uperWhiteY = endWhiteY;
			uperYellowY = endYelloY;

			// 绘制下部表内数据线
			int buy = fenshiData.getBuy();
			if (i <= 0) {
				paint.setColor(Color.RED);
			} else if (fenshiData.getNonWeightedIndex() >= timesList.get(i - 1)
					.getNonWeightedIndex()) {
				paint.setColor(Color.RED);
			} else {
				paint.setColor(Color.GREEN);
			}
			canvas.drawLine(x, lowerBottom, x, lowerBottom - buy * lowerRate, paint);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			touchX = event.getRawX();
			if (touchX < 2 || touchX > getWidth() - 2) {
				return false;
			}
			showDetails = true;
			postInvalidate();
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
			showDetails = false;
			break;

		default:
			break;
		}

		return true;
	}

	public void setTimesList(List<TimesEntity> timesList) {
		if (timesList == null || timesList.size() <= 0) {
			return;
		}
		this.timesList = timesList;

		TimesEntity fenshiData = timesList.get(0);
		double weightedIndex = fenshiData.getWeightedIndex();
		double nonWeightedIndex = fenshiData.getNonWeightedIndex();
		int buy = fenshiData.getBuy();
		initialWeightedIndex = weightedIndex;
		lowerHigh = buy;
		for (int i = 1; i < timesList.size() && i < DATA_MAX_COUNT; i++) {
			fenshiData = timesList.get(i);
			weightedIndex = fenshiData.getWeightedIndex();
			nonWeightedIndex = fenshiData.getNonWeightedIndex();
			buy = fenshiData.getBuy();

			uperHalfHigh = (float) (uperHalfHigh > Math
					.abs(nonWeightedIndex - initialWeightedIndex) ? uperHalfHigh : Math
					.abs(nonWeightedIndex - initialWeightedIndex));
			uperHalfHigh = (float) (uperHalfHigh > Math.abs(weightedIndex - initialWeightedIndex) ? uperHalfHigh
					: Math.abs(weightedIndex - initialWeightedIndex));
			lowerHigh = lowerHigh > buy ? lowerHigh : buy;
		}
		postInvalidate();

	}
}
