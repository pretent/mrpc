package org.pretent.mrpc.message;

public class TextMessage implements Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String content;

	public MessageType getType() {
		return MessageType.TEXT;
	}

	public Object getContent() {
		return content;
	}

}
