package com.example.seiry.mysecurechat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatDetails extends AppCompatActivity {
    private String TAG = "ChatDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);

        TextView recipient = (TextView) findViewById(R.id.chatRecipient);

        Intent intent = getIntent();
        String recipients = intent.getStringExtra("recipients");

        // This is useless, we will use database later
        ArrayList<Message> messages = ChatList.messages.get(recipients);
        recipient.setText(recipients);
    }
}
