package org.opentradingsolutions.log4fix.importer;

public class MessageDirectionException extends Exception {

	private final String line;

	public MessageDirectionException(String line) {
		super("Message directionality could not be determined. \"" + line
				+ "\"");
		this.line = line;
	}

}
