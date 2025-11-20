package com.g4share.fakeproxy.model;

import java.util.TreeMap;

public record UpdatedData(String url, TreeMap<String, String> headers, byte[] body) {
}
