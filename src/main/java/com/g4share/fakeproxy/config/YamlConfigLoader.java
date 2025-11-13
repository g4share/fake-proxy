package com.g4share.fakeproxy.config;

import com.g4share.fakeproxy.model.Filter;
import com.g4share.fakeproxy.model.Proxy;
import com.g4share.fakeproxy.proxy.AppConfig;

import java.util.List;

import static com.g4share.fakeproxy.helper.Utils.safeSetNumber;

public class YamlConfigLoader {

    public void load(final String configPath, final AppConfig appConfig) throws Exception {
        YamlPropertyReader yamlPropertyReader = new YamlPropertyReader();
        yamlPropertyReader.init(configPath);

        setListeningPort(yamlPropertyReader, appConfig);
        setProxy(yamlPropertyReader, appConfig);
        setFilters(yamlPropertyReader, appConfig);
    }

    private void setListeningPort(YamlPropertyReader yamlPropertyReader, AppConfig appConfig) {
        safeSetNumber(yamlPropertyReader.readValue("listeningPort"), "listeningPort", "yaml", appConfig::setPort);
    }

    private void setProxy(YamlPropertyReader yamlPropertyReader, AppConfig appConfig) {
        String host = yamlPropertyReader.readValue("proxy/host");
        if (host != null) {
            if (appConfig.getProxy() == null) {
                appConfig.setProxy(new Proxy());
            }
            appConfig.getProxy().setHost(host);
        }

        safeSetNumber(yamlPropertyReader.readValue("proxy/port"), "proxy/port", "yaml", port -> {
            if (appConfig.getProxy() == null) {
                appConfig.setProxy(new Proxy());
            }
            appConfig.getProxy().setPort(port);
        });
    }

    private void setFilters(YamlPropertyReader yamlPropertyReader, AppConfig appConfig) {
        Filter filter = appConfig.getFilter();

        List<String> urlPatterns = yamlPropertyReader.readValues("filter/urlPatterns");
        if (urlPatterns != null) {
            if (filter == null) {
                filter = new Filter();
            }
            filter.setUrlPatterns(urlPatterns);
        }

        List<String> filteredHeaders = yamlPropertyReader.readValues("filter/headers");
        if (filteredHeaders != null) {
            if (filter == null) {
                filter = new Filter();
            }
            filter.setFilteredHeaders(filteredHeaders);
        }

        appConfig.setFilter(filter);
    }
}