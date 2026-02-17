package com.fanout.engine.sink;

import com.fanout.engine.model.Record;
import com.fanout.engine.throttling.RateLimiter;
import com.fanout.engine.transform.JsonTransformer;
import com.fanout.engine.transform.Transformer;

public class RestSink implements Sink {

    private final Transformer transformer;
    private final RateLimiter rateLimiter;

    public RestSink(int rateLimit) {
        this.transformer = new JsonTransformer();
        this.rateLimiter = new RateLimiter(rateLimit);
    }

    @Override
    public void send(Record record) {

        rateLimiter.acquire();

        String payload = transformer.transform(record);

        // Mock REST call
        System.out.println("[REST] POST -> " + payload);
    }

    @Override
    public String name() {
        return "REST";
    }
}
