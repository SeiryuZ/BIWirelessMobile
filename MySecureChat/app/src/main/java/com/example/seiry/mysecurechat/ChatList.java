package com.example.seiry.mysecurechat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
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
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.realm.Realm;
import io.realm.RealmResults;

public class ChatList extends AppCompatActivity {

    private static String TAG = "CHAT LIST";
    public static RealmResults<Message> messages;
    private LinkedHashMap<String, Message> chatList = new LinkedHashMap<>();

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Query database
        Realm.init(ChatList.this);
        Realm realm = Realm.getDefaultInstance();
        messages = realm.where(Message.class).findAll();
        buildChatList();

        // specify an adapter
        mAdapter = new MessageAdapter(chatList);
        mRecyclerView.setAdapter(mAdapter);

        // Bind refresh swipe
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshChat();
            }
        });
    }

    public void refreshChat() {
//        ChatRefresherService.startActionRefresh(this);

        Log.v(TAG, "LOGGING IN");
        String url = "http://192.168.1.107:8000/api/messages/index/";

        Log.v(TAG, "CURRENT MESSAGES");
        // Initialize Realm
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(TAG, "Response: " + response.toString());
                        refreshLayout.setRefreshing(false);

                        try {
                            JSONArray messages = response.getJSONArray("messages");
                            for (int i = 0; i < messages.length(); i++) {
                                JSONObject message = messages.getJSONObject(i);

                                final Message localMessage = new Message();
                                localMessage.setId(message.getInt("id"));
                                localMessage.setRecipient(message.getString("recipient"));
                                localMessage.setSender(message.getString("sender"));
                                localMessage.setMessages(message.getString("message"));
                                localMessage.setCreated(message.getInt("created"));

                                // Initialize Realm
                                Realm.init(ChatList.this);
                                Realm realm = Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction(){
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.copyToRealmOrUpdate(localMessage);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Realm.init(ChatList.this);
                        Realm realm = Realm.getDefaultInstance();
                        messages = realm.where(Message.class).findAll();
                        buildChatList();
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.v(TAG, "MESSAGES ERROR " + error.getMessage());
                        refreshLayout.setRefreshing(false);

                        NetworkResponse response = error.networkResponse;
                        String errorBody = new String(response.data);

                        Toast toast = Toast.makeText(ChatList.this, errorBody, Toast.LENGTH_LONG);
                        toast.show();
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



    public void buildChatList() {
        SharedPreferences preference = getSharedPreferences("SECURECHAT", Context.MODE_PRIVATE);
        String activeUsername = preference.getString("ACTIVE_USER", "");

        // Parse each messages to build chatlist
        for (Message message: messages){

            Log.v("ADAPTER", message.toString());

            if (!message.sender.equals(activeUsername)) {
                chatList.put(message.sender, message);
            } else {
                chatList.put(message.recipient, message);
            }
        }
    }
}


class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {
    private LinkedHashMap<String, Message> chatList = new LinkedHashMap<>();

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
                    for (String user : chatList.keySet()) {
                        currentUser = user;
                        if (counter == getAdapterPosition()) {
                            break;
                        }
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
        Integer counter = 0;
        String currentUser = "DEFAULT";
        Message userMessage = null;
        for (Map.Entry<String, Message> entry : chatList.entrySet()) {
            currentUser = entry.getKey();
            userMessage = entry.getValue();

            if (counter == position) {
                break;
            }
            counter++;
        }

        // Set all the variables to the respective component
        holder.recipientText.setText(currentUser);
        holder.messageText.setText(Utils.getInstance().decrypt(userMessage.messages));

        // Change unix timestamp to Hour:Minute format
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
        holder.createdText.setText(formatter.format(new Date((long)userMessage.created * 1000)));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public MessageAdapter(LinkedHashMap<String, Message> chatList) {
        this.chatList = chatList;
    }
}
