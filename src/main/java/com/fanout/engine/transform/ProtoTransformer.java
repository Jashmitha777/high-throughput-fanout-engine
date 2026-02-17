package com.fanout.engine.transform;

import com.fanout.engine.model.Record;

public class ProtoTransformer implements Transformer {

    @Override
    public String transform(Record record) {

        // Mock protobuf representation
        return "PROTO{"
                + "id=" + record.getId()
                + ",data=" + record.getPayload()
                + "}";
    }
}
