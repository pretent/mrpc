package org.pretent.mrpc.util;

import org.pretent.mrpc.ServerConfig;
import org.pretent.mrpc.register.ProtocolType;

public class ProtocolUtils {

	public static ProtocolType getProtocol() {
		ProtocolType protocol = null;
		String ptl = ResourcesFactory.getString(ServerConfig.KEY_STRING_PROTOCOL);
		if (ptl == null) {
			String address = ResourcesFactory.getString(ServerConfig.KEY_STRING_REGISTER) == null
					? "zookeeper://127.0.0.1:2181" : ResourcesFactory.getString(ServerConfig.KEY_STRING_REGISTER);
			protocol = ProtocolType.valueOf(address.substring(0, address.indexOf(":")).toUpperCase());
		} else {
			protocol = ProtocolType.valueOf(ResourcesFactory.getString(ServerConfig.KEY_STRING_PROTOCOL).toUpperCase());
		}
		return protocol;
	}
}
