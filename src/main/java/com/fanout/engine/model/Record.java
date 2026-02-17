package com.fanout.engine.model;

import java.time.Instant;
import java.util.UUID;

public class Record {

    private final String id;
    private final String payload;
    private final Instant createdAt;

    public static final Record POISON_PILL =
            new Record("POISON", "POISON");

    public Record(String id, String payload) {
        this.id = id;
        this.payload = payload;
        this.createdAt = Instant.now();
    }

    // Optional constructor for auto-generated IDs
    public Record(String payload) {
        this.id = UUID.randomUUID().toString();
        this.payload = payload;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id='" + id + '\'' +
                ", payload='" + payload + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
