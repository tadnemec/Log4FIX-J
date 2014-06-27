package org.opentradingsolutions.log4fix.core;

public class MessageQueueItem implements MessageQueueItemConstants {

	public String rawMessage;
	public Direction direction;

	public MessageQueueItem() {
		rawMessage = null;
		direction = null;
	}

	public MessageQueueItem(String message, Direction direction) {
		rawMessage = message;
		this.direction = direction;
	}

	public String getRawMessage() {
		return rawMessage;
	}

	public Direction getDirection() {
		return direction;
	}
}
