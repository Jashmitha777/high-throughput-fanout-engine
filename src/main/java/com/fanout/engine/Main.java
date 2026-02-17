package com.fanout.engine;

import com.fanout.engine.config.AppConfig;
import com.fanout.engine.config.ConfigLoader;
import com.fanout.engine.factory.SinkFactory;
import com.fanout.engine.ingestion.FileProducer;
import com.fanout.engine.metrics.MetricsCollector;
import com.fanout.engine.model.Record;
import com.fanout.engine.orchestrator.FanOutOrchestrator;
import com.fanout.engine.sink.Sink;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    public static void main(String[] args) {

        try {

            System.out.println("Starting High-Throughput Fan-Out Engine...");

            // ==============================
            // 1. Load Configuration
            // ==============================
            AppConfig config =
                    ConfigLoader.load("application.yaml");

            // ==============================
            // 2. Create Shared Queue
            // (Backpressure handled here)
            // ==============================
            BlockingQueue<Record> queue =
                    new ArrayBlockingQueue<>(config.getQueueSize());

            // ==============================
            // 3. Create Metrics Collector
            // ==============================
            MetricsCollector metrics = new MetricsCollector();
            metrics.startPrinter();

            // ==============================
            // 4. Create Sinks using Factory
            // ==============================
            List<Sink> sinks =
                    SinkFactory.createSinks(config);

            // ==============================
            // 5. Start File Producer
            // (runs on virtual thread)
            // ==============================
            Thread.startVirtualThread(() -> {
                new FileProducer()
                        .produce(config.getInputFile(), queue);
            });

            // ==============================
            // 6. Start Orchestrator
            // (main processing engine)
            // ==============================
            FanOutOrchestrator orchestrator =
                    new FanOutOrchestrator();

            orchestrator.start(queue, sinks, metrics);

        } catch (Exception e) {

            System.err.println("Application failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}