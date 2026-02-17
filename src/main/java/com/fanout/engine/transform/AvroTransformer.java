package com.fanout.engine.transform;

import com.fanout.engine.model.Record;

public class AvroTransformer implements Transformer {

    @Override
    public String transform(Record record) {

        // Mock key-value map representation
        return "AVRO_MAP{"
                + "id:" + record.getId()
                + ",payload:" + record.getPayload()
                + "}";
    }
}
