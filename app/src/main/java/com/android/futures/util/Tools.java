package com.android.futures.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class Tools {

	public static void startActivity(Context context, Class<? extends Activity> cls) {
		Intent intent = new Intent(context, cls);
		context.startActivity(intent);
	}
}
