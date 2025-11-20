package com.g4share.fakeproxy.config;

import com.g4share.fakeproxy.model.UpdatedData;
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

        CliConfigLoader cliConfigLoader = new CliConfigLoader();
        cliConfigLoader.load(cliConfig, appConfig);

        return appConfig;
    }

    public UpdatedData getUpdatedData(final String[] args) throws Exception {
        CliConfig cliConfig = new CliConfig();
        cliConfig.parseArgs(args);
        CliConfigLoader cliConfigLoader = new CliConfigLoader();
        return cliConfigLoader.getUpdatedData(cliConfig);
    }
}