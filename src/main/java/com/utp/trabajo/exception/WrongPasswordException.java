package com.utp.trabajo.exception;

public class WrongPasswordException extends Exception {
	
	public WrongPasswordException() {
		super();
	}
	
	public WrongPasswordException(String message) {
		super(message);
	}
	
}
