package com.g4share.fakeproxy.exception;

public class InvalidNumberValueException extends NumberFormatException {

    public InvalidNumberValueException(String parameterName, String invalidValue, String source) {
        super(String.format(
                "Invalid numeric value '%s' for parameter '%s'%s",
                invalidValue,
                parameterName,
                source != null ? " (source: " + source + ")" : ""
        ));
    }
}