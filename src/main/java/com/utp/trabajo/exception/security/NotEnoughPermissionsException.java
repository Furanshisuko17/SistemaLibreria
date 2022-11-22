package com.utp.trabajo.exception.security;

public class NotEnoughPermissionsException extends Exception {

    public NotEnoughPermissionsException() {
        super();
    }

    public NotEnoughPermissionsException(String message) {
        super(message);
    }

}
