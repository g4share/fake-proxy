package com.g4share.fakeproxy.helper;

import com.g4share.fakeproxy.exception.InvalidNumberValueException;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;

import java.net.URI;
import java.util.function.Consumer;

public final class Utils {
    private Utils() { }

    public static String extractHost(final HttpRequest req) {
        String host = req.headers().get(HttpHeaderNames.HOST);
        if (host == null) {
            try {
                URI u = URI.create(req.uri());
                host = u.getHost();
            } catch (Exception ignored) {
            }
        }

        return host == null ? "" : host.toLowerCase();
    }

    public static void safeSetNumber(final String value, final String propertyName, final String source,
                                     final Consumer<Integer> setter) {
        if (value == null) {
            return;
        }

        int transformed;
        try {
            transformed = Integer.parseInt(value);
        }
        catch (Exception e) {
            throw new InvalidNumberValueException(propertyName, value, source);
        }
        setter.accept(transformed);
    }
}
