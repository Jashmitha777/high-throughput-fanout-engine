package com.fanout.engine.ingestion;

import com.fanout.engine.model.Record;

import java.util.concurrent.atomic.AtomicLong;

public class RecordParser {

    private final AtomicLong idGenerator = new AtomicLong(0);

    /**
     * Parses one line into a Record object.
     * Currently supports simple JSONL / raw line input.
     */
    public Record parse(String line) {

        if (line == null || line.isBlank()) {
            return null;
        }

        String id = String.valueOf(idGenerator.incrementAndGet());

        return new Record(id, line);
    }
}
