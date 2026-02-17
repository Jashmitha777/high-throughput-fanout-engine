package com.fanout.engine.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class ConfigLoader {

    public static AppConfig load(String fileName) {

        Yaml yaml = new Yaml();

        try (InputStream input =
                     ConfigLoader.class.getClassLoader()
                             .getResourceAsStream(fileName)) {

            if (input == null) {
                throw new RuntimeException("Config file not found: " + fileName);
            }

            Map<String, Object> raw = yaml.load(input);

            AppConfig config = new AppConfig();

            // ===== basic fields =====
            config.setInputFile((String) raw.get("inputFile"));
            config.setQueueSize((Integer) raw.get("queueSize"));

            // ===== sinks =====
            Map<String, Map<String, Object>> sinkMap =
                    (Map<String, Map<String, Object>>) raw.get("sinks");

            Map<String, AppConfig.SinkConfig> parsedSinks =
                    new java.util.HashMap<>();

            for (String key : sinkMap.keySet()) {

                AppConfig.SinkConfig sinkConfig =
                        new AppConfig.SinkConfig();

                sinkConfig.setRateLimit(
                        (Integer) sinkMap.get(key).get("rateLimit")
                );

                parsedSinks.put(key, sinkConfig);
            }

            config.setSinks(parsedSinks);

            return config;

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }
}
