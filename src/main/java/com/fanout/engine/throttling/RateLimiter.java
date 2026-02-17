package com.fanout.engine.throttling;

public class RateLimiter {

    private final long intervalNanos;
    private long nextAllowedTime;

    /**
     * @param permitsPerSecond allowed requests per second
     */
    public RateLimiter(int permitsPerSecond) {

        if (permitsPerSecond <= 0) {
            throw new IllegalArgumentException(
                    "Rate limit must be > 0");
        }

        // spacing between requests
        this.intervalNanos = 1_000_000_000L / permitsPerSecond;

        this.nextAllowedTime = System.nanoTime();
    }

    /**
     * Blocks if needed until next permit is available.
     */
    public synchronized void acquire() {

        long now = System.nanoTime();

        // wait if called too early
        if (now < nextAllowedTime) {

            long waitNanos = nextAllowedTime - now;

            try {
                long millis = waitNanos / 1_000_000;
                int nanos = (int)(waitNanos % 1_000_000);

                Thread.sleep(millis, nanos);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // schedule next slot
        nextAllowedTime = System.nanoTime() + intervalNanos;
    }
}
