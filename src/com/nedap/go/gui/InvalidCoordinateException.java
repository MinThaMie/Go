package com.nedap.go.gui;

/**
 * Created by daan.vanbeek on 15-12-16.
 */
public class InvalidCoordinateException extends Exception {
	public static final long serialVersionUID = 1;
	
    public InvalidCoordinateException(String message) {
        super(message);
    }
}
