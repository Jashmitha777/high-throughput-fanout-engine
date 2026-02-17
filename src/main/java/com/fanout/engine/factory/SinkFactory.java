package com.fanout.engine.factory;

import com.fanout.engine.config.AppConfig;
import com.fanout.engine.sink.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SinkFactory {

    /**
     * Creates all configured sinks using application config.
     */
    public static List<Sink> createSinks(AppConfig config) {

        List<Sink> sinks = new ArrayList<>();

        Map<String, AppConfig.SinkConfig> sinkConfigs =
                config.getSinks();

        // REST Sink
        if (sinkConfigs.containsKey("rest")) {
            int rate =
                    sinkConfigs.get("rest").getRateLimit();

            sinks.add(new RestSink(rate));
        }

        // gRPC Sink
        if (sinkConfigs.containsKey("grpc")) {
            int rate =
                    sinkConfigs.get("grpc").getRateLimit();

            sinks.add(new GrpcSink(rate));
        }

        // Message Queue Sink
        if (sinkConfigs.containsKey("mq")) {
            int rate =
                    sinkConfigs.get("mq").getRateLimit();

            sinks.add(new MessageQueueSink(rate));
        }

        // Wide Column DB Sink
        if (sinkConfigs.containsKey("db")) {
            int rate =
                    sinkConfigs.get("db").getRateLimit();

            sinks.add(new WideColumnDbSink(rate));
        }

        System.out.println("Loaded sinks: " + sinks.size());

        return sinks;
    }
}
