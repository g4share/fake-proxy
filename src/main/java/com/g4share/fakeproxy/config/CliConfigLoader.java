package com.g4share.fakeproxy.config;

import com.g4share.fakeproxy.model.Filter;
import com.g4share.fakeproxy.model.Proxy;
import com.g4share.fakeproxy.proxy.AppConfig;

import java.util.List;

import static com.g4share.fakeproxy.helper.Utils.safeSetNumber;


public class CliConfigLoader {

    public void load(final CliConfig cliConfig, final AppConfig appConfig) {
        safeSetNumber(cliConfig.getOption("port"), "port", "cli", appConfig::setPort);

        String proxyHost = cliConfig.getOption("proxyHost");
        if (proxyHost != null) {
            if (appConfig.getProxy() == null) {
                appConfig.setProxy(new Proxy());
            }
            appConfig.getProxy().setHost(proxyHost);
        }

        safeSetNumber(cliConfig.getOption("proxyPort"), "proxyPort", "cli", port -> {
            if (appConfig.getProxy() == null) {
                appConfig.setProxy(new Proxy());
            }
            appConfig.getProxy().setPort(port);
        });

        List<String> urlPatterns = cliConfig.getOptions("urlPatterns");
        if (urlPatterns != null) {
            if (appConfig.getFilter() == null) {
                appConfig.setFilter(new Filter());
            }
            appConfig.getFilter().setUrlPatterns(urlPatterns);
        }

        List<String> headers = cliConfig.getOptions("headers");
        if (headers != null) {
            if (appConfig.getFilter() == null) {
                appConfig.setFilter(new Filter());
            }
            appConfig.getFilter().setFilteredHeaders(headers);
        }
    }
}