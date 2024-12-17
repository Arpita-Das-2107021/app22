package com.example.krypt;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapActivity extends bottomNavigation {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = "MapActivity";
    private static final String API_URL = "https://api.myjson.online/v1/records/b5c9f9ca-e0a2-466e-8d80-936416a0c82c"; // Replace with your API endpoint
    private MapView mapView;
    private LocationManager locationManager;
    private FirebaseAuth auth;
    private Button panicButton, endPanicButton;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_map, findViewById(R.id.content_frame));

        auth = FirebaseAuth.getInstance();
        requestQueue = Volley.newRequestQueue(this);

        mapView = findViewById(R.id.map);
        Configuration.getInstance().setUserAgentValue("com.example.krypt");
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        mapView.getController().setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(22.900, 89.502); // KUET, Khulna, BD
        mapView.getController().setCenter(startPoint);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        panicButton = findViewById(R.id.btnPanic);
        endPanicButton = findViewById(R.id.btnEndPanic);

        panicButton.setOnClickListener(v -> shareLocationWithAdmins());
        endPanicButton.setOnClickListener(v -> removeLocationFromAdmins());

        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            requestLocationUpdates();
        }
    }

    private void requestLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Log.d(TAG, "Location changed: " + location.getLatitude() + ", " + location.getLongitude());
                    updateMapLocation(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(@NonNull String provider) {}

                @Override
                public void onProviderDisabled(@NonNull String provider) {}
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void updateMapLocation(Location location) {
        GeoPoint newPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapView.getController().setCenter(newPoint);

        Marker marker = new Marker(mapView);
        marker.setPosition(newPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Current Location");

        mapView.getOverlays().clear();
        mapView.getOverlays().add(marker);

        Log.d(TAG, "Updated map location to: " + location.getLatitude() + ", " + location.getLongitude());
    }

    private void shareLocationWithAdmins() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                sendLocationMessage(lastKnownLocation);
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Location permission is required to share your location", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendLocationMessage(Location location) {
        String userEmail = auth.getCurrentUser().getEmail();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String message = "Panic! User needs help.";

        try {
            JSONObject userJson = new JSONObject();
            userJson.put("userId", userEmail);
            userJson.put("latitude", latitude);
            userJson.put("longitude", longitude);
            userJson.put("message", message);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL, userJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(MapActivity.this, "Location shared successfully!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Location shared: " + response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MapActivity.this, "Failed to share location.", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error: " + error.getMessage());
                        }
                    });

            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeLocationFromAdmins() {
        String userEmail = auth.getCurrentUser().getEmail();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, API_URL + "/" + userEmail, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MapActivity.this, "Location removed successfully!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Location removed: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MapActivity.this, "Failed to remove location.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error: " + error.getMessage());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission is required to show your current location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Configuration.getInstance().save(this, getPreferences(MODE_PRIVATE));
    }
}
