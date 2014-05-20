package com.example.daysjourney.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.util.Log;

public class IpSubnet {
	private static final String LOG = "Exception in IpSubnet";
	private String subnet;
	
	private IpSubnet(){
		super();
		// TODO Auto-generated constructor stub
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = (NetworkInterface) en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = (InetAddress) enumIpAddr
							.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						subnet = inetAddress.getHostAddress().toString();
						if (InetAddressUtils.isIPv4Address(subnet)) {
							int idx1 = subnet.lastIndexOf(".");
							subnet = "http://" + subnet.substring(0, idx1)
									+ ".*";
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e(LOG, e.toString());
		}
	}
	
	private static IpSubnet ipSubnet = new IpSubnet();
	
	public static IpSubnet getIpSubnet(){
		return ipSubnet;
	}
	
	
	public String getSubnet(){
		return subnet;
	}

}
