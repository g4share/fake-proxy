package com.g4share.fakeproxy.config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class YamlPropertyReader{

    private Map<String, Object> config;

    public void init(final String configPath) throws Exception {
        final Yaml yaml = new Yaml();
        try (InputStream in = loadStream(configPath)) {
            config = yaml.load(in);
        }
    }

    private InputStream loadStream(final String path) throws IOException {
        if (path.startsWith("file://")) {
            File f = new File(path.substring("file://".length()));
            return Files.newInputStream(f.toPath());
        }
        if (path.startsWith("classpath://")) {
            String resource = path.substring("classpath://".length());
            return getClass().getClassLoader().getResourceAsStream(resource);
        }

        throw new IllegalArgumentException("Invalid config path (must start with file: or classpath: ): " + path);
    }

    public String readValue(final String path) {
        Map<String, Object> obj = config;
        String[] split = path.split("/");
        for (int i = 0; i < split.length; i++) {
            if (i == split.length - 1) {
                Object partial = obj.get(split[i]);
                return partial == null ? null : partial.toString();
            }
            obj = (Map<String, Object>) obj.get(split[i]);
            if (obj == null) {
                return null;
            }
        }
        throw new RuntimeException("No value found for path: " + path);
    }

    public List<String> readValues(final String path) {
        Map<String, Object> obj = config;
        String[] split = path.split("/");
        for (int i = 0; i < split.length; i++) {
            if (i == split.length - 1) {
                return (List<String>) obj.get(split[i]);
            }
            obj = (Map<String, Object>) obj.get(split[i]);
            if (obj == null) {
                return null;
            }
        }
        throw new RuntimeException("No value found for path: " + path);
    }
}
