package com.fanout.engine.config;

import java.util.Map;

public class AppConfig {

    private String inputFile;
    private int queueSize;
    private Map<String, SinkConfig> sinks;

    // ===== Getters & Setters =====

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public Map<String, SinkConfig> getSinks() {
        return sinks;
    }

    public void setSinks(Map<String, SinkConfig> sinks) {
        this.sinks = sinks;
    }

    // ===== Inner Class for Sink Config =====

    public static class SinkConfig {
        private int rateLimit;

        public int getRateLimit() {
            return rateLimit;
        }

        public void setRateLimit(int rateLimit) {
            this.rateLimit = rateLimit;
        }
    }
}
