package com.github.terma.logb;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class JsonException {

    public final String exceptionClass;
    public final String message;
    public final String stacktrace;

    public JsonException(final Throwable e) {
        exceptionClass = e.getClass().getCanonicalName();
        message = e.getMessage();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(out);
        e.printStackTrace(ps);
        stacktrace = out.toString();
    }

}
