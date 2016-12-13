package org.pretent.mrpc.proxy;

import org.apache.log4j.Logger;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.pretent.mrpc.RegisterConfig;
import org.pretent.mrpc.ServerConfig;
import org.pretent.mrpc.message.ObjectMessage;
import org.pretent.mrpc.message.request.HeaderMessage;
import org.pretent.mrpc.provider.Service;
import org.pretent.mrpc.provider.ServiceFactory;
import org.pretent.mrpc.register.ProtocolType;
import org.pretent.mrpc.register.redis.RedisServiceFactory;
import org.pretent.mrpc.register.zk.ZkServiceFactory;
import org.pretent.mrpc.util.ProtocolUtils;
import org.pretent.mrpc.util.ResourcesFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * @author pretent
 */
public class MinaInvocationHandler<T> implements InvocationHandler {

    private static final Logger LOGGER = Logger.getLogger(MinaInvocationHandler.class);

    private Class<T> clazz;

    private NioSocketConnector connector;

    private IoSession session;

    private ServiceFactory serviceFactory;

    private RegisterConfig registerConfig;

    private int timeout;

    public MinaInvocationHandler(Class<T> clazz) throws Exception {
        this.clazz = clazz;
        serviceFactory = getServiceFactory();
        init();
    }

    public MinaInvocationHandler(Class<T> clazz, RegisterConfig registerConfig) throws Exception {
        this.clazz = clazz;
        this.registerConfig = registerConfig;
        serviceFactory = getServiceFactory();
        init();
    }


    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object object = null;
        try {
            object = call(method, args);
            if (session != null) {
                session.getService().dispose();
            }
        } catch (Exception e) {
            if (session != null) {
                session.getService().dispose();
            }
            throw e;
        }
        return object;
    }

    private void init() throws Exception {
        connector = new NioSocketConnector();
        this.timeout = ResourcesFactory.getInt(ServerConfig.KEY_STRING_CONSUMER_TIMEOUT) == null ? 1500
                : ResourcesFactory.getInt(ServerConfig.KEY_STRING_CONSUMER_TIMEOUT);
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
        connector.setConnectTimeoutMillis(timeout);
        chain.addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        SocketSessionConfig cfg = connector.getSessionConfig();
        cfg.setUseReadOperation(true);
    }

    private Object call(Method method, Object[] args) throws Exception {
        init();
        Service provider = null;
        if (registerConfig != null) {
            provider = serviceFactory.getService(clazz.getName(), registerConfig);
        } else {
            provider = serviceFactory.getService(clazz.getName());
        }
        LOGGER.debug("connection to " + provider.getIp() + ":" + provider.getPort() + "/" + clazz.getName());
        InetSocketAddress point = new InetSocketAddress(provider.getIp(), provider.getPort());
        connector.setDefaultRemoteAddress(point);
        // connectFuture = connector.connect(point).awaitUninterruptibly();
        session = connector.connect().awaitUninterruptibly().getSession();
        HeaderMessage request = new HeaderMessage(clazz.getName(), method.getName(), args);
        session.write(request);
        ReadFuture readFuture = session.read();
        if (readFuture.awaitUninterruptibly(timeout)) {
            session.close(true);
            return ((ObjectMessage) readFuture.getMessage()).getContent();
        } else {
            session.close(true);
            throw new Exception("read timeout.");
        }
    }

    public ServiceFactory getServiceFactory() throws Exception {
        if (serviceFactory != null) {
            return serviceFactory;
        }
        ProtocolType protocol = ProtocolUtils.getProtocol();
        LOGGER.info("protocol : " + protocol.name());
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
