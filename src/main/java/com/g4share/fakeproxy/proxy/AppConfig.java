package com.g4share.fakeproxy.proxy;

import com.g4share.fakeproxy.model.Filter;
import com.g4share.fakeproxy.model.Proxy;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AppConfig {
    private int port;
    private Proxy proxy;
    private Filter filter;
}