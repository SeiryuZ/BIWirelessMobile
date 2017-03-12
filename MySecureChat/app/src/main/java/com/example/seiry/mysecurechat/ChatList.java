package com.example.seiry.mysecurechat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ChatList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
    }


    public void openChatDetails(View view) {
        Intent intent = new Intent(this, ChatDetails.class);
        startActivity(intent);
    }


    public void refreshChat(View view) {
        ChatRefresherService.startActionRefresh(this);
    }
}
