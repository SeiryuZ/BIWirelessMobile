package com.example.seiry.mysecurechat;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatList extends AppCompatActivity {

    public static ArrayList<Message> messages = new ArrayList<Message>();

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // Populate messages for now
        messages.add(new Message("steven", "test", "hi", true));
        messages.add(new Message("steven", "gaben", "praise the gaben", true));

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        mAdapter = new MessageAdapter(messages);
        mRecyclerView.setAdapter(mAdapter);

        // Bind refresh swipe
        refreshLayout =  (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshChat();
            }
        });
    }

    public void refreshChat() {
        ChatRefresherService.startActionRefresh(this);
        refreshLayout.setRefreshing(false);
    }
}


class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder>{
    private ArrayList<Message> messages;

    class MessageHolder extends RecyclerView.ViewHolder {
        public TextView recipientText;
        public TextView messageText;
        public TextView createdText;

        public MessageHolder(View v) {
            super(v);

            recipientText = (TextView) v.findViewById(R.id.chat_row_recipient);
            messageText = (TextView) v.findViewById(R.id.chat_row_message);
            createdText = (TextView) v.findViewById(R.id.chat_row_time);

            // Set listener to open chat details correctly
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ChatDetails.class);
                    intent.putExtra("recipients", messages.get(getAdapterPosition()).getRecipient());
                    view.getContext().startActivity(intent);
                }
            });
        }

    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create new view for each row
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_row, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        // Set all the variables to the respective components
        Message message = messages.get(position);
        holder.recipientText.setText(message.getRecipient());
        holder.messageText.setText(message.getMessage());

        // Change unix timestamp to Hour:Minute format
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
        holder.createdText.setText(formatter.format(new Date(message.getCreated())));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public MessageAdapter(ArrayList messages) {
        this.messages = messages;
    }


}
