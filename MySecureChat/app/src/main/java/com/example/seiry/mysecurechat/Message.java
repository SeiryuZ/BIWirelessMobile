package com.example.seiry.mysecurechat;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by steven on 3/12/17.
 */



public class Message extends RealmObject {
    @PrimaryKey
    public long id;

    public void setId(long id) {
        this.id = id;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String recipient;
    public String sender;
    public String messages;
    public int created;
}
