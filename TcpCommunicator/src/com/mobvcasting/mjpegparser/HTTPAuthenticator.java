package com.mobvcasting.mjpegparser;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * From http://www.walking-productions.com/notslop/2010/04/20/motion-jpeg-in-flash-and-java/
 */
public class HTTPAuthenticator extends Authenticator {
	private String username, password;

	public HTTPAuthenticator(String user, String pass) {
		username = user;
		password = pass;
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		System.out.println("Requesting Host  : " + getRequestingHost());
		System.out.println("Requesting Port  : " + getRequestingPort());
		System.out.println("Requesting Prompt : " + getRequestingPrompt());
		System.out.println("Requesting Protocol: "
				+ getRequestingProtocol());
		System.out.println("Requesting Scheme : " + getRequestingScheme());
		System.out.println("Requesting Site  : " + getRequestingSite());
		return new PasswordAuthentication(username, password.toCharArray());
	}
}