package org.pretent.mrpc;

import java.lang.reflect.InvocationHandler;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public interface Handler {

	Map<String, InvocationHandler> ALL_HANDLER = new HashMap<String, InvocationHandler>();

	void handle(Socket socket) throws Exception;
}
