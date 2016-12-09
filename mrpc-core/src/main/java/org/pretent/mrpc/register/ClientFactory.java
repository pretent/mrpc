package org.pretent.mrpc.register;

import org.I0Itec.zkclient.ZkClient;
import org.pretent.mrpc.ServerConfig;
import org.pretent.mrpc.util.ResourcesFactory;
import redis.clients.jedis.Jedis;

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
		switch(type){
			case ZOOKEEPER:{
				return new ZkClient(getAddress(address));
			}
			case REDIS:{
				return new Jedis(getHost(address), getPort(address));
			}
			case MULTICAST:{
				throw new Exception("not implementats");
			}
			default:{
				return new ZkClient(getAddress(address));
			}
	
		}
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

	public String getHost(String address){
		return address.substring(address.lastIndexOf("/") + 1,address.indexOf(":"));
	}
	public  int getPort(String address){
		String port=address.substring(address.lastIndexOf("/") + 1,address.indexOf(":"));
		port=port.length()>2?port:"6379";
		return  Integer.parseInt(port);
	}
}
