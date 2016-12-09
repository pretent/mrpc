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
import java.net.SocketTimeoutException;

import org.apache.log4j.Logger;
import org.pretent.mrpc.ServerConfig;
import org.pretent.mrpc.message.Message;
import org.pretent.mrpc.message.ObjectMessage;
import org.pretent.mrpc.message.request.HeaderMessage;
import org.pretent.mrpc.provider.Service;
import org.pretent.mrpc.provider.ServiceFactory;
import org.pretent.mrpc.util.ResourcesFactory;

/**
 * @author pretent
 */
public class SocketInvocationHandler<T> implements InvocationHandler {

	private static final Logger LOGGER = Logger.getLogger(SocketInvocationHandler.class);

	private Class<T> clazz;

	private Socket socket;

	private ServiceFactory serviceFactory;

	public SocketInvocationHandler(Class<T> clazz, ServiceFactory serviceFactory) {
		this.serviceFactory = serviceFactory;
		this.clazz = clazz;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return call(method, args);
	}

	private Object call(Method method, Object[] args) throws Exception {
		this.socket = new Socket();
		socket.setSoTimeout(ResourcesFactory.getInt(ServerConfig.KEY_STRING_CONSUMER_TIMEOUT) == null ? 1500
				: ResourcesFactory.getInt(ServerConfig.KEY_STRING_CONSUMER_TIMEOUT));
		// 获取服务
		Service provider = serviceFactory.getService(clazz.getName());
		LOGGER.debug("connection to " + provider.getIp() + ":" + provider.getPort() + "/" + clazz.getName());
		InetSocketAddress point = new InetSocketAddress(provider.getIp(), provider.getPort());
		try {
			socket.connect(point);
		} catch (IOException e) {
			// 重连第二次
			LOGGER.debug("connect fail try again");
			socket.connect(point);
		}
		HeaderMessage request = new HeaderMessage(provider.getClassName(), method.getName(), args);
		writeObject(socket.getOutputStream(), request);
		Object retval = null;
		try {
			retval = readObject(socket.getInputStream());
		} catch (SocketTimeoutException e)

		{
			LOGGER.debug("read data timeout ,try again");
			// 重读第二次
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

}
