package org.pretent.mrpc.provider;

import java.io.Serializable;

public class Service implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String className;
	private String method;
	private String ip;
	private int port;
	private long createTime;

	public Service() {
	}

	public Service(String ip, int port) {
		super();
		this.ip = ip;
		this.port = port;
	}

	
	public Service(String className, String ip, int port) {
		super();
		this.className = className;
		this.ip = ip;
		this.port = port;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

}
