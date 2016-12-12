package org.pretent.mrpc.support.config;

/**
 * 服务注册
 */
public class ServiceConfig {

	private String interfaceName;

	private String ref;

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}
}