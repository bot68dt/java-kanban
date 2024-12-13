package com.yandex.taskmanager.exceptions;

import java.io.IOException;

public class TimeCrossingException extends IOException {
    public TimeCrossingException(final String message) {
        super(message);
    }
}
