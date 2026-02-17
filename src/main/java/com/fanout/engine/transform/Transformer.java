package com.fanout.engine.transform;

import com.fanout.engine.model.Record;

public interface Transformer {

    /**
     * Convert a Record into sink-specific format.
     */
    String transform(Record record);
}
