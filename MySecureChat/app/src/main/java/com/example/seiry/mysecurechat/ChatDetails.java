package com.example.seiry.mysecurechat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class ChatDetails extends AppCompatActivity {
    private String TAG = "ChatDetails";
    private ArrayList<Message> conversation = new ArrayList<>();

    private EditText messagetTextView;
    private String OtherUsername;

    private RecyclerView conversationView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);

        // Bind elements
        conversationView = (RecyclerView) findViewById(R.id.conversationView);
        messagetTextView = (EditText) findViewById(R.id.messageTextView);

        // Get relevant information from intent
        Intent intent = getIntent();
        OtherUsername = intent.getStringExtra("recipients");

        // specify an adapter
        String activeUser = getSharedPreferences("SECURECHAT", Context.MODE_PRIVATE).getString("ACTIVE_USER", "");
        conversationView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ConversationAdapter(this.conversation, activeUser);
        conversationView.setAdapter(mAdapter);


        // refresh the messages
        refreshMessages();

    }

    public void refreshMessages() {
        // Query database and build chat conversation
        Realm.init(ChatDetails.this);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Message> messages = realm.where(Message.class).findAll();
        messages = messages.sort("created");

        // update conversation arraylist
        this.conversation.clear();

        for (Message message: messages) {
            this.conversation.add(0, message);
        }

        mAdapter.notifyDataSetChanged();

    }

    public void sendMessage(View view) {
        String EncryptedMessage = Utils.getInstance().encrypt(messagetTextView.getText().toString());

        String url = "http://106.186.116.87:8123/api/messages/add/";

        Log.v(TAG, "SEND MESSAGE");
        // Initialize Realm
        //Login request
        JSONObject body = new JSONObject();
        try {
            SharedPreferences preference = getSharedPreferences("SECURECHAT", Context.MODE_PRIVATE);
            body.put("sender", preference.getString("ACTIVE_USER", ""));
            body.put("recipient", OtherUsername);
            body.put("message", Utils.getInstance().encrypt(messagetTextView.getText().toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, body, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.v(TAG, "Response: " + response.toString());
                        JSONObject message = null;
                        try {
                            message = response.getJSONObject("message");

                            final Message localMessage = new Message();
                            localMessage.setId(message.getInt("id"));
                            localMessage.setRecipient(message.getString("recipient"));
                            localMessage.setSender(message.getString("sender"));
                            localMessage.setMessages(message.getString("message"));
                            localMessage.setCreated(message.getInt("created"));

                            // Initialize Realm
                            Realm.init(ChatDetails.this);
                            Realm realm = Realm.getDefaultInstance();
                            realm.executeTransaction(new Realm.Transaction(){
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(localMessage);
                                }
                            });

                            refreshMessages();

                            messagetTextView.setText("");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(TAG, "MESSAGES ERROR " + error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences preference = getSharedPreferences("SECURECHAT", Context.MODE_PRIVATE);
                String token = preference.getString("TOKEN", "");
                params.put("Authorization", "Token " + token);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsObjRequest);

    }

}


class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationHolder> {
    private ArrayList<Message> conversation = new ArrayList<>();
    private String activeUser = "";

    class ConversationHolder extends RecyclerView.ViewHolder {
        public TextView recipientText;
        public TextView messageText;
        public TextView createdText;

        public ConversationHolder(View v) {
            super(v);

            recipientText = (TextView) v.findViewById(R.id.chat_row_user);
            messageText = (TextView) v.findViewById(R.id.chat_row_message);
            createdText = (TextView) v.findViewById(R.id.chat_row_time);

            // Set listener to open chat details correctly
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(view.getContext(), ChatDetails.class);
//
//                    // Look for the correct user
//                    // THIS IS NOT GOOD AT ALL, WE'LL REPLACE THIS WITH DB CALL LATER
//                    Integer counter = 0;
//                    String currentUser = "DEFAULT";
//                    for (String user : chatList.keySet()) {
//                        currentUser = user;
//                        if (counter == getAdapterPosition()) {
//                            break;
//                        }
//                        counter++;
//                    }
//                    intent.putExtra("recipients", currentUser);
//                    view.getContext().startActivity(intent);
//                }
//            });
        }

    }

    @Override
    public ConversationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create new view for each row
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_row, parent, false);
        return new ConversationHolder(view);
    }

    @Override
    public void onBindViewHolder(ConversationHolder holder, int position) {
        Message message = conversation.get(position);

        if (message.sender.equals(this.activeUser)) {
            holder.messageText.setGravity(Gravity.RIGHT);
        }

        holder.recipientText.setText(message.sender);
        holder.messageText.setText(Utils.getInstance().decrypt(message.messages));

        // Change unix timestamp to Hour:Minute format
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
        holder.createdText.setText(formatter.format(new Date((long)message.created * 1000)));
    }


    @Override
    public int getItemCount() {
        return conversation.size();
    }


    public ConversationAdapter(ArrayList<Message> conversation, String activeUser) {
        this.conversation = conversation;
        this.activeUser = activeUser;
    }
}

