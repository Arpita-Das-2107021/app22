package com.example.krypt;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class LocationService {

    private static final String SERVER_URL = "https://myjson.online/app/collections/942d5394-000e-44c3-a452-cffcd2b477f8";
    private RequestQueue requestQueue;

    public LocationService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void sendLocationMessage(LocationMessage locationMessage, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject userObject = new JSONObject();
            userObject.put("userId", locationMessage.getUser().getUserId());
            userObject.put("userName", locationMessage.getUser().getUserName());
            jsonObject.put("user", userObject);
            jsonObject.put("latitude", locationMessage.getLatitude());
            jsonObject.put("longitude", locationMessage.getLongitude());
            jsonObject.put("message", locationMessage.getMessage());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SERVER_URL, jsonObject, responseListener, errorListener);
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
