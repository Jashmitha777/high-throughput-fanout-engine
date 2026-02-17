package com.fanout.engine.sink;

import com.fanout.engine.model.Record;

public interface Sink {

    /**
     * Sends record to destination.
     */
    void send(Record record);

    /**
     * Sink name (REST / GRPC etc.)
     */
    String name();
}
