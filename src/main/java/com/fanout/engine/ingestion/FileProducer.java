package com.fanout.engine.ingestion;

import com.fanout.engine.model.Record;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.BlockingQueue;

public class FileProducer {

    private final RecordParser parser = new RecordParser();

    /**
     * Reads file line-by-line and pushes records to queue.
     */
    public void produce(String filePath,
                        BlockingQueue<Record> queue) {

        System.out.println("Starting file ingestion: " + filePath);

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                Record record = parser.parse(line);

                if (record != null) {

                    // BlockingQueue gives automatic backpressure
                    queue.put(record);
                }
            }

            System.out.println("File ingestion completed.");

            // signal end of stream
            queue.put(Record.POISON_PILL);

        } catch (Exception e) {
            throw new RuntimeException("Error reading file", e);
        }
    }
}
