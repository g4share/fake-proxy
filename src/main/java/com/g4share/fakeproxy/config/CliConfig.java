package com.g4share.fakeproxy.config;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.List;

public class CliConfig {
    private final Options options = new Options();
    private CommandLine cmd;

    public CliConfig() {
        options.addOption(new Option(null, "port", true, "Listening port"));
        options.addOption(new Option(null, "proxyHost", true, "Proxy host"));
        options.addOption(new Option(null, "proxyPort", true, "Proxy port"));
        options.addOption(new Option(null, "yaml", true, "Path to YAML configuration file (e.g., config.yaml)"));

        Option urlPatternsOpt = new Option(null, "urlPatterns", true, "One or more URL patterns (multiple options)");
        urlPatternsOpt.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(urlPatternsOpt);

        Option headersOpt = new Option(null, "headers", true, "Headers to be logged (multiple options)");
        headersOpt.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(headersOpt);
    }

    public void parseArgs(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        this.cmd = parser.parse(options, args);
    }

    public String getOption(String name) {
        return cmd != null && cmd.hasOption(name) ? cmd.getOptionValue(name) : null;
    }

    public List<String> getOptions(String name) {
        return cmd != null && cmd.hasOption(name) ? List.of(cmd.getOptionValues(name)) : null;
    }
}