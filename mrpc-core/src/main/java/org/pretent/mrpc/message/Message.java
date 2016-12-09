package org.pretent.mrpc.message;

import java.io.Serializable;

public interface Message extends Serializable{

	MessageType getType();

	Object getContent();

}
