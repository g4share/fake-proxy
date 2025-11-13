package com.g4share.fakeproxy.config;

import com.g4share.fakeproxy.proxy.AppConfig;

public class AppConfigReader {

    public AppConfig getAppConfig(final String[] args) throws Exception {

        AppConfig appConfig = new AppConfig();

        YamlConfigLoader yamlConfigLoader = new YamlConfigLoader();
        yamlConfigLoader.load("classpath://application.yaml", appConfig);

        CliConfig cliConfig = new CliConfig();
        cliConfig.parseArgs(args);

        String externalYaml = cliConfig.getOption("yaml");
        if (externalYaml != null) {
            yamlConfigLoader.load("file://" + yamlConfigLoader, appConfig);
        }

        CliConfigLoader cmdConfigLoader = new CliConfigLoader();
        cmdConfigLoader.load(cliConfig, appConfig);

        return appConfig;
    }
}