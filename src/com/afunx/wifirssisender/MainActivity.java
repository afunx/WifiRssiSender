package com.afunx.wifirssisender;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	private static final Logger log = Logger.getLogger(MainActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		log.debug("onCreate");
	}
}
