package com.g4share.fakeproxy;

import com.g4share.fakeproxy.proxy.AppConfig;
import com.g4share.fakeproxy.config.AppConfigReader;
import com.g4share.fakeproxy.proxy.ChainedProxy;
import com.g4share.fakeproxy.proxy.FilterSource;
import lombok.extern.log4j.Log4j2;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.littleshoot.proxy.mitm.CertificateSniffingMitmManager;
import org.littleshoot.proxy.mitm.RootCertificateException;

@Log4j2
public class FakeProxyApplication {

    private HttpProxyServerBootstrap bootstrapProxy(final AppConfig appConfig) throws RootCertificateException {
        HttpProxyServerBootstrap bootstrap =
                DefaultHttpProxyServer.bootstrap()
                        .withPort(appConfig.getPort())
                        .withManInTheMiddle(new CertificateSniffingMitmManager())
                        .withFiltersSource(new FilterSource(appConfig.getFilter()));

        if (appConfig.getProxy() != null) {
            bootstrap.withChainProxyManager((req, chain, clientDetails) ->
                    chain.add(new ChainedProxy(appConfig.getProxy())));
        }
        return bootstrap;
    }

    public static void main(final String[] args) throws Exception {

        AppConfigReader configReader = new AppConfigReader();
        AppConfig appConfig = configReader.getAppConfig(args);

        FakeProxyApplication app = new FakeProxyApplication();
        var bootstrap = app.bootstrapProxy(appConfig);

        HttpProxyServer server = bootstrap.start();
        log.info("Proxy started on port {}", server.getListenAddress().getPort());

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}