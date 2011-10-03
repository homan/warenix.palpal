package org.dyndns.warenix.palpal.social.facebook;

@SuppressWarnings("serial")
public class FacebookException extends Exception {
	public String type;
	public String error;

	public FacebookException(String type, String error) {
		super();

		this.type = type;
		this.error = error;
	}

	public String toString() {
		return String.format("%s\n%s", type, error);
	}
}
