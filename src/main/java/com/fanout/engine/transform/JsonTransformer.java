package com.fanout.engine.transform;

import com.fanout.engine.model.Record;

public class JsonTransformer implements Transformer {

    @Override
    public String transform(Record record) {

        return "{"
                + "\"id\":\"" + record.getId() + "\","
                + "\"payload\":" + record.getPayload()
                + "}";
    }
}
