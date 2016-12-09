package org.pretent.mrpc.util;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class IPHelper {

	public static String getIp(String host) {
		String ip = host;
		try {
			if (ip.startsWith("0.")) {
				ip = Inet4Address.getLocalHost().getHostAddress();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ip;
	}

}
