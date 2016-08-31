package com.afunx.wifirssisender;

public class AfxUnit implements Comparable<AfxUnit> {

	private final String mBssid;
	private final String mSsid;
	private final int mRssi;

	private AfxUnit(String bssid, String ssid, int rssi) {
		mBssid = bssid;
		mSsid = ssid;
		mRssi = rssi;
	}

	public static AfxUnit create(String bssid, String ssid, int rssi) {
		return new AfxUnit(bssid, ssid, rssi);
	}

	public int getRssi() {
		return mRssi;
	}

	public String getBssid() {
		return mBssid;
	}

	public String getSsid() {
		return mSsid;
	}

	@Override
	public boolean equals(Object o) {
		// check the type
		if (o == null || !(o instanceof AfxUnit)) {
			return false;
		}
		AfxUnit other = (AfxUnit) o;
		return other.getBssid().equals(mBssid);
	}

	@Override
	public int hashCode() {
		return mBssid.hashCode();
	}
	
	@Override
	public int compareTo(AfxUnit another) {
		return another.mRssi - mRssi;
	}
	
	@Override
	public String toString() {
		return "{" + "AfxUnit=" + "[" + "bssid=" + mBssid + ","
				+ "ssid=" + mSsid + ",rssi=" + mRssi + "]" + "}";
	}
	
}
