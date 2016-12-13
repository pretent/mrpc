package org.pretent.mrpc.support.bean;

public class ImplProxyBean {

	private String id;
	private String interfaceName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	@Override
	public String toString() {
		return "ImplProxyBean [id=" + id + ", interfaceName=" + interfaceName + "]";
	}
}
