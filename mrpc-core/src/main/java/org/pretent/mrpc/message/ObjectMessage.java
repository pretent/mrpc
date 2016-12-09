package org.pretent.mrpc.message;

public class ObjectMessage implements Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object content;

	public MessageType getType() {
		return MessageType.OBJECT;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
