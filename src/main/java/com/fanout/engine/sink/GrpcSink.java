package com.fanout.engine.sink;

import com.fanout.engine.model.Record;
import com.fanout.engine.throttling.RateLimiter;
import com.fanout.engine.transform.ProtoTransformer;
import com.fanout.engine.transform.Transformer;

public class GrpcSink implements Sink {

    private final Transformer transformer;
    private final RateLimiter rateLimiter;

    public GrpcSink(int rateLimit) {
        this.transformer = new ProtoTransformer();
        this.rateLimiter = new RateLimiter(rateLimit);
    }

    @Override
    public void send(Record record) {

        rateLimiter.acquire();

        String payload = transformer.transform(record);

        // Mock gRPC call
        System.out.println("[GRPC] STREAM -> " + payload);
    }

    @Override
    public String name() {
        return "GRPC";
    }
}
