package org.pretent.mrpc.provider;

public interface ServiceFactory {
	
	/**
	 * 获取服务
	 *
	 * @return
	 * @throws Exception
	 */
	public Service getService(String serviceName) throws Exception;

}
