package com.g4share.fakeproxy.proxy;

import com.g4share.fakeproxy.model.Proxy;
import org.littleshoot.proxy.ChainedProxyAdapter;

import java.net.InetSocketAddress;

public class ChainedProxy extends ChainedProxyAdapter {

    private final InetSocketAddress inetSocketAddress;

    public ChainedProxy(final Proxy proxy) {
        inetSocketAddress = new InetSocketAddress(proxy.getHost(), proxy.getPort());
    }

    @Override
    public InetSocketAddress getChainedProxyAddress() {
        return inetSocketAddress;
    }
}
