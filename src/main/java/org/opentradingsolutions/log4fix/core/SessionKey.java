package org.opentradingsolutions.log4fix.core;

import quickfix.SessionID;

public class SessionKey {
	private final String version;
	private final String sender;
	private final String target;

	public SessionKey() {
		version = null;
		sender = null;
		target = null;
	}

	public SessionKey(String version, String sender, String target) {
		this.version = version;
		this.sender = sender;
		this.target = target;
	}

	public SessionKey(SessionID sessionId) {
		version = sessionId.getBeginString();
		sender = sessionId.getSenderCompID();
		target = sessionId.getTargetCompID();
	}

	public String getVersion() {
		return version;
	}

	public String getSender() {
		return sender;
	}

	public String getTarget() {
		return target;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
		result = result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SessionKey) {
			if (((SessionKey) other).getVersion().equals(this.getVersion())
					&& (((SessionKey) other).getTarget().equals(
							this.getTarget())
							&& ((SessionKey) other).getSender().equals(
									this.getSender()) || ((SessionKey) other)
							.getTarget().equals(this.getSender())
							&& ((SessionKey) other).getSender().equals(
									this.getTarget()))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public String toString() {
		return (this != null) ? this.getVersion() + ":" + this.getSender()
				+ "->" + this.getTarget() : "null";
	}

}
