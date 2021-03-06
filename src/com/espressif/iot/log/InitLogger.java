package com.espressif.iot.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

// it is generated by python
public class InitLogger {

	public static void init() {
		// ######content######
		ConfigureLog4J.configure();
		Logger.getLogger(com.espressif.iot.base.net.wifi.WifiAdmin.class)
				.setLevel(Level.DEBUG);
		Logger.getLogger(com.afunx.wifirssisender.MainActivity.class).setLevel(
				Level.DEBUG);
		Logger.getLogger(com.afunx.wifirssisender.WifiScanService.class)
				.setLevel(Level.DEBUG);
	}
}