package com.android.futures;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class WelcomeActivity extends Activity implements Handler.Callback {

	private Handler handler;
	private final int WHAT = 1987;
	private final long DELAY_TIME = 1500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		handler = new Handler(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new Thread(new Runnable() {

			public void run() {
				handler.sendEmptyMessageDelayed(WHAT, DELAY_TIME);

			}
		}).start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		handler.removeMessages(WHAT);
	}

	public boolean handleMessage(Message msg) {

		if (msg.what == WHAT) {
			Intent intent = new Intent(WelcomeActivity.this, MyFragmentActivity.class);
			startActivity(intent);
			finish();
			return true;
		}
		return false;

	}
}
