package com.example.seiry.mysecurechat;

/**
 * Created by steven on 3/12/17.
 */

public class Message {
    // This class will represent every single message we send/receive
    private String message;
    private Long created;
    private Boolean isRead;
    private int messageType;

    // Type of message
    public static class Type {
        public static final int INCOMING = 1;
        public static final int OUTGOING = 2;
    }

    public Message(String message, long created, Boolean isRead, int messageType) {
        this.message = message;
        this.created = created;
        this.isRead = isRead;
        this.messageType = messageType;
    }

    public Message(String message, Boolean isRead, int messageType) {
        this.message = message;

        // Not provided time, current time
        this.created = System.currentTimeMillis();
        this.isRead = isRead;
        this.messageType = messageType;
    }

    public int getMessageType() {
        return messageType;
    }

    public String getMessage() {
        return message;
    }

    public Long getCreated() {
        return created;
    }

    public Boolean getRead() {
        return isRead;
    }
}
