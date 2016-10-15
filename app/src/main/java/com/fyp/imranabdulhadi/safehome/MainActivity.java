package com.fyp.imranabdulhadi.safehome;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements View.OnClickListener {

    // Session Manager Class
    private SessionManager session;

    private ImageButton btnStartSurveillance;
    private Button btnLogout;//, btnStartSurveillance;
    private Switch switchMode;

    private String mode;
    private ProgressDialog pDialog;

    private HashMap<String, String> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        btnLogout = (Button) findViewById(R.id.button_logout);
        //btnStartSurveillance = (Button) findViewById(R.id.button_surveillance);
        btnStartSurveillance = (ImageButton) findViewById(R.id.btnStartSurveillance);
        switchMode = (Switch) findViewById(R.id.switch_mode);
        pDialog = new ProgressDialog(this);

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        initializeUser();

        retrieveMode();
        //initiateSurveillance();

        switchMode.setOnClickListener(this);
        btnStartSurveillance.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    /**
     * Disable the user's from going back to the previous user's page
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void initializeUser() {

        session.checkLogin();

        // get user data from session
        user = session.getUserDetails();

        // name
        String name = user.get(SessionManager.KEY_NAME);

        // position
        String position = user.get(SessionManager.KEY_POSITION);

        if(position!=null)
            if (!position.equals("Owner")) {
                switchMode.setEnabled(false);
                Toast.makeText(getApplicationContext(), "You are not authorized to change the system mode !", Toast.LENGTH_LONG).show();
            } else {
                switchMode.setEnabled(true);
            }

        ((TextView)findViewById(R.id.textView)).setText(Html.fromHtml("Name: " + name + "\nPosition: " + position));
    }

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
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
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
                params.put("Mode", mode);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }

    private void initiateSurveillance() {
        String url = getString(R.string.raspberrypi_address) + getString(R.string.initiate_surveillance);

        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
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

    private void startSurveillance() {
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
