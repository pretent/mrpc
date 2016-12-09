package org.pretent.mrpc.register;

import org.pretent.mrpc.ServerConfig;
import org.pretent.mrpc.util.ResourcesFactory;

public class RegisterFactory {

	private ProtocolType protocol;

	public RegisterFactory() {
		String ptl = ResourcesFactory.getString(ServerConfig.KEY_STRING_PROTOCOL);
		if (ptl == null) {
			String address = ResourcesFactory.getString(ServerConfig.KEY_STRING_REGISTER) == null
					? "zookeeper://127.0.0.1:2181" : ResourcesFactory.getString(ServerConfig.KEY_STRING_REGISTER);
			protocol = ProtocolType.valueOf(address.substring(0, address.indexOf(":")).toUpperCase());
		} else {
			protocol = ProtocolType.valueOf(ResourcesFactory.getString(ServerConfig.KEY_STRING_PROTOCOL).toUpperCase());
		}
	}

	public Register getRegister() throws Exception {
		return getRegister(protocol);
	}

	public Register getRegister(ProtocolType protocol) throws Exception {
		if (protocol == ProtocolType.DEFAULT || protocol == ProtocolType.ZOOKEEPER) {
			return new ZkRegister();
		}
		throw new Exception("not implementats");
	}

	public ProtocolType getProtocol() {
		return protocol;
	}

	public void setProtocol(ProtocolType protocol) {
		this.protocol = protocol;
	}

}
