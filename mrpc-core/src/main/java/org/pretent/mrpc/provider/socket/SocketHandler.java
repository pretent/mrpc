package org.pretent.mrpc.provider.socket;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.pretent.mrpc.Handler;

public class SocketHandler implements Handler {

	private static final Logger LOGGER = Logger.getLogger(SocketHandler.class);
	private static ExecutorService executorService = null;

	static {
		LOGGER.debug("threadPool created.");
		executorService = Executors.newCachedThreadPool();
	}

	public void handle(Socket socket) throws Exception {
		executorService.execute(new SocketWorker(socket));
	}
}
