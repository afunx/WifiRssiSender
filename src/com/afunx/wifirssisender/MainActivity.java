package com.afunx.wifirssisender;

import java.util.List;

import org.apache.log4j.Logger;

import com.afunx.wifirssisender.WifiScanService.MyBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private static final Logger log = Logger.getLogger(MainActivity.class);

	private static final int MENU_ID_START_SERVICE = 1;
	private static final int MENU_ID_STOP_SERVICE = 2;
	private static final int MENU_ID_TEST = 3;

	private WifiScanService mWifiScanService;

	private ServiceConnection sc = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			log.info("ServiceConnection onServiceDisconnected()");
			mWifiScanService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			log.info("ServiceConnection onServiceConnected()");
			mWifiScanService = ((MyBinder) service).getService();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log.info("onCreate");
		Intent intent = new Intent(this, WifiScanService.class);
		bindService(intent, sc, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		log.info("onDestroy");
		Intent intent = new Intent(this, WifiScanService.class);
		stopService(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ID_START_SERVICE, Menu.NONE, "Start Service");
		menu.add(Menu.NONE, MENU_ID_STOP_SERVICE, Menu.NONE, "Stop Service");
		menu.add(Menu.NONE, MENU_ID_TEST, Menu.NONE, "Test");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ID_START_SERVICE:
			log.debug("menu item start service is clicked");
			mWifiScanService.startService();
			break;
		case MENU_ID_STOP_SERVICE:
			log.debug("menu item stop service is clicked");
			mWifiScanService.stopService();
			break;
		case MENU_ID_TEST:
			log.debug("menu item test");
			List<AfxUnit>topList = mWifiScanService.getTopList(5);
			log.error("topList:" + topList);
			break;
		}
		return true;
	}
}
