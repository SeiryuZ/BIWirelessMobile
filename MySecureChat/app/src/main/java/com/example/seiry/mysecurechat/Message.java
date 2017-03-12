package com.example.seiry.mysecurechat;

/**
 * Created by steven on 3/12/17.
 */

public class Message {
    // This class will represent every single message we send/receive

    private String sender;
    private String recipient;

    private String message;
    private Long created;
    private Boolean isRead;

    public Message(String sender, String recipient, String message, Boolean isRead) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;

        // Unix timestamp
        this.created = System.currentTimeMillis();
        this.isRead = isRead;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
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
