package com.mobvcasting.mjpegparser;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import logging.Logger;

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
		Logger.log("Requesting Host  : " + getRequestingHost());
		Logger.log("Requesting Port  : " + getRequestingPort());
		Logger.log("Requesting Prompt : " + getRequestingPrompt());
		Logger.log("Requesting Protocol: "
				+ getRequestingProtocol());
		Logger.log("Requesting Scheme : " + getRequestingScheme());
		Logger.log("Requesting Site  : " + getRequestingSite());
		return new PasswordAuthentication(username, password.toCharArray());
	}
}