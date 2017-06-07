package com.android.futures.view;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;

import com.android.futures.entity.KDJEntity;
import com.android.futures.entity.MACDEntity;
import com.android.futures.entity.MALineEntity;
import com.android.futures.entity.OHLCEntity;
import com.android.futures.entity.RSIEntity;

public class KChartsView extends GridChart implements GridChart.OnTabClickListener {

	/** 触摸模式 */
	private static int TOUCH_MODE;
	private final static int NONE = 0;
	private final static int DOWN = 1;
	private final static int MOVE = 2;
	private final static int ZOOM = 3;

	/** 默认Y轴字体颜色 **/
	private static final int DEFAULT_AXIS_Y_TITLE_COLOR = Color.YELLOW;

	/** 默认X轴字体颜色 **/
	private static final int DEFAULT_AXIS_X_TITLE_COLOR = Color.RED;

	/** 显示的最小Candle数 */
	private final static int MIN_CANDLE_NUM = 10;

	/** 默认显示的Candle数 */
	private final static int DEFAULT_CANDLE_NUM = 50;

	/** 最小可识别的移动距离 */
	private final static int MIN_MOVE_DISTANCE = 15;

	/** Candle宽度 */
	private double mCandleWidth;

	/** 触摸点 */
	private float mStartX;
	private float mStartY;

	/** OHLC数据 */
	private List<OHLCEntity> mOHLCData;

	/** 显示的OHLC数据起始位置 */
	private int mDataStartIndext;

	/** 显示的OHLC数据个数 */
	private int mShowDataNum;

	/** 是否显示蜡烛详情 */
	private boolean showDetails;

	/** 当前数据的最大最小值 */
	private double mMaxPrice;
	private double mMinPrice;

	/** MA数据 */
	private List<MALineEntity> MALineData;

	private String mTabTitle;

	// 下部表的数据
	MACDEntity mMACDData;
	KDJEntity mKDJData;
	RSIEntity mRSIData;

	public KChartsView(Context context) {
		super(context);
		init();
	}

	public KChartsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public KChartsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		super.setOnTabClickListener(this);
		mShowDataNum = DEFAULT_CANDLE_NUM;
		mDataStartIndext = 0;
		showDetails = false;
		mMaxPrice = -1;
		mMinPrice = -1;
		mTabTitle = "MACD";

		mOHLCData = new ArrayList<OHLCEntity>();
		mMACDData = new MACDEntity(null);
		mKDJData = new KDJEntity(null);
		mRSIData = new RSIEntity(null);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mOHLCData == null || mOHLCData.size() <= 0) {
			return;
		}
		drawUpperRegion(canvas);
		drawLowerRegion(canvas);
		drawTitles(canvas);
		drawCandleDetails(canvas);
	}

	private void drawCandleDetails(Canvas canvas) {
		if (showDetails) {
			float width = getWidth();
			float left = 3.0f;
			float top = (float) (5.0 + DEFAULT_AXIS_TITLE_SIZE);
			float right = 3.0f + 7 * DEFAULT_AXIS_TITLE_SIZE;
			float bottom = 8.0f + 7 * DEFAULT_AXIS_TITLE_SIZE;
			if (mStartX < width / 2.0f) {
				right = width - 4.0f;
				left = width - 4.0f - 7 * DEFAULT_AXIS_TITLE_SIZE;
			}
			int selectIndext = (int) ((width - 2.0f - mStartX) / mCandleWidth + mDataStartIndext);

			// 绘制点击线条及详情区域
			Paint paint = new Paint();
			paint.setColor(Color.LTGRAY);
			paint.setAlpha(150);
			canvas.drawLine(mStartX, 2.0f + DEFAULT_AXIS_TITLE_SIZE, mStartX, UPER_CHART_BOTTOM,
					paint);
			canvas.drawLine(mStartX, getHeight() - 2.0f, mStartX, LOWER_CHART_TOP, paint);
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
			canvas.drawText("日期: " + mOHLCData.get(selectIndext).getDate(), left + 1, top
					+ DEFAULT_AXIS_TITLE_SIZE, textPaint);

			canvas.drawText("开盘:", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 2.0f, textPaint);
			double open = mOHLCData.get(selectIndext).getOpen();
			try {
				double ysdclose = mOHLCData.get(selectIndext + 1).getClose();
				if (open >= ysdclose) {
					textPaint.setColor(Color.RED);
				} else {
					textPaint.setColor(Color.GREEN);
				}
				canvas.drawText(new DecimalFormat("#.##").format(open), left + 1
						+ DEFAULT_AXIS_TITLE_SIZE * 2.5f, top + DEFAULT_AXIS_TITLE_SIZE * 2.0f,
						textPaint);
			} catch (Exception e) {
				canvas.drawText(new DecimalFormat("#.##").format(open), left + 1
						+ DEFAULT_AXIS_TITLE_SIZE * 2.5f, top + DEFAULT_AXIS_TITLE_SIZE * 2.0f,
						textPaint);
			}

			textPaint.setColor(Color.WHITE);
			canvas.drawText("最高:", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 3.0f, textPaint);
			double high = mOHLCData.get(selectIndext).getHigh();
			if (open < high) {
				textPaint.setColor(Color.RED);
			} else {
				textPaint.setColor(Color.GREEN);
			}
			canvas.drawText(new DecimalFormat("#.##").format(high), left + 1
					+ DEFAULT_AXIS_TITLE_SIZE * 2.5f, top + DEFAULT_AXIS_TITLE_SIZE * 3.0f,
					textPaint);

			textPaint.setColor(Color.WHITE);
			canvas.drawText("最低:", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 4.0f, textPaint);
			double low = mOHLCData.get(selectIndext).getLow();
			try {
				double yesterday = (mOHLCData.get(selectIndext + 1).getLow() + mOHLCData.get(
						selectIndext + 1).getHigh()) / 2.0f;
				if (yesterday <= low) {
					textPaint.setColor(Color.RED);
				} else {
					textPaint.setColor(Color.GREEN);
				}
			} catch (Exception e) {

			}
			canvas.drawText(new DecimalFormat("#.##").format(low), left + 1
					+ DEFAULT_AXIS_TITLE_SIZE * 2.5f, top + DEFAULT_AXIS_TITLE_SIZE * 4.0f,
					textPaint);

			textPaint.setColor(Color.WHITE);
			canvas.drawText("收盘:", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 5.0f, textPaint);
			double close = mOHLCData.get(selectIndext).getClose();
			try {
				double yesdopen = (mOHLCData.get(selectIndext + 1).getLow() + mOHLCData.get(
						selectIndext + 1).getHigh()) / 2.0f;
				if (yesdopen <= close) {
					textPaint.setColor(Color.RED);
				} else {
					textPaint.setColor(Color.GREEN);
				}
			} catch (Exception e) {

			}
			canvas.drawText(new DecimalFormat("#.##").format(close), left + 1
					+ DEFAULT_AXIS_TITLE_SIZE * 2.5f, top + DEFAULT_AXIS_TITLE_SIZE * 5.0f,
					textPaint);

			textPaint.setColor(Color.WHITE);
			canvas.drawText("涨跌幅:", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 6.0f, textPaint);
			try {
				double yesdclose = mOHLCData.get(selectIndext + 1).getClose();
				double priceRate = (close - yesdclose) / yesdclose;
				if (priceRate >= 0) {
					textPaint.setColor(Color.RED);
				} else {
					textPaint.setColor(Color.GREEN);
				}
				canvas.drawText(new DecimalFormat("#.##%").format(priceRate), left + 1
						+ DEFAULT_AXIS_TITLE_SIZE * 3.5f, top + DEFAULT_AXIS_TITLE_SIZE * 6.0f,
						textPaint);
			} catch (Exception e) {
				canvas.drawText("--", left + 1 + DEFAULT_AXIS_TITLE_SIZE * 3.5f, top
						+ DEFAULT_AXIS_TITLE_SIZE * 6.0f, textPaint);
			}
		}

	}

	private void drawTitles(Canvas canvas) {
		Paint textPaint = new Paint();
		textPaint.setColor(DEFAULT_AXIS_Y_TITLE_COLOR);
		textPaint.setTextSize(DEFAULT_AXIS_TITLE_SIZE);

		// Y轴Titles
		canvas.drawText(new DecimalFormat("#.##").format(mMinPrice), 1, UPER_CHART_BOTTOM - 1,
				textPaint);
		canvas.drawText(new DecimalFormat("#.##").format(mMinPrice + (mMaxPrice - mMinPrice) / 4),
				1, UPER_CHART_BOTTOM - getLatitudeSpacing() - 1, textPaint);
		canvas.drawText(
				new DecimalFormat("#.##").format(mMinPrice + (mMaxPrice - mMinPrice) / 4 * 2), 1,
				UPER_CHART_BOTTOM - getLatitudeSpacing() * 2 - 1, textPaint);
		canvas.drawText(
				new DecimalFormat("#.##").format(mMinPrice + (mMaxPrice - mMinPrice) / 4 * 3), 1,
				UPER_CHART_BOTTOM - getLatitudeSpacing() * 3 - 1, textPaint);
		canvas.drawText(new DecimalFormat("#.##").format(mMaxPrice), 1,
				DEFAULT_AXIS_TITLE_SIZE * 2, textPaint);

		// X轴Titles
		textPaint.setColor(DEFAULT_AXIS_X_TITLE_COLOR);
		canvas.drawText(mOHLCData.get(mDataStartIndext).getDate(), getWidth() - 4 - 4.5f
				* DEFAULT_AXIS_TITLE_SIZE, UPER_CHART_BOTTOM + DEFAULT_AXIS_TITLE_SIZE, textPaint);
		try {
			canvas.drawText(
					String.valueOf(mOHLCData.get(mDataStartIndext + mShowDataNum / 2).getDate()),
					getWidth() / 2 - 2.25f * DEFAULT_AXIS_TITLE_SIZE, UPER_CHART_BOTTOM
							+ DEFAULT_AXIS_TITLE_SIZE, textPaint);
			canvas.drawText(
					String.valueOf(mOHLCData.get(mDataStartIndext + mShowDataNum - 1).getDate()),
					2, UPER_CHART_BOTTOM + DEFAULT_AXIS_TITLE_SIZE, textPaint);
		} catch (Exception e) {

		}
	}

	private void drawUpperRegion(Canvas canvas) {
		// 绘制蜡烛图
		Paint redPaint = new Paint();
		redPaint.setColor(Color.RED);
		Paint greenPaint = new Paint();
		greenPaint.setColor(Color.GREEN);
		int width = getWidth();
		mCandleWidth = (width - 4) / 10.0 * 10.0 / mShowDataNum;
		double rate = (getUperChartHeight() - 2) / (mMaxPrice - mMinPrice);
		for (int i = 0; i < mShowDataNum && mDataStartIndext + i < mOHLCData.size(); i++) {
			OHLCEntity entity = mOHLCData.get(mDataStartIndext + i);
			float open = (float) ((mMaxPrice - entity.getOpen()) * rate + DEFAULT_AXIS_TITLE_SIZE + 4);
			float close = (float) ((mMaxPrice - entity.getClose()) * rate + DEFAULT_AXIS_TITLE_SIZE + 4);
			float high = (float) ((mMaxPrice - entity.getHigh()) * rate + DEFAULT_AXIS_TITLE_SIZE + 4);
			float low = (float) ((mMaxPrice - entity.getLow()) * rate + DEFAULT_AXIS_TITLE_SIZE + 4);

			float left = (float) (width - 2 - mCandleWidth * (i + 1));
			float right = (float) (width - 3 - mCandleWidth * i);
			float startX = (float) (width - 3 - mCandleWidth * i - (mCandleWidth - 1) / 2);
			if (open < close) {
				canvas.drawRect(left, close, right, open, greenPaint);
				canvas.drawLine(startX, high, startX, low, greenPaint);
			} else if (open == close) {
				canvas.drawLine(left, open, right, open, redPaint);
				canvas.drawLine(startX, high, startX, low, redPaint);
			} else {
				canvas.drawRect(left, open, right, close, redPaint);
				canvas.drawLine(startX, high, startX, low, redPaint);
			}

		}

		// 绘制上部曲线图及上部分MA值
		float MATitleWidth = width / 10.0f * 10.0f / MALineData.size();
		for (int j = 0; j < MALineData.size(); j++) {
			MALineEntity lineEntity = MALineData.get(j);

			float startX = 0;
			float startY = 0;
			Paint paint = new Paint();
			paint.setColor(lineEntity.getLineColor());
			paint.setTextSize(DEFAULT_AXIS_TITLE_SIZE);
			canvas.drawText(
					lineEntity.getTitle()
							+ "="
							+ new DecimalFormat("#.##").format(lineEntity.getLineData().get(
									mDataStartIndext)), 2 + MATitleWidth * j,
					DEFAULT_AXIS_TITLE_SIZE, paint);
			for (int i = 0; i < mShowDataNum
					&& mDataStartIndext + i < lineEntity.getLineData().size(); i++) {
				if (i != 0) {
					canvas.drawLine(
							startX,
							startY + DEFAULT_AXIS_TITLE_SIZE + 4,
							(float) (width - 2 - mCandleWidth * i - mCandleWidth * 0.5f),
							(float) ((mMaxPrice - lineEntity.getLineData()
									.get(mDataStartIndext + i)) * rate + DEFAULT_AXIS_TITLE_SIZE + 4),
							paint);
				}
				startX = (float) (width - 2 - mCandleWidth * i - mCandleWidth * 0.5f);
				startY = (float) ((mMaxPrice - lineEntity.getLineData().get(mDataStartIndext + i)) * rate);
			}
		}
	}

	private void drawLowerRegion(Canvas canvas) {
		float lowertop = LOWER_CHART_TOP + 1;
		float lowerHight = getHeight() - lowertop - 4;
		float viewWidth = getWidth();

		// 下部表的数据
		// MACDData mMACDData;
		// KDJData mKDJData;
		// RSIData mRSIData;
		Paint whitePaint = new Paint();
		whitePaint.setColor(Color.WHITE);
		Paint yellowPaint = new Paint();
		yellowPaint.setColor(Color.YELLOW);
		Paint magentaPaint = new Paint();
		magentaPaint.setColor(Color.MAGENTA);

		Paint textPaint = new Paint();
		textPaint.setColor(DEFAULT_AXIS_Y_TITLE_COLOR);
		textPaint.setTextSize(DEFAULT_AXIS_TITLE_SIZE);
		if (mTabTitle.trim().equalsIgnoreCase("MACD")) {
			List<Double> MACD = mMACDData.getMACD();
			List<Double> DEA = mMACDData.getDEA();
			List<Double> DIF = mMACDData.getDIF();

			double low = DEA.get(mDataStartIndext);
			double high = low;
			double rate = 0.0;
			for (int i = mDataStartIndext; i < mDataStartIndext + mShowDataNum && i < MACD.size(); i++) {
				low = low < MACD.get(i) ? low : MACD.get(i);
				low = low < DEA.get(i) ? low : DEA.get(i);
				low = low < DIF.get(i) ? low : DIF.get(i);

				high = high > MACD.get(i) ? high : MACD.get(i);
				high = high > DEA.get(i) ? high : DEA.get(i);
				high = high > DIF.get(i) ? high : DIF.get(i);
			}
			rate = lowerHight / (high - low);

			Paint paint = new Paint();
			float zero = (float) (high * rate) + lowertop;
			if (zero < lowertop) {
				zero = lowertop;
			}
			// 绘制双线
			float dea = 0.0f;
			float dif = 0.0f;
			for (int i = mDataStartIndext; i < mDataStartIndext + mShowDataNum && i < MACD.size(); i++) {
				// 绘制矩形
				if (MACD.get(i) >= 0.0) {
					paint.setColor(Color.RED);
					float top = (float) ((high - MACD.get(i)) * rate) + lowertop;
					if (zero - top < 0.55f) {
						canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
								* (i + 1 - mDataStartIndext), zero, viewWidth - 2
								- (float) mCandleWidth * (i - mDataStartIndext), zero, paint);
					} else {
						canvas.drawRect(viewWidth - 1 - (float) mCandleWidth
								* (i + 1 - mDataStartIndext), top, viewWidth - 2
								- (float) mCandleWidth * (i - mDataStartIndext), zero, paint);
					}

				} else {
					paint.setColor(Color.GREEN);
					float bottom = (float) ((high - MACD.get(i)) * rate) + lowertop;

					if (bottom - zero < 0.55f) {
						canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
								* (i + 1 - mDataStartIndext), zero, viewWidth - 2
								- (float) mCandleWidth * (i - mDataStartIndext), zero, paint);
					} else {
						canvas.drawRect(viewWidth - 1 - (float) mCandleWidth
								* (i + 1 - mDataStartIndext), zero, viewWidth - 2
								- (float) mCandleWidth * (i - mDataStartIndext), bottom, paint);
					}
				}

				if (i != mDataStartIndext) {
					canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
							* (i + 1 - mDataStartIndext) + (float) mCandleWidth / 2,
							(float) ((high - DEA.get(i)) * rate) + lowertop, viewWidth - 2
									- (float) mCandleWidth * (i - mDataStartIndext)
									+ (float) mCandleWidth / 2, dea, whitePaint);

					canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
							* (i + 1 - mDataStartIndext) + (float) mCandleWidth / 2,
							(float) ((high - DIF.get(i)) * rate) + lowertop, viewWidth - 2
									- (float) mCandleWidth * (i - mDataStartIndext)
									+ (float) mCandleWidth / 2, dif, yellowPaint);
				}
				dea = (float) ((high - DEA.get(i)) * rate) + lowertop;
				dif = (float) ((high - DIF.get(i)) * rate) + lowertop;
			}

			canvas.drawText(new DecimalFormat("#.##").format(high), 2, lowertop
					+ DEFAULT_AXIS_TITLE_SIZE - 2, textPaint);
			canvas.drawText(new DecimalFormat("#.##").format((high + low) / 2), 2, lowertop
					+ lowerHight / 2 + DEFAULT_AXIS_TITLE_SIZE, textPaint);
			canvas.drawText(new DecimalFormat("#.##").format(low), 2, lowertop + lowerHight,
					textPaint);

		} else if (mTabTitle.trim().equalsIgnoreCase("KDJ")) {
			List<Double> Ks = mKDJData.getK();
			List<Double> Ds = mKDJData.getD();
			List<Double> Js = mKDJData.getJ();

			double low = Ks.get(mDataStartIndext);
			double high = low;
			double rate = 0.0;
			for (int i = mDataStartIndext; i < mDataStartIndext + mShowDataNum && i < Ks.size(); i++) {
				low = low < Ks.get(i) ? low : Ks.get(i);
				low = low < Ds.get(i) ? low : Ds.get(i);
				low = low < Js.get(i) ? low : Js.get(i);

				high = high > Ks.get(i) ? high : Ks.get(i);
				high = high > Ds.get(i) ? high : Ds.get(i);
				high = high > Js.get(i) ? high : Js.get(i);
			}
			rate = lowerHight / (high - low);

			// 绘制白、黄、紫线
			float k = 0.0f;
			float d = 0.0f;
			float j = 0.0f;
			for (int i = mDataStartIndext; i < mDataStartIndext + mShowDataNum && i < Ks.size(); i++) {

				if (i != mDataStartIndext) {
					canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
							* (i + 1 - mDataStartIndext) + (float) mCandleWidth / 2,
							(float) ((high - Ks.get(i)) * rate) + lowertop, viewWidth - 2
									- (float) mCandleWidth * (i - mDataStartIndext)
									+ (float) mCandleWidth / 2, k, whitePaint);

					canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
							* (i + 1 - mDataStartIndext) + (float) mCandleWidth / 2,
							(float) ((high - Ds.get(i)) * rate) + lowertop, viewWidth - 2
									- (float) mCandleWidth * (i - mDataStartIndext)
									+ (float) mCandleWidth / 2, d, yellowPaint);

					canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
							* (i + 1 - mDataStartIndext) + (float) mCandleWidth / 2,
							(float) ((high - Js.get(i)) * rate) + lowertop, viewWidth - 2
									- (float) mCandleWidth * (i - mDataStartIndext)
									+ (float) mCandleWidth / 2, j, magentaPaint);
				}
				k = (float) ((high - Ks.get(i)) * rate) + lowertop;
				d = (float) ((high - Ds.get(i)) * rate) + lowertop;
				j = (float) ((high - Js.get(i)) * rate) + lowertop;
			}

			canvas.drawText(new DecimalFormat("#.##").format(high), 2, lowertop
					+ DEFAULT_AXIS_TITLE_SIZE - 2, textPaint);
			canvas.drawText(new DecimalFormat("#.##").format((high + low) / 2), 2, lowertop
					+ lowerHight / 2 + DEFAULT_AXIS_TITLE_SIZE, textPaint);
			canvas.drawText(new DecimalFormat("#.##").format(low), 2, lowertop + lowerHight,
					textPaint);

		} else if (mTabTitle.trim().equalsIgnoreCase("RSI")) {

		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// 设置触摸模式
		case MotionEvent.ACTION_DOWN:
			TOUCH_MODE = DOWN;
			mStartX = event.getRawX();
			mStartY = event.getRawY();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			if (TOUCH_MODE == DOWN) {
				TOUCH_MODE = NONE;
				if (!super.onTouchEvent(event)) {
					if (mStartX > 2.0f && mStartX < getWidth() - 2.0f) {
						showDetails = true;
					}
				}
				postInvalidate();
			} else {
				TOUCH_MODE = NONE;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			TOUCH_MODE = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mOHLCData == null || mOHLCData.size() <= 0) {
				return true;
			}
			showDetails = false;
			if (TOUCH_MODE == MOVE) {
				float horizontalSpacing = event.getRawX() - mStartX;
				if (Math.abs(horizontalSpacing) < MIN_MOVE_DISTANCE) {
					return true;
				}
				mStartX = event.getRawX();
				mStartY = event.getRawY();
				if (horizontalSpacing < 0) {
					mDataStartIndext--;
					if (mDataStartIndext < 0) {
						mDataStartIndext = 0;
					}
				} else if (horizontalSpacing > 0) {
					mDataStartIndext++;
				}
				setCurrentData();
				postInvalidate();
			} else if (TOUCH_MODE == ZOOM) {
				float verticalSpacing = event.getRawY() - mStartY;
				if (Math.abs(verticalSpacing) < MIN_MOVE_DISTANCE) {
					return true;
				}
				mStartX = event.getRawX();
				mStartY = event.getRawY();
				if (verticalSpacing < 0) {
					zoomOut();
				} else {
					zoomIn();
				}
				setCurrentData();
				postInvalidate();

			} else if (TOUCH_MODE == DOWN) {
				setTouchMode(event);
			}

			break;
		}
		return true;
	}

	private void setCurrentData() {
		if (mShowDataNum > mOHLCData.size()) {
			mShowDataNum = mOHLCData.size();
		}
		if (MIN_CANDLE_NUM > mOHLCData.size()) {
			mShowDataNum = MIN_CANDLE_NUM;
		}

		if (mShowDataNum > mOHLCData.size()) {
			mDataStartIndext = 0;
		} else if (mShowDataNum + mDataStartIndext > mOHLCData.size()) {
			mDataStartIndext = mOHLCData.size() - mShowDataNum;
		}
		mMinPrice = mOHLCData.get(mDataStartIndext).getLow();
		mMaxPrice = mOHLCData.get(mDataStartIndext).getHigh();
		for (int i = mDataStartIndext + 1; i < mOHLCData.size()
				&& i < mShowDataNum + mDataStartIndext; i++) {
			OHLCEntity entity = mOHLCData.get(i);
			mMinPrice = mMinPrice < entity.getLow() ? mMinPrice : entity.getLow();
			mMaxPrice = mMaxPrice > entity.getHigh() ? mMaxPrice : entity.getHigh();
		}

		for (MALineEntity lineEntity : MALineData) {
			for (int i = mDataStartIndext; i < lineEntity.getLineData().size()
					&& i < mShowDataNum + mDataStartIndext; i++) {
				mMinPrice = mMinPrice < lineEntity.getLineData().get(i) ? mMinPrice : lineEntity
						.getLineData().get(i);
				mMaxPrice = mMaxPrice > lineEntity.getLineData().get(i) ? mMaxPrice : lineEntity
						.getLineData().get(i);
			}
		}

	}

	private void zoomIn() {
		mShowDataNum++;
		if (mShowDataNum > mOHLCData.size()) {
			mShowDataNum = MIN_CANDLE_NUM > mOHLCData.size() ? MIN_CANDLE_NUM : mOHLCData.size();
		}

	}

	private void zoomOut() {
		mShowDataNum--;
		if (mShowDataNum < MIN_CANDLE_NUM) {
			mShowDataNum = MIN_CANDLE_NUM;
		}

	}

	private void setTouchMode(MotionEvent event) {
		float daltX = Math.abs(event.getRawX() - mStartX);
		float daltY = Math.abs(event.getRawY() - mStartY);
		if (FloatMath.sqrt(daltX * daltX + daltY * daltY) > MIN_MOVE_DISTANCE) {
			if (daltX < daltY) {
				TOUCH_MODE = ZOOM;
			} else {
				TOUCH_MODE = MOVE;
			}
			mStartX = event.getRawX();
			mStartY = event.getRawY();
		}
	}

	/**
	 * 初始化MA值，从数组的最后一个数据开始初始化
	 * 
	 * @param entityList
	 * @param days
	 * @return
	 */
	private List<Float> initMA(List<OHLCEntity> entityList, int days) {
		if (days < 2 || entityList == null || entityList.size() <= 0) {
			return null;
		}
		List<Float> MAValues = new ArrayList<Float>();

		float sum = 0;
		float avg = 0;
		for (int i = entityList.size() - 1; i >= 0; i--) {
			float close = (float) entityList.get(i).getClose();
			if (i > entityList.size() - days) {
				sum = sum + close;
				avg = sum / (entityList.size() - i);
			} else {
				sum = close + avg * (days - 1);
				avg = sum / days;
			}
			MAValues.add(avg);
		}

		List<Float> result = new ArrayList<Float>();
		for (int j = MAValues.size() - 1; j >= 0; j--) {
			result.add(MAValues.get(j));
		}
		return result;
	}

	public List<OHLCEntity> getOHLCData() {
		return mOHLCData;
	}

	public void setOHLCData(List<OHLCEntity> OHLCData) {
		if (OHLCData == null || OHLCData.size() <= 0) {
			return;
		}
		this.mOHLCData = OHLCData;
		initMALineData();
		mMACDData = new MACDEntity(mOHLCData);
		mKDJData = new KDJEntity(mOHLCData);
		mRSIData = new RSIEntity(mOHLCData);

		setCurrentData();
		postInvalidate();
	}

	private void initMALineData() {
		MALineEntity MA5 = new MALineEntity();
		MA5.setTitle("MA5");
		MA5.setLineColor(Color.WHITE);
		MA5.setLineData(initMA(mOHLCData, 5));

		MALineEntity MA10 = new MALineEntity();
		MA10.setTitle("MA10");
		MA10.setLineColor(Color.CYAN);
		MA10.setLineData(initMA(mOHLCData, 10));

		MALineEntity MA20 = new MALineEntity();
		MA20.setTitle("MA20");
		MA20.setLineColor(Color.BLUE);
		MA20.setLineData(initMA(mOHLCData, 20));

		MALineData = new ArrayList<MALineEntity>();
		MALineData.add(MA5);
		MALineData.add(MA10);
		MALineData.add(MA20);

	}

	public void onTabClick(int indext) {
		String[] titles = getLowerChartTabTitles();
		mTabTitle = titles[indext];
		postInvalidate();
	}
}
