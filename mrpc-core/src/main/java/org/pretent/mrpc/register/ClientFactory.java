package org.pretent.mrpc.register;

import org.I0Itec.zkclient.ZkClient;
import org.pretent.mrpc.ServerConfig;
import org.pretent.mrpc.util.ResourcesFactory;

public class ClientFactory {

	private String address;

	public ClientFactory(String address) {
		this.address = address;
	}

	public ClientFactory() {
		address = ResourcesFactory.getString(ServerConfig.KEY_STRING_REGISTER) == null ? "zookeeper://127.0.0.1:2181"
				: ResourcesFactory.getString(ServerConfig.KEY_STRING_REGISTER);
	}

	public Object getClient(ProtocolType type) throws Exception {
		if (type == ProtocolType.DEFAULT || type == ProtocolType.ZOOKEEPER) {
			return new ZkClient(getAddress(address));
		}
		throw new Exception("not implementats");
	}

	private String getAddress(String address) {
		return address.substring(address.lastIndexOf("/") + 1);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
