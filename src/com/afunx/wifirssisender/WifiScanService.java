package com.afunx.wifirssisender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.espressif.iot.base.net.wifi.WifiAdmin;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Binder;
import android.os.IBinder;

public class WifiScanService extends Service {

	private static final Logger log = Logger.getLogger(WifiScanService.class);
	
	
	// INTERVAL shouldn't less than IWifiAdmin.SCAN_CHECK_INTERVAL = 100
	private static final int INTERVAL = 500;
	
	public class MyBinder extends Binder {
		public WifiScanService getService() {
			return WifiScanService.this;
		}
	}

	// LAYER means how many layer is cached local
	// top list will get from all layer
	private static final int CACHE_LAYER = 2;
	
	private MyBinder mBinder;

	private ScanWifiTask mScanWifiTask;
	
	private List<ArrayList<AfxUnit>> mAfxUnitsLists;
	
	private final Object lock = new Object();
	
	@Override
	public IBinder onBind(Intent intent) {
		log.info("onBind");
		mBinder = new MyBinder();
		mScanWifiTask = new ScanWifiTask();
		mAfxUnitsLists = new ArrayList<ArrayList<AfxUnit>>();
		for (int i = 0; i < CACHE_LAYER; ++i) {
			ArrayList<AfxUnit> afxUnits = new ArrayList<AfxUnit>();
			mAfxUnitsLists.add(afxUnits);
		}
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		log.info("onUnbind");
		if (mScanWifiTask != null) {
			mScanWifiTask.stopTask();
			mScanWifiTask = null;
		}
		return super.onUnbind(intent);
	}

	public List<AfxUnit> getTopList(int topN) {
		if(topN <= 0) {
			throw new RuntimeException("topN should greater than 0");
		}
		synchronized (lock) {
			// get all afx units
			List<AfxUnit> allAfxUnits = new ArrayList<AfxUnit>();
			for (int layer = 0; layer < CACHE_LAYER; ++layer) {
				allAfxUnits.addAll(mAfxUnitsLists.get(layer));
			}
			// sort all afx units
			Collections.sort(allAfxUnits);
			// get top N list
			List<AfxUnit> topList = new ArrayList<AfxUnit>();
			for (AfxUnit afxUnit : allAfxUnits) {
				if (!topList.contains(afxUnit)) {
					topList.add(afxUnit);
				}
				if (topList.size() == topN) {
					break;
				}
			}
			return topList;
		}
	}

	public void startService() {
		log.debug("startService()");
		mScanWifiTask.startTask();
	}

	public void stopService() {
		log.debug("stopService()");
		mScanWifiTask.stopTask();
	}

	private class ScanWifiTask {

		private int mCurrentLayer;
		private Thread mThread;
		private volatile boolean mIsInterrupted = true;
		
		private WifiAdmin gWifiAdmin = WifiAdmin.getInstance();
		
		private boolean isAfxUnitValid(AfxUnit afxUnit) {
			// TODO - check whether the bssid is valid
			// TODO - check whether the rssi is valid(ignore too little one)
			return true;
		}

		private List<AfxUnit> scanAfxUnits() {
			List<ScanResult> scanResults = gWifiAdmin.scan();
			Set<AfxUnit> unitSet = new HashSet<AfxUnit>();
			for (ScanResult scanResult : scanResults) {
				String bssid = scanResult.BSSID;
				String ssid = scanResult.SSID;
				int rssi = scanResult.level;
				AfxUnit afxUnit = AfxUnit.create(bssid, ssid, rssi);
				if(isAfxUnitValid(afxUnit)) {
					unitSet.add(afxUnit);
				}
			}
			List<AfxUnit> unitList = new ArrayList<AfxUnit>();
			unitList.addAll(unitSet);
			return unitList;
		}
		
		private void doScanTask() {
			synchronized (lock) {
				log.debug("doScanTask() currentLayer=" + mCurrentLayer);
				// get current layer
				ArrayList<AfxUnit> afxUnits = mAfxUnitsLists.get(mCurrentLayer);
				// update specific layer
				afxUnits.clear();
				afxUnits.addAll(scanAfxUnits());
				mCurrentLayer = (mCurrentLayer + 1) % CACHE_LAYER;
			}
		}
		
		public void startTask() {
			if (!mIsInterrupted) {
				stopTask();
			}
			mCurrentLayer = 0;
			mIsInterrupted = false;
			mThread = new Thread() {
				public void run() {
					log.debug("startTask() run");
					long startTimestamp;
					long sleepTimestamp;
					while (!mIsInterrupted) {
						log.debug("startTask() running...");
						// set startTimestamp
						startTimestamp = System.currentTimeMillis();
						
						// do scan task
						doScanTask();

						// sleep some time if necessary
						sleepTimestamp = INTERVAL
								- (System.currentTimeMillis() - startTimestamp);
						if (sleepTimestamp > 0) {
							try {
								Thread.sleep(sleepTimestamp);
							} catch (InterruptedException e) {
								log.debug("InterruptedException: " + e);
								break;
							}
						}
					}
				}
			};
			mThread.start();
		}

		public void stopTask() {
			mIsInterrupted = true;
			if (mThread != null) {
				mThread.interrupt();
				mThread = null;
			}
		}

	}
}
