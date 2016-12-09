package org.pretent.mrpc.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.log4j.Logger;
import org.pretent.mrpc.ServerConfig;
import org.pretent.mrpc.message.Message;
import org.pretent.mrpc.message.ObjectMessage;
import org.pretent.mrpc.message.request.HeaderMessage;
import org.pretent.mrpc.provider.Service;
import org.pretent.mrpc.provider.ServiceFactory;
import org.pretent.mrpc.register.ProtocolType;
import org.pretent.mrpc.register.redis.RedisServiceFactory;
import org.pretent.mrpc.register.zk.ZkServiceFactory;
import org.pretent.mrpc.util.ProtocolUtils;
import org.pretent.mrpc.util.ResourcesFactory;

/**
 * @author pretent
 */
public class SocketInvocationHandler<T> implements InvocationHandler {

	private static final Logger LOGGER = Logger.getLogger(SocketInvocationHandler.class);

	private Class<T> clazz;

	private Socket socket;

	private ServiceFactory serviceFactory;

	private int timeout;

	public SocketInvocationHandler(Class<T> clazz) throws Exception {
		this.clazz = clazz;
		init();
	}

	private void init() throws Exception {
		timeout = ResourcesFactory.getInt(ServerConfig.KEY_STRING_CONSUMER_TIMEOUT) == null ? 1500
				: ResourcesFactory.getInt(ServerConfig.KEY_STRING_CONSUMER_TIMEOUT);
		socket = new Socket();
		try {
			socket.setSoTimeout(timeout);
		} catch (SocketException e) {
			LOGGER.error(e.getStackTrace());
		}
		serviceFactory = getServiceFactory();
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return call(method, args);
	}

	private Object call(Method method, Object[] args) throws Exception {
		Service provider = serviceFactory.getService(clazz.getName());
		LOGGER.debug("connection to " + provider.getIp() + ":" + provider.getPort() + "/" + clazz.getName());
		InetSocketAddress point = new InetSocketAddress(provider.getIp(), provider.getPort());
		if (socket.isConnected()) {
			socket.close();
		}
		try {
			socket.connect(point);
		} catch (IOException e) {
			LOGGER.debug("connect fail try again");
			socket.connect(point);
		}
		HeaderMessage request = new HeaderMessage(provider.getClassName(), method.getName(), args);
		writeObject(socket.getOutputStream(), request);
		Object retval = null;
		try {
			retval = readObject(socket.getInputStream());
		} catch (SocketTimeoutException e) {
			LOGGER.debug("read data timeout ,try again");
			try {
				retval = readObject(socket.getInputStream());
			} catch (SocketTimeoutException e1) {
				LOGGER.debug("service is not available,try other.");
				socket.close();
				return call(method, args);
			}
		}
		if (retval instanceof ObjectMessage) {
			if (socket != null) {
				socket.close();
			}
			return ((ObjectMessage) retval).getContent();
		}
		return null;
	}

	private Message readObject(InputStream in) throws Exception {
		ObjectInputStream ois = new ObjectInputStream(in);
		return (Message) ois.readObject();
	}

	/**
	 * @param out
	 * @param obj
	 * @throws Exception
	 */
	private void writeObject(OutputStream out, Serializable obj) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.reset();
		oos.writeObject(obj);
	}

	public ServiceFactory getServiceFactory() throws Exception {
		if (serviceFactory != null) {
			return serviceFactory;
		}
		ProtocolType protocol = ProtocolUtils.getProtocol();
		LOGGER.info("protocol : "+protocol.name());
		switch (protocol) {
			case ZOOKEEPER: {
				return new ZkServiceFactory();
			}
			case REDIS: {
				return new RedisServiceFactory();
			}
			case MULTICAST: {
				throw new Exception("not implementats");
			}
			default: {
				return new ZkServiceFactory();
			}
		}
	}

}
