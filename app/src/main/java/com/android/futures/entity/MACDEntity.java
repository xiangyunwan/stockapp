package com.android.futures.entity;

import java.util.ArrayList;
import java.util.List;


public class MACDEntity {

	private List<Double> DEAs;
	private List<Double> DIFs;
	private List<Double> MACDs;

	public MACDEntity(List<OHLCEntity> OHLCData) {
		DEAs = new ArrayList<Double>();
		DIFs = new ArrayList<Double>();
		MACDs = new ArrayList<Double>();

		List<Double> dEAs = new ArrayList<Double>();
		List<Double> dIFs = new ArrayList<Double>();
		List<Double> mACDs = new ArrayList<Double>();

		double eMA12 = 0.0;
		double eMA26 = 0.0;
		double close = 0;
		double dIF = 0.0;
		double dEA = 0.0;
		double mACD = 0.0;
		if (OHLCData != null && OHLCData.size() > 0) {

			for (int i = OHLCData.size() - 1; i >= 0; i--) {
				close = OHLCData.get(i).getClose();
				if (i == OHLCData.size() - 1) {
					eMA12 = close;
					eMA26 = close;
				} else {
					eMA12 = eMA12 * 11 / 13 + close * 2 / 13;
					eMA26 = eMA26 * 25 / 27 + close * 2 / 27;
					dIF = eMA12 - eMA26;
					dEA = dEA * 8 / 10 + dIF * 2 / 10;
					mACD = dIF - dEA;
				}
				dEAs.add(dEA);
				dIFs.add(dIF);
				mACDs.add(mACD);
			}

			for (int i = dEAs.size() - 1; i >= 0; i--) {
				DEAs.add(dEAs.get(i));
				DIFs.add(dIFs.get(i));
				MACDs.add(mACDs.get(i));
			}
		}

	}

	public List<Double> getDEA() {
		return DEAs;
	}

	public List<Double> getDIF() {
		return DIFs;
	}

	public List<Double> getMACD() {
		return MACDs;
	}

}
