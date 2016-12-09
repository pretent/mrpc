package org.pretent.mrpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.pretent.mrpc.ServerConfig;
import org.pretent.mrpc.message.ObjectMessage;
import org.pretent.mrpc.provider.Service;
import org.pretent.mrpc.provider.ServiceFactory;
import org.pretent.mrpc.util.ResourcesFactory;
import org.pretent.rpc.message.request.HeaderMessage;

/**
 * @author Administrator
 */
public class MinaInvocationHandler<T> implements InvocationHandler {

	private static final Logger LOGGER = Logger.getLogger(MinaInvocationHandler.class);

	private Class<T> clazz;

	private NioSocketConnector connector;

	private ServiceFactory serviceFactory;

	public MinaInvocationHandler(Class<T> clazz, ServiceFactory serviceFactory) {
		this.clazz = clazz;
		this.serviceFactory = serviceFactory;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return call(method, args);
	}

	private Object call(Method method, Object[] args) throws Exception {
		connector = new NioSocketConnector();
		DefaultIoFilterChainBuilder chain = connector.getFilterChain();
		chain.addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		connector.setConnectTimeoutMillis(ResourcesFactory.getInt(ServerConfig.KEY_STRING_CONSUMER_TIMEOUT) == null
				? 1500 : ResourcesFactory.getInt(ServerConfig.KEY_STRING_CONSUMER_TIMEOUT));
		SocketSessionConfig cfg = connector.getSessionConfig();
		cfg.setUseReadOperation(true);
		// 获取服务
		Service provider = serviceFactory.getService(clazz.getName());
		LOGGER.debug("connection to " + provider.getIp() + ":" + provider.getPort() + "/" + clazz.getName());
		InetSocketAddress point = new InetSocketAddress(provider.getIp(), provider.getPort());
		IoSession session = connector.connect(point).awaitUninterruptibly().getSession();
		HeaderMessage request = new HeaderMessage(clazz.getName(), method.getName(), args);
		session.write(request);
		ReadFuture readFuture = session.read();
		if (readFuture.awaitUninterruptibly(ResourcesFactory.getInt(ServerConfig.KEY_STRING_CONSUMER_TIMEOUT) == null
				? 1500 : ResourcesFactory.getInt(ServerConfig.KEY_STRING_CONSUMER_TIMEOUT), TimeUnit.MILLISECONDS)) {
			session.getService().dispose();
			connector.dispose();
			return ((ObjectMessage) readFuture.getMessage()).getContent();
		} else {
			session.getService().dispose();
			connector.dispose();
			throw new Exception("read timeout.");
		}
	}
}
