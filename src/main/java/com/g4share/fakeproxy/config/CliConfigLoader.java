package com.g4share.fakeproxy.config;

import com.g4share.fakeproxy.model.Filter;
import com.g4share.fakeproxy.model.Proxy;
import com.g4share.fakeproxy.model.UpdatedData;
import com.g4share.fakeproxy.proxy.AppConfig;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.TreeMap;

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

        List<String> logHeaders = cliConfig.getOptions("logHeader");
        if (logHeaders != null) {
            if (appConfig.getFilter() == null) {
                appConfig.setFilter(new Filter());
            }
            appConfig.getFilter().setLogHeaders(logHeaders);
        }
    }

    public UpdatedData getUpdatedData(final CliConfig cliConfig) throws IOException {
        String url = cliConfig.getOption("url");
        List<String> setHeaders = cliConfig.getOptions("setHeader");
        String body = cliConfig.getOption("body");

        if (url == null && setHeaders == null && body == null) {
            return null;
        }

        return new UpdatedData(url, setHeaders(setHeaders), body(body));
    }

    private TreeMap<String, String> setHeaders(List<String> setHeaders) {
        if (setHeaders == null || setHeaders.isEmpty()) {
            return null;
        }
        TreeMap<String, String> map = new TreeMap<>();
        for (String setHeader : setHeaders) {
            int i = setHeader.indexOf('=');
            if (i > 0) {
                map.put(setHeader.substring(0, i).trim(), setHeader.substring(i + 1).trim());
            }
            else {
                map.put(setHeader.trim(), null);
            }
        }
        return map;
    }

    private byte[] body(String body) throws IOException {
        if (body == null || !body.startsWith("file://")) {
            return body == null ? null : body.getBytes(StandardCharsets.UTF_8);
        }

        File f = new File(body.substring("file://".length()));
        return Files.readAllBytes(f.toPath());
    }
}