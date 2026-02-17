package com.fanout.engine.transform;

import com.fanout.engine.model.Record;

public class XmlTransformer implements Transformer {

    @Override
    public String transform(Record record) {

        return "<record>"
                + "<id>" + record.getId() + "</id>"
                + "<payload>" + record.getPayload() + "</payload>"
                + "</record>";
    }
}
