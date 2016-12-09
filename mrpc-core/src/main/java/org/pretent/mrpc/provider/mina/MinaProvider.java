package org.pretent.mrpc.provider.mina;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.pretent.mrpc.Provider;
import org.pretent.mrpc.ServerConfig;
import org.pretent.mrpc.provider.Service;
import org.pretent.mrpc.register.Register;
import org.pretent.mrpc.register.RegisterFactory;
import org.pretent.mrpc.util.ClassUtils;
import org.pretent.mrpc.util.IPHelper;
import org.pretent.mrpc.util.MapHelper;
import org.pretent.mrpc.util.ResourcesFactory;

/**
 * @author pretent
 */
public class MinaProvider implements Provider {

	private static final Logger LOGGER = Logger.getLogger(MinaProvider.class);

	private IoAcceptor acceptor = null;

	private String host;

	private int port;

	private Register register;

	public MinaProvider() throws Exception {
		acceptor = new NioSocketAcceptor();
		register = new RegisterFactory().getRegister();
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10000);
		setHost(ResourcesFactory.getString(ServerConfig.KEY_STRING_HOST) == null ? "0.0.0.0"
				: ResourcesFactory.getString(ServerConfig.KEY_STRING_HOST));
		setPort(ResourcesFactory.getInt(ServerConfig.KEY_INT_PORT) == null ? 51000
				: ResourcesFactory.getInt(ServerConfig.KEY_INT_PORT));
	}

	public void publish(Object object) throws Exception {
		LOGGER.info("-----------------publish starting");
		String interfaceName = object.getClass().getInterfaces()[0].getName();
		ALL_OBJECT.put(interfaceName, object);
		register.register(new Service(interfaceName, IPHelper.getIp(host), port));
	}

	/**
	 * 根据包名扫描
	 */
	public void publish(String packageName) throws Exception {
		LOGGER.info("-----------------publish starting");
		if (packageName == null) {
			throw new NullPointerException("packageName is null");
		}
		Set<Class<?>> classes = ClassUtils.getClasses(packageName);
		Iterator<Class<?>> iter = classes.iterator();
		Set<Service> servicees = new HashSet<Service>();
		while (iter.hasNext()) {
			Class<?> clazz = iter.next();
			if (!clazz.isInterface()) {
				servicees.add(new Service(clazz.getInterfaces()[0].getName(), IPHelper.getIp(host), port));
				ALL_OBJECT.put(clazz.getInterfaces()[0].getName(), clazz.newInstance());
			}
		}
		register.register(servicees);
	}

	public void start() throws Exception {
		MapHelper.print(ALL_OBJECT);
		bind(host, port);
	}

	private void bind(String host, int port) throws Exception {
		SocketAddress endpoint = new InetSocketAddress(host, port);
		LOGGER.info("published...");
		LOGGER.info("server started on " + host + ":" + port + "...");
		DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
		ObjectSerializationCodecFactory objSerialFactory = new ObjectSerializationCodecFactory();
		objSerialFactory.setDecoderMaxObjectSize(10240000);
		objSerialFactory.setEncoderMaxObjectSize(10240000);
		chain.addLast("codec", new ProtocolCodecFilter(objSerialFactory));
		acceptor.setHandler(new MinaHandlerAdapter());
		acceptor.bind(endpoint);
	}

	public void setRegister(Register register) {
		this.register = register;
	}

	public Register getRegister() {
		return this.register;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return this.port;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return this.host;
	}
}
