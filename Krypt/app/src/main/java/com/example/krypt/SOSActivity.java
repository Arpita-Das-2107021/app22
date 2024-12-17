package com.example.krypt;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SOSActivity extends bottomNavigation {

    private static final String TAG = "SOSActivity";
    private static final String API_URL ="https://api.myjson.online/v1/records/5e119476-88c9-4cb3-b109-3e8c6b264350";//"https://api.myjson.online/v1/records/4f71ef0c-adde-4d55-b668-7e52b0888a93"; //"https://myjson.online/app/records/b5c9f9ca-e0a2-466e-8d80-936416a0c82c";
    private LinearLayout userInfoLayout;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_sosactivity, findViewById(R.id.content_frame));

        requestQueue = Volley.newRequestQueue(this);

        userInfoLayout = findViewById(R.id.user_info_layout);

        fetchUserInfo();
    }

    private void fetchUserInfo() {
        Toast.makeText(this, "Fetching user info...", Toast.LENGTH_SHORT).show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Extract the 'data' array from the response
                            JSONArray userArray = response.getJSONArray("data");
                            displayUserInfo(userArray);
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage());
                            Toast.makeText(SOSActivity.this, "Error parsing user info!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.toString());
                        if (error.networkResponse != null) {
                            Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                            if (error.networkResponse.data != null) {
                                Log.e(TAG, "Response Data: " + new String(error.networkResponse.data));
                            }
                        }
                        Toast.makeText(SOSActivity.this, "Error fetching user info!", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    @SuppressLint("SetTextI18n")
    private void displayUserInfo(JSONArray usersArray) {
        Toast.makeText(this, "Displaying user info...", Toast.LENGTH_SHORT).show();

        userInfoLayout.removeAllViews();

        for (int i = 0; i < usersArray.length(); i++) {
            try {
                JSONObject userJson = usersArray.getJSONObject(i);
                String userId = userJson.getString("userId");
                String message = userJson.getString("message");
                double latitude = userJson.getDouble("latitude");
                double longitude = userJson.getDouble("longitude");


                TextView userInfoTextView = new TextView(this);
                userInfoTextView.setText(
                        "User ID: " + userId + "\n" +
                                "Latitude: " + latitude + "\n" +
                                "Longitude: " + longitude + "\n" +
                                "Message: " + message + "\n"
                );
                userInfoTextView.setPadding(16, 16, 16, 16);

                userInfoLayout.addView(userInfoTextView);

            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                Toast.makeText(this, "Error parsing JSON!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
