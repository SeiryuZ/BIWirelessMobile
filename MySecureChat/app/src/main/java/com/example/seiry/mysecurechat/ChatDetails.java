package com.example.seiry.mysecurechat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
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
    private LocationManager locationManager;
    private LocationListener locationListener;

    private RecyclerView conversationView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Location currentLocation;

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
        mAdapter = new ConversationAdapter(this.conversation, activeUser, this);
        conversationView.setAdapter(mAdapter);


        // refresh the messages
        refreshMessages();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                currentLocation = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "NO PERMISSION FOR LOCATION, attempt to request, if fail button will do nothing");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Always remove location listener when not used to preserve battery
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(locationListener);
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

    public void send(String text) {
        String url = "http://106.186.116.87:8123/api/messages/add/";

        Log.v(TAG, "SEND MESSAGE");
        // Initialize Realm
        //Login request
        JSONObject body = new JSONObject();
        try {
            SharedPreferences preference = getSharedPreferences("SECURECHAT", Context.MODE_PRIVATE);
            body.put("sender", preference.getString("ACTIVE_USER", ""));
            body.put("recipient", OtherUsername);
            body.put("message", text);
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


    public void sendMessage(View view) {
        String encryptedMessage = Utils.getInstance().encrypt(messagetTextView.getText().toString());
        send(encryptedMessage);
        messagetTextView.setText("");
    }

    public void locationButtonPressed(View view) {
        Log.v(TAG, "SENDING LOCATION......");
        if (currentLocation == null) {
            Toast toast = Toast.makeText(ChatDetails.this, "Cannot get current location, try again later", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        send(Utils.getInstance().encrypt("GPS: "+currentLocation.getLatitude() +"," + currentLocation.getLongitude()));
    }

}


class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationHolder> {
    private ArrayList<Message> conversation = new ArrayList<>();
    private Context context = null;
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

        // decrypt message
        final String decryptedMessage = Utils.getInstance().decrypt(message.messages);
        holder.messageText.setText(decryptedMessage);

        if (decryptedMessage.contains("GPS:")) {
            holder.messageText.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    // parse GPS message
                    String location = decryptedMessage.substring(4, decryptedMessage.length());
                    Log.v("ADAPTER", "OPEN GPS" + location);

                    // Open up any activity that can handle this url, be it browser / google map
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=" + location));
                    context.startActivity(browserIntent);
                }
            });
        } else {
            holder.messageText.setOnClickListener(null);
        }

        // Change unix timestamp to Hour:Minute format
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
        holder.createdText.setText(formatter.format(new Date((long)message.created * 1000)));
    }


    @Override
    public int getItemCount() {
        return conversation.size();
    }


    public ConversationAdapter(ArrayList<Message> conversation, String activeUser, Context context) {
        this.conversation = conversation;
        this.activeUser = activeUser;
        this.context = context;
    }
}

