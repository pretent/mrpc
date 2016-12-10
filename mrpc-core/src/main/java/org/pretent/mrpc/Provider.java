package org.pretent.mrpc;

import java.util.HashMap;
import java.util.Map;

import org.pretent.mrpc.register.Register;

public interface Provider {

	/**
	 * all published object
	 */
	Map<String, Object> ALL_OBJECT = new HashMap<String, Object>();

	void start() throws Exception;

	void setRegister(Register register);

	Register getRegister();

	void setPort(int port);

	int getPort();

	void setHost(String host);

	String getHost();

	/**
	 * 发布单个对象
	 * 
	 * @param object
	 * @throws Exception
	 */
	void export(Object object) throws Exception;

	/**
	 * 根据包名注册发布单个对象
	 * 
	 * @param packageName
	 * @throws Exception
	 */
	void export(String packageName) throws Exception;

}
