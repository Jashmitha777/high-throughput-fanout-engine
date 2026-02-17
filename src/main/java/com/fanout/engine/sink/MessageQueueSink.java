package com.fanout.engine.sink;

import com.fanout.engine.model.Record;
import com.fanout.engine.throttling.RateLimiter;
import com.fanout.engine.transform.Transformer;
import com.fanout.engine.transform.XmlTransformer;

public class MessageQueueSink implements Sink {

    private final Transformer transformer;
    private final RateLimiter rateLimiter;

    public MessageQueueSink(int rateLimit) {
        this.transformer = new XmlTransformer();
        this.rateLimiter = new RateLimiter(rateLimit);
    }

    @Override
    public void send(Record record) {

        rateLimiter.acquire();

        String payload = transformer.transform(record);

        // Mock message queue publish
        System.out.println("[MQ] PUBLISH -> " + payload);
    }

    @Override
    public String name() {
        return "MESSAGE_QUEUE";
    }
}
