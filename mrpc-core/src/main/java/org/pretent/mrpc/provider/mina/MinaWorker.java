package org.pretent.mrpc.provider.mina;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import org.pretent.mrpc.Provider;
import org.pretent.mrpc.message.Message;
import org.pretent.mrpc.message.ObjectMessage;
import org.pretent.mrpc.message.request.HeaderMessage;

public class MinaWorker {

	private static final Logger LOGGER = Logger.getLogger(MinaWorker.class);

	public void invoke(IoSession session, Message recevie) {
		try {
			HeaderMessage message = (HeaderMessage) recevie;
			if (!Provider.ALL_OBJECT.containsKey(message.getClassName())) {
				throw new Exception("service is not available.");
			}
			Class clazz = Class.forName(message.getClassName());
			if (!clazz.isInterface()) {
				throw new Exception("not a interface.");
			}
			Class[] parameterTypes = null;
			if (message.getArguments() != null && message.getArguments().length > 0) {
				parameterTypes = new Class[message.getArguments().length];
				int i = 0;
				for (Object obj : message.getArguments()) {
					parameterTypes[i++] = obj.getClass();
				}

			}
			Method method = clazz.getMethod(message.getMethod(), parameterTypes);
			Object target = Provider.ALL_OBJECT.get(message.getClassName());
			Object retval = method.invoke(target, message.getArguments());
			ObjectMessage retMessage = new ObjectMessage();
			retMessage.setContent(retval);
			session.write(retMessage);
			session.close(true);
		} catch (Exception e) {
			LOGGER.error(e.getStackTrace());
		}
	}
}
