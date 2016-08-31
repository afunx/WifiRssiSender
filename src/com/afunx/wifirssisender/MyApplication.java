package com.afunx.wifirssisender;

import com.espressif.iot.log.InitLogger;

import android.app.Application;
import android.os.Environment;

public class MyApplication extends Application {
	
	private static MyApplication instance;

	public static MyApplication sharedInstance() {
		if (instance == null) {
			throw new NullPointerException(
					"MyApplication instance is null, please register in AndroidManifest.xml first");
		}
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		InitLogger.init();
	}
	
	public String getEspRootSDPath()
    {
        String path = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            path = Environment.getExternalStorageDirectory().toString() + "/WifiRssiSender/";
        }
        return path;
    }
}
