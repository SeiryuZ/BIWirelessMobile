package com.example.seiry.mysecurechat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static String TAG = "LOGIN";
    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.EmailEditText);
        password = (EditText) findViewById(R.id.PasswordEditText);


        // Skip this page if we have token saved in the prefernce
        SharedPreferences preference = getSharedPreferences("SECURECHAT", Context.MODE_PRIVATE);
        if (preference.getString("TOKEN", "") != "") {
            Intent intent = new Intent(LoginActivity.this, ChatList.class);
            startActivity(intent);
        }

    }

    public void loginButtonPressed(View view) {

        Log.v(TAG, "LOGGING IN");
//        String url = "http://106.186.116.87:8123/api/auth/login/";
        String url = "http://192.168.1.107:8000/api/auth/login/";

        //Login request
        JSONObject body = new JSONObject();
        try {
            body.put("username", username.getText().toString());
            body.put("password", password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, body, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(TAG, "Response: " + response.toString());
                        try {
                            // Save this token for next requests
                            String token = response.getString("token");
                            Log.v(TAG, token);

                            SharedPreferences preference = getSharedPreferences("SECURECHAT", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preference.edit();

                            // Also save the active user name
                            editor.putString("TOKEN", token);
                            editor.putString("ACTIVE_USER", username.getText().toString());
                            editor.commit();


                            Intent intent = new Intent(LoginActivity.this, ChatList.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.v(TAG, "LOGIN ERROR " + error.getMessage());

                        NetworkResponse response = error.networkResponse;
                        String errorBody = new String(response.data);

                        Toast toast = Toast.makeText(LoginActivity.this, errorBody, Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsObjRequest);
    }
}
