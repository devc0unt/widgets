package com.miro.dev.widgets.exceptions;

public class InvalidWidgetException extends RuntimeException {
    public InvalidWidgetException() {
        super("Invalid widget params!");
    }
}
