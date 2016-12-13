package org.pretent.mrpc.client;

import java.lang.reflect.Proxy;

import org.pretent.mrpc.RegisterConfig;
import org.pretent.mrpc.proxy.MinaInvocationHandler;
import org.pretent.mrpc.proxy.SocketInvocationHandler;

public class ProxyFactory {

    public static <T> T getSocketService(Class<T> clazz) throws Exception {
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("not a interface");
        }
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
                new SocketInvocationHandler(clazz));
    }

    public static <T> T getService(Class<T> clazz) throws Exception {
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("not a interface");
        }
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
                new MinaInvocationHandler(clazz));
    }

    public static <T> T getService(Class<T> clazz, RegisterConfig registerConfig) throws Exception {
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("not a interface");
        }
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
                new MinaInvocationHandler(clazz, registerConfig));
    }
}
