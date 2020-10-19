package com.miro.dev.widgets.exceptions;

public class WidgetNotFoundException extends RuntimeException {
    public WidgetNotFoundException() {
        super("Widget not found!");
    }
}
