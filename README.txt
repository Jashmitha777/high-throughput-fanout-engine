HIGH-THROUGHPUT FAN-OUT ENGINE
=============================

Author: [Jashmitha Natva]  
Language: Java 21  
Concurrency: Virtual Threads (Project Loom)  
Build Tool: Maven  
Configuration: YAML  


------------------------------------------------------------
1. PROJECT OVERVIEW
------------------------------------------------------------

This project implements a High-Throughput Data Fan-Out &
Transformation Engine in Java.

The engine reads records from a flat-file source and distributes
each record to multiple downstream sinks while applying
sink-specific transformations.

Goals:

- Stream very large files safely
- Fan-out data to multiple systems
- Prevent downstream overload
- Support parallel execution
- Provide observability metrics
- Maintain clean extensible architecture


------------------------------------------------------------
2. CORE IDEA
------------------------------------------------------------

The system follows:

    Producer → Queue → Orchestrator → Multiple Sinks

Each record:

1. Is read from the input file (streaming).
2. Converted into a Record object.
3. Inserted into a BlockingQueue.
4. Consumed by the orchestrator.
5. Sent in parallel to all sinks.
6. Transformed according to sink format.

Fan-out Example:

        Record
           ↓
     REST Sink
     gRPC Sink
     MQ Sink
     DB Sink


------------------------------------------------------------
3. ARCHITECTURE DIAGRAM
------------------------------------------------------------

┌─────────────────────────────────────────────┐
│                 INPUT FILE                   │
│          (JSONL / CSV / Flat File)           │
└──────────────────────────┬───────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────┐
│               FILE PRODUCER                  │
│        (Streaming Line-by-Line Reader)       │
└──────────────────────────┬───────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────┐
│            BLOCKING QUEUE (BUFFER)           │
│          Backpressure + Memory Safety         │
└──────────────────────────┬───────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────┐
│             FANOUT ORCHESTRATOR              │
│     Virtual Threads + Parallel Dispatch       │
└───────────────┬──────────┬──────────┬────────┘
                │          │          │
                ▼          ▼          ▼
        ┌──────────┐ ┌──────────┐ ┌──────────┐
        │ REST     │ │ gRPC     │ │ MQ       │
        │ Sink     │ │ Sink     │ │ Sink     │
        └──────────┘ └──────────┘ └──────────┘
                │
                ▼
        ┌────────────────────┐
        │ Wide Column DB Sink │
        └────────────────────┘

Each Sink:
- Own Transformer (Strategy Pattern)
- Own RateLimiter
- Retry Handler


------------------------------------------------------------
4. INGESTION LAYER
------------------------------------------------------------

- FileProducer reads input using BufferedReader.
- Processing is line-by-line.
- Entire file is never loaded into memory.
- Supports very large file sizes (100GB+).


------------------------------------------------------------
5. TRANSFORMATION LAYER (STRATEGY PATTERN)
------------------------------------------------------------

Transformer interface:

    Transformer
        ├── JsonTransformer
        ├── ProtoTransformer
        ├── XmlTransformer
        └── AvroTransformer

Mappings:

- Source → REST → JSON
- Source → gRPC → Protobuf-style
- Source → MQ → XML
- Source → Wide DB → Avro/Map

Transformation logic is isolated from sink logic.


------------------------------------------------------------
6. DISTRIBUTION LAYER (MOCK SINKS)
------------------------------------------------------------

Implemented sinks:

1. REST Sink
   - Simulates HTTP POST

2. gRPC Sink
   - Simulates streaming execution

3. Message Queue Sink
   - Simulates topic publishing

4. Wide Column DB Sink
   - Simulates async UPSERT


------------------------------------------------------------
7. CONCURRENCY MODEL
------------------------------------------------------------

Concurrency uses Java 21 Virtual Threads:

    Executors.newVirtualThreadPerTaskExecutor()

Each sink execution runs independently:

    executor.submit(() -> sink.send(record));

Benefits:

- Lightweight threads
- Massive concurrency support
- Simple task-per-record model
- Linear scalability with CPU cores


------------------------------------------------------------
8. RATE LIMITING (THROTTLING)
------------------------------------------------------------

Each sink has configurable rate limits.

Example:

    REST → 50 req/sec
    gRPC → 100 req/sec
    MQ   → 200 req/sec
    DB   → 500 req/sec

Implemented via custom RateLimiter.


------------------------------------------------------------
9. RETRY & RESILIENCE
------------------------------------------------------------

RetryHandler provides:

- Maximum 3 retries
- Small retry backoff
- Centralized failure logic
- Failure tracking via metrics

Failures are counted to simulate DLQ behavior.


------------------------------------------------------------
10. BACKPRESSURE MECHANISM
------------------------------------------------------------

Backpressure is implemented using:

    ArrayBlockingQueue<Record>

Behavior:

- Producer inserts records into queue.
- If sinks are slow → queue fills.
- Producer blocks automatically.
- Memory usage remains stable.

This prevents:

- Unbounded memory growth
- OutOfMemory errors


------------------------------------------------------------
11. GRACEFUL SHUTDOWN (POISON PILL)
------------------------------------------------------------

Shutdown flow:

1. FileProducer finishes reading.
2. Pushes POISON_PILL into queue.
3. Orchestrator detects stop signal.
4. Stops consuming new records.
5. Waits for running tasks.
6. Executor shuts down safely.


------------------------------------------------------------
12. OBSERVABILITY (METRICS)
------------------------------------------------------------

Metrics collected:

- Records processed
- Throughput (records/sec)
- Success count per sink
- Failure count per sink

Example:

========== METRICS ==========
Records Processed : 60
Throughput        : 60 records/sec

--- Success Per Sink ---
REST : 60
GRPC : 60
MESSAGE_QUEUE : 60
WIDE_COLUMN_DB : 60


------------------------------------------------------------
13. DESIGN DECISIONS
------------------------------------------------------------

### Backpressure Handling

Backpressure is handled using a bounded BlockingQueue between
FileProducer and FanOutOrchestrator.

Reasoning:

- Producer can read faster than sinks process.
- Queue buffering smooths speed differences.
- Producer blocks automatically when full.
- Prevents memory overflow and stabilizes throughput.

Why this approach:

- Simple and reliable
- Built-in thread safety
- Natural producer-consumer flow control


### Concurrency Model Choice

Virtual Threads were chosen because:

- Fan-out creates many short-lived tasks.
- Traditional thread pools are heavier.
- Virtual threads allow high concurrency with low overhead.

Advantages:

- Simple programming model
- High scalability
- Independent sink execution
- Minimal synchronization complexity


------------------------------------------------------------
14. ASSUMPTIONS
------------------------------------------------------------

### Data Format Assumptions

- Input file is JSONL (one JSON per line).
- Records are well-formed.
- Deterministic record IDs exist.
- CSV/fixed-width can be added with new parsers.

### Network & Sink Assumptions

- Sinks are mocked (no real network calls).
- Latency simulated via logging.
- Sink operations assumed idempotent.
- Rate limits represent realistic downstream capacity.

### Execution Assumptions

- Single JVM execution.
- Ordering across sinks is not guaranteed due to parallelism.
- Failures are transient and recoverable by retries.


------------------------------------------------------------
15. CONFIGURATION
------------------------------------------------------------

application.yaml

inputFile: sample-data/input.jsonl
queueSize: 1000

sinks:
  rest:
    rateLimit: 50
  grpc:
    rateLimit: 100
  mq:
    rateLimit: 200
  db:
    rateLimit: 500


------------------------------------------------------------
16. HOW TO RUN
------------------------------------------------------------

Step 1: Build the Project

    mvn clean install

Step 2: Run the Application

    mvn exec:java "-Dexec.mainClass=com.fanout.engine.Main"

Note:
If you encounter a PowerShell error such as:

    Unknown lifecycle phase ".mainClass=..."

It means the -D argument was not parsed correctly.

In that case, use one of the following alternatives:

Option A (Recommended for PowerShell):

    mvn exec:java "-Dexec.mainClass=com.fanout.engine.Main"

Option B (Set JVM option first, then run):

    $env:JAVA_TOOL_OPTIONS=""
    mvn exec:java "-Dexec.mainClass=com.fanout.engine.Main"

Option C (Run directly using Java after build):

    java -cp target/fanout-engine-1.0.jar com.fanout.engine.Main

Alternatively, you can open the project in IntelliJ IDEA
and run Main.java directly.

Run completes when:
- Input file is fully processed
- Metrics are printed
- Orchestrator shuts down gracefully



------------------------------------------------------------
17. SAMPLE INPUT
------------------------------------------------------------

{"id":"1","name":"Alice","country":"India","amount":120.5}
{"id":"2","name":"Bob","country":"USA","amount":89.2}
{"id":"3","name":"Charlie","country":"UK","amount":45.1}


------------------------------------------------------------
18. ASSIGNMENT REQUIREMENT COVERAGE
------------------------------------------------------------

✔ Streaming ingestion  
✔ Memory-safe processing  
✔ Strategy Pattern transformations  
✔ Factory Pattern sink creation  
✔ Parallel fan-out execution  
✔ Virtual thread concurrency  
✔ Rate limiting  
✔ Backpressure handling  
✔ Retry logic (3 retries)  
✔ Metrics & observability  
✔ Config-driven setup  
✔ Graceful shutdown  
✔ Extensible architecture  


------------------------------------------------------------
19. PROJECT STRUCTURE
------------------------------------------------------------

src/main/java/com/fanout/engine/

    config/
    model/
    ingestion/
    transform/
    sink/
    factory/
    orchestrator/
    throttling/
    retry/
    metrics/

Main.java
pom.xml
application.yaml
sample-data/


------------------------------------------------------------
20. FUTURE IMPROVEMENTS
------------------------------------------------------------

- Real HTTP/gRPC integrations
- Real Protobuf & Avro serialization
- Dead Letter Queue
- Structured logging
- Distributed metrics monitoring


------------------------------------------------------------
21. CONCLUSION
------------------------------------------------------------

This project demonstrates how to build a scalable, extensible,
and resilient data fan-out engine using:

- Streaming ingestion
- Virtual thread concurrency
- Backpressure-driven design
- Strategy + Factory patterns

The design reflects real-world backend systems used for:

- Data pipelines
- Event propagation
- Multi-sink synchronization
- Microservice integration

------------------------------------------------------------
END OF FILE
------------------------------------------------------------
