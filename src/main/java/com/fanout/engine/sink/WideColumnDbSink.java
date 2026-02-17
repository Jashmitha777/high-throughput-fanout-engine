package com.fanout.engine.sink;

import com.fanout.engine.model.Record;
import com.fanout.engine.throttling.RateLimiter;
import com.fanout.engine.transform.AvroTransformer;
import com.fanout.engine.transform.Transformer;

public class WideColumnDbSink implements Sink {

    private final Transformer transformer;
    private final RateLimiter rateLimiter;

    public WideColumnDbSink(int rateLimit) {
        this.transformer = new AvroTransformer();
        this.rateLimiter = new RateLimiter(rateLimit);
    }

    @Override
    public void send(Record record) {

        rateLimiter.acquire();

        String payload = transformer.transform(record);

        // Mock async UPSERT
        System.out.println("[DB] UPSERT -> " + payload);
    }

    @Override
    public String name() {
        return "WIDE_COLUMN_DB";
    }
}
