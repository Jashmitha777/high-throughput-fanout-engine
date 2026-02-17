package com.fanout.engine.orchestrator;

import com.fanout.engine.metrics.MetricsCollector;
import com.fanout.engine.model.Record;
import com.fanout.engine.retry.RetryHandler;
import com.fanout.engine.sink.Sink;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FanOutOrchestrator {

    private final ExecutorService executor;

    public FanOutOrchestrator() {
        // Virtual threads → lightweight high concurrency
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * Starts consuming records and dispatching to sinks.
     */
    public void start(
            BlockingQueue<Record> queue,
            List<Sink> sinks,
            MetricsCollector metrics) {

        System.out.println("FanOut Orchestrator started...");

        boolean running = true;

        while (running) {

            try {

                // waits if queue is empty (backpressure-safe)
                Record record = queue.take();

                // ===== poison pill handling =====
                if (record == Record.POISON_PILL) {
                    System.out.println("Received poison pill.");
                    running = false;
                    continue; // do not submit new tasks
                }

                // ===== fan-out to all sinks =====
                for (Sink sink : sinks) {

                    executor.submit(() -> {

                        try {

                            RetryHandler.execute(() ->
                                    sink.send(record)
                            );

                            metrics.incrementSuccess(sink.name());

                        } catch (Exception e) {

                            metrics.incrementFailure(sink.name());
                        }
                    });
                }

                metrics.incrementProcessed();

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                running = false;
            }
        }

        // ===== graceful shutdown =====
        System.out.println("Waiting for tasks to finish...");

        executor.shutdown();

        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        metrics.printFinalMetrics();

        System.out.println("Orchestrator stopped.");
    }
}
