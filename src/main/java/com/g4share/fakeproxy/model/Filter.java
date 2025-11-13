package com.g4share.fakeproxy.model;

import io.netty.handler.codec.http.HttpHeaders;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Setter
public class Filter {

    private List<String> urlPatterns;
    private List<String> filteredHeaders;

    public boolean isFiltered(final String url) {
        if (urlPatterns == null || urlPatterns.isEmpty()) {
            return true;
        }
        return urlPatterns.stream().anyMatch(url::matches);
    }

    public Map<String, String> filteredHeaders(final HttpHeaders headers) {
        Map<String, String> result = new LinkedHashMap<>();

        if (filteredHeaders == null || filteredHeaders.isEmpty()) {
            headers.forEach(h -> result.put(h.getKey(), h.getValue()));
            return result;
        }
        for (Map.Entry<String, String> entry : headers) {
            String name = entry.getKey();
            if (filteredHeaders.stream().anyMatch(f -> f.equalsIgnoreCase(name))) {
                result.put(name, entry.getValue());
            }
        }

        return result;
    }
}