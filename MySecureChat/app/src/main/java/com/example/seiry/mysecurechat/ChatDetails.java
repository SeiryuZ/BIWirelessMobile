package com.example.seiry.mysecurechat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
        for (Message message: ChatList.messages) {

            Log.v(TAG, message.getRecipient() + " " + recipients + " " );
            if (message.getRecipient().equals(recipients)) {
                recipient.setText(message.getRecipient());
            }
        }
    }
}
