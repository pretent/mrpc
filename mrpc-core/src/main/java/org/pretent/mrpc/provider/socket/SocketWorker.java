package org.pretent.mrpc.provider.socket;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.Socket;

import org.pretent.mrpc.Provider;
import org.pretent.mrpc.message.ObjectMessage;
import org.pretent.mrpc.message.request.HeaderMessage;

public class SocketWorker implements Runnable {

	Socket socket = null;

	public SocketWorker(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		InputStream in;
		try {
			in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			Object recieve = readObject(in);
			if (recieve instanceof HeaderMessage) {
				HeaderMessage message = (HeaderMessage) recieve;
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
				writeObject(out, retMessage);
			} else {
				throw new Exception("protocol error");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Object readObject(InputStream in) throws Exception {
		ObjectInputStream ois = new ObjectInputStream(in);
		return ois.readObject();
	}

	/**
	 * @param out
	 * @param obj
	 * @throws Exception
	 */
	private void writeObject(OutputStream out, Serializable obj) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(obj);
	}

}
