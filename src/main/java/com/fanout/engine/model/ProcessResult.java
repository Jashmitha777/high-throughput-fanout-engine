package com.fanout.engine.model;

public class ProcessResult {

    private final String recordId;
    private final String sinkName;
    private final boolean success;
    private final String message;
    private final int attempt;

    public ProcessResult(String recordId,
                         String sinkName,
                         boolean success,
                         String message,
                         int attempt) {
        this.recordId = recordId;
        this.sinkName = sinkName;
        this.success = success;
        this.message = message;
        this.attempt = attempt;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getSinkName() {
        return sinkName;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getAttempt() {
        return attempt;
    }

    @Override
    public String toString() {
        return "ProcessResult{" +
                "recordId='" + recordId + '\'' +
                ", sinkName='" + sinkName + '\'' +
                ", success=" + success +
                ", message='" + message + '\'' +
                ", attempt=" + attempt +
                '}';
    }
}
