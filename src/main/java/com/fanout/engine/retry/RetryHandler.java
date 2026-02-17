package com.fanout.engine.retry;

public class RetryHandler {

    private static final int MAX_RETRIES = 3;

    /**
     * Executes a task with retry logic.
     */
    public static void execute(Runnable task) {

        int attempt = 0;

        while (attempt < MAX_RETRIES) {

            try {
                task.run();
                return; // success

            } catch (Exception e) {

                attempt++;

                System.out.println(
                        "Retry attempt " + attempt +
                                " failed: " + e.getMessage()
                );

                // small backoff before retry
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // all retries failed
        throw new RuntimeException("Max retries exceeded");
    }
}
