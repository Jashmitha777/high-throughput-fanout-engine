package com.fanout.engine.metrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MetricsCollector {

    // total processed records
    private final AtomicLong processed = new AtomicLong(0);

    // per-sink success/failure counters
    private final Map<String, AtomicLong> successPerSink =
            new ConcurrentHashMap<>();

    private final Map<String, AtomicLong> failurePerSink =
            new ConcurrentHashMap<>();

    // start time for throughput calculation
    private final long startTime = System.currentTimeMillis();

    /**
     * Increment total processed records
     */
    public void incrementProcessed() {
        processed.incrementAndGet();
    }

    /**
     * Increment success counter for a sink
     */
    public void incrementSuccess(String sinkName) {
        successPerSink
                .computeIfAbsent(sinkName, k -> new AtomicLong(0))
                .incrementAndGet();
    }

    /**
     * Increment failure counter for a sink
     */
    public void incrementFailure(String sinkName) {
        failurePerSink
                .computeIfAbsent(sinkName, k -> new AtomicLong(0))
                .incrementAndGet();
    }

    /**
     * Starts periodic metrics printing every 5 seconds.
     */
    public void startPrinter() {

        Thread.startVirtualThread(() -> {

            while (true) {

                try {
                    Thread.sleep(5000);
                    printMetrics();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    /**
     * Prints current metrics snapshot.
     */
    private void printMetrics() {

        long totalProcessed = processed.get();

        long elapsedSeconds =
                (System.currentTimeMillis() - startTime) / 1000;

        long throughput =
                elapsedSeconds == 0 ? 0 : totalProcessed / elapsedSeconds;

        System.out.println("\n========== METRICS ==========");
        System.out.println("Records Processed : " + totalProcessed);
        System.out.println("Throughput        : " + throughput + " records/sec");

        System.out.println("\n--- Success Per Sink ---");
        successPerSink.forEach((sink, count) ->
                System.out.println(sink + " : " + count.get()));

        System.out.println("\n--- Failure Per Sink ---");
        failurePerSink.forEach((sink, count) ->
                System.out.println(sink + " : " + count.get()));

        System.out.println("============================\n");
    }

    public void printFinalMetrics() {
        printMetrics();
    }

}
