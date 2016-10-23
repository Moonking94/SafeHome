package com.fyp.imranabdulhadi.safehome;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Main Application screen
 */
public class MainActivity extends Activity implements View.OnClickListener {

    // Session Manager Class
    private SessionManager session;

    // Layout widgets
    private ImageButton btnStartSurveillance;
    private Button btnLogout;
    private Switch switchMode;

    // Store the system's current mode
    private String mode;

    // Progress Dialog
    private ProgressDialog pDialog;

    // Store the user's detail temporarily
    private HashMap<String, String> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        btnLogout = (Button) findViewById(R.id.button_logout);
        btnStartSurveillance = (ImageButton) findViewById(R.id.btnStartSurveillance);
        switchMode = (Switch) findViewById(R.id.switch_mode);
        pDialog = new ProgressDialog(this);

        // Initialize the user's detail
        initializeUser();

        // Retrieve the system's mode from the server and initialize it on the application
        retrieveMode();

        switchMode.setOnClickListener(this);
        btnStartSurveillance.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addAddress:

                return true;
            case R.id.changeAddress:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Disable the user's from going back to the previous user's page
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /**
     * Initialize the user detail after they logged in
     */
    private void initializeUser() {

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        // Get user data from session
        user = session.getUserDetails();

        // Get the user's name from the session
        String name = user.get(SessionManager.KEY_NAME);

        // Get the user's position from the session
        String position = user.get(SessionManager.KEY_POSITION);

        if (position != null) {
            if (!position.equals("Owner")) {
                switchMode.setEnabled(false);
                Toast.makeText(getApplicationContext(), "You are not authorized to change the system mode !", Toast.LENGTH_LONG).show();
            } else {
                switchMode.setEnabled(true);
            }
            ((TextView) findViewById(R.id.text_welcome)).setText(Html.fromHtml(name));
        }
    }

    /**
     * Retrieve the current system's mode from the server
     */
    private void retrieveMode() {
        String url = getApplicationContext().getString(R.string.raspberrypi_address) + getApplicationContext().getString(R.string.retrieve_mode);
        try {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            Boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
            if (isConnected) {
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                if (!pDialog.isShowing())
                                    pDialog.show();

                                try {
                                    JSONObject modeResponse = (JSONObject) response.get(0);

                                    mode = modeResponse.getString("currentmode");
                                    if (mode.equals("Home")) {
                                        switchMode.setChecked(false);
                                    } else {
                                        switchMode.setChecked(true);
                                    }

                                    if (pDialog.isShowing())
                                        pDialog.dismiss();
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Toast.makeText(getApplicationContext(), "Error:" + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                                if (pDialog.isShowing())
                                    pDialog.dismiss();
                            }
                        });

                RequestQueue queue = Volley.newRequestQueue(this);
                queue.add(jsonArrayRequest);
            } else {
                Toast.makeText(getApplication(), "Network is NOT available",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplication(),
                    "Error reading mode:" + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Switch the system's mode to the ones the user change
     */
    private void updateMode() {
        String url = getApplicationContext().getString(R.string.raspberrypi_address) + getApplicationContext().getString(R.string.update_mode);

        if (switchMode.isChecked()) {
            mode = switchMode.getTextOn().toString();
        } else {
            mode = switchMode.getTextOff().toString();
        }

        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject modeResponse = new JSONObject(response);
                            String newMode = modeResponse.getString("Info");

                            Toast.makeText(getApplicationContext(), newMode, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "JSON Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("MainActivity", "Login Error: " + error.getMessage());
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(getApplicationContext(), "Connection time out", Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Mode", mode);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }

    /**
     * This function to start the stream when the user press start surveillance button
     */
    private void startStream() {
        String url = getString(R.string.raspberrypi_address) + getString(R.string.surveillance);

        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject streamResponse = new JSONObject(response);
                            String status = streamResponse.getString("Status");
                            Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "JSON Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("StartSurveillance", "StartSurveillance");
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }

    /**
     * Start the surveillance activity
     */
    private void startSurveillance() {
        startStream();
        Intent intent = new Intent(this, SurveillanceCameraActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == switchMode) {
            updateMode();
        }
        if (v == btnStartSurveillance) {
            startSurveillance();
        }
        if (v == btnLogout) {
            session.logoutUser();
        }
    }
}
