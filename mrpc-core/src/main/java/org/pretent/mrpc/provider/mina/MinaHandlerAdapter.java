package org.pretent.mrpc.provider.mina;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.pretent.rpc.message.request.HeaderMessage;

/**
 * date: 16/12/7 22:35 author: PRETENT
 **/
public class MinaHandlerAdapter extends IoHandlerAdapter {

	private static final Logger LOGGER = Logger.getLogger(MinaHandlerAdapter.class);

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		LOGGER.debug("has a new client connected:" + session.getRemoteAddress().toString());
		if (!(message instanceof HeaderMessage)) {
			throw new Exception("protocol not support!");
		}
		HeaderMessage headerMessage = (HeaderMessage) message;
		new MinaWorker().invoke(session, headerMessage);
	}
}
