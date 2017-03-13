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

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChatList extends AppCompatActivity {

    public static LinkedHashMap<String, ArrayList<Message>> messages = new LinkedHashMap();

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // Populate messages manually, later we will use database
        Calendar cal = Calendar.getInstance();

        messages.put("steveJob", new ArrayList<Message>());

        // Latest message is on the first index
        cal.set(2017, 3, 13, 14, 22);
        messages.get("steveJob").add(
                new Message("Awesome fun man", cal.getTime().getTime(),
                        true, Message.Type.INCOMING)
        );
        cal.set(2017, 3, 13, 14, 20);
        messages.get("steveJob").add(
                new Message("Hi there, having fun up there?", cal.getTime().getTime(),
                        true, Message.Type.OUTGOING)
        );


        messages.put("gaben", new ArrayList<Message>());
        cal.set(2017, 3, 13, 14, 32);
        messages.get("gaben").add(
                new Message("Sure mate, wait for the sales", cal.getTime().getTime(),
                        true, Message.Type.INCOMING)
        );
        cal.set(2017, 3, 13, 14, 30);
        messages.get("gaben").add(
                new Message("Hi GabeN, can we get more discount?", cal.getTime().getTime(),
                        true, Message.Type.OUTGOING)
        );



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
    public static LinkedHashMap<String, ArrayList<Message>> messages;

    class MessageHolder extends RecyclerView.ViewHolder {
        public TextView recipientText;
        public TextView messageText;
        public TextView createdText;

        public MessageHolder(View v) {
            super(v);

            recipientText = (TextView) v.findViewById(R.id.chat_row_user);
            messageText = (TextView) v.findViewById(R.id.chat_row_message);
            createdText = (TextView) v.findViewById(R.id.chat_row_time);

            // Set listener to open chat details correctly
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ChatDetails.class);

                    // Look for the correct user
                    // THIS IS NOT GOOD AT ALL, WE'LL REPLACE THIS WITH DB CALL LATER
                    Integer counter = 0;
                    String currentUser = "DEFAULT";
                    for (String user: messages.keySet()) {
                        currentUser = user;
                        if (counter == getAdapterPosition()) {break;}
                        counter++;
                    }
                    intent.putExtra("recipients", currentUser);
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
        // Look for the correct user and message list
        // THIS IS NOT GOOD AT ALL, WE'LL REPLACE THIS WITH DB CALL LATER
        Integer counter = 0;
        String currentUser = "DEFAULT";
        ArrayList<Message> userMessages = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Message>> entry: messages.entrySet()) {
            currentUser = entry.getKey();
            userMessages = entry.getValue();

            if (counter == position) {break;}
            counter++;
        }


        // Set all the variables to the respective component
        holder.recipientText.setText(currentUser);
        holder.messageText.setText(userMessages.get(0).getMessage());

        // Change unix timestamp to Hour:Minute format
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
        holder.createdText.setText(formatter.format(new Date(userMessages.get(0).getCreated())));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public MessageAdapter(LinkedHashMap messages) {
        this.messages = messages;
    }


}
