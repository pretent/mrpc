package org.pretent.rpc.message.request;

import org.pretent.mrpc.message.Message;
import org.pretent.mrpc.message.MessageType;

public class HeaderMessage implements Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String className;
	private String method;
	private Object[] arguments;

	public HeaderMessage() {
	}

	public HeaderMessage(String className, String method, Object[] arguments) {
		super();
		this.className = className;
		this.method = method;
		this.arguments = arguments;
	}

	public MessageType getType() {
		return MessageType.OBJECT;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	public Object getContent() {
		return this.className + ":" + this.method + ":" + this.arguments;
	}

}
