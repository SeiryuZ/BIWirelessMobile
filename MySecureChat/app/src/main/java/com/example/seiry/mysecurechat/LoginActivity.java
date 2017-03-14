package com.example.seiry.mysecurechat;

import android.content.Intent;
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
    }

    public void loginButtonPressed(View view) {

        Log.v(TAG, "LOGGING IN");
        String url = "http://106.186.116.87:8123/api/auth/login/";
//      url = "http://requestb.in/qpqej1qp";

        //Login request
//        JSONObject body = new JSONObject();
//        try {
//            body.put("username", username.getText());
//            body.put("password", password.getText());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                (Request.Method.POST, url, body, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.v(TAG, "Response: " + response.toString());
//                        try {
//                            // Save this token for next requests
//                            String token = response.getString("token");
//                            Log.v(TAG, token);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // TODO Auto-generated method stub
//                        NetworkResponse response = error.networkResponse;
//                        String errorBody = new String(response.data);
//
//                        Toast toast = Toast.makeText(LoginActivity.this, errorBody, Toast.LENGTH_LONG);
//                        toast.show();
//                        Log.v(TAG, "LOGIN ERROR " + errorBody);
//                    }
//                });


        // Auth Test Token
//        url = "http://106.186.116.87:8123/api/auth/token-test/";
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.v(TAG, "Response: " + response.toString());
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // TODO Auto-generated method stub
//                        NetworkResponse response = error.networkResponse;
//                        String errorBody = new String(response.data);
//
//                        Toast toast = Toast.makeText(LoginActivity.this, errorBody, Toast.LENGTH_LONG);
//                        toast.show();
//                        Log.v(TAG, "AUTH TEST ERROR " + errorBody);
//                    }
//                }) {
//
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String>  params = new HashMap<String, String>();
//                String token = "4d90ae39f8e70fa146d44c63a014963af3b45e25";
//                params.put("Authorization", "Token "+ token);
//                return params;
//            }
//        };

//        RequestQueue queue = Volley.newRequestQueue(this);
//        queue.add(jsObjRequest);

        Intent intent = new Intent(this, ChatList.class);
        startActivity(intent);
    }
}
