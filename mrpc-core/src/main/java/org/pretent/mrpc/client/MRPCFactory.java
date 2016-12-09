package org.pretent.mrpc.client;

import java.lang.reflect.Proxy;

import org.pretent.mrpc.proxy.MinaInvocationHandler;
import org.pretent.mrpc.proxy.SocketInvocationHandler;
import org.pretent.mrpc.register.zk.ZkServiceFactory;

public class MRPCFactory {

	public static <T> T getSoceketProxy(Class<T> clazz) throws Exception {
		if (!clazz.isInterface()) {
			throw new IllegalArgumentException("not a interface");
		}
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz },
				new SocketInvocationHandler(clazz, new ZkServiceFactory()));
	}

	public static <T> T getProxy(Class<T> clazz) throws Exception {
		if (!clazz.isInterface()) {
			throw new IllegalArgumentException("not a interface");
		}
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz },
				new MinaInvocationHandler(clazz, new ZkServiceFactory()));
	}
}
