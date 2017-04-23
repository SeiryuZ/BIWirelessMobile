package com.example.seiry.mysecurechat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class ChatDetails extends AppCompatActivity {
    private String TAG = "ChatDetails";
    private LinkedList<Message> messages;

    private EditText messagetTextView;
    private TextView conversationTextView;
    private String OtherUsername;
    String conversation = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);

        // Bind elements
        conversationTextView = (TextView) findViewById(R.id.conversationTextView);
        messagetTextView = (EditText) findViewById(R.id.messageTextView);

        // Get relevant information from intent
        Intent intent = getIntent();
        OtherUsername = intent.getStringExtra("recipients");

        // Query database and build chat conversation
        Realm.init(ChatDetails.this);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Message> messages = realm.where(Message.class).findAll();


        // latest messsage on top
        for (Message message: messages) {
            if (message.recipient.equals(OtherUsername) || message.sender.equals(OtherUsername)) {
                conversation = Utils.getInstance().decrypt(message.messages) + "     \n" + conversation;
            }
        }
        conversationTextView.setText(conversation);
    }

    public void sendMessage(View view) {
        String EncryptedMessage = Utils.getInstance().encrypt(messagetTextView.getText().toString());

        String url = "http://192.168.1.107:8000/api/messages/add/";

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
                        Log.v(TAG, "Response: " + response.toString());
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

                            conversation = Utils.getInstance().decrypt(localMessage.messages) + "     \n" + conversation;
                            conversationTextView.setText(conversation);

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
