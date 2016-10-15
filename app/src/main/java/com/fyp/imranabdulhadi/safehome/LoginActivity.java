package com.fyp.imranabdulhadi.safehome;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that allows the user to login by entering their email/password.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    // Email and Password  EditText
    EditText txtEmail, txtPassword;

    // Login Button
    Button btnLogin;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    // Store the user detail temporarily
    private User user;

    // Display the Progress Box
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        // Email and Password input text
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        // Login Button
        btnLogin = (Button) findViewById(R.id.button_login);

        // Setup the progress dialog used when log in button is pressed
        pDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_DARK);
        pDialog.setMessage("Please wait...");
        pDialog.setTitle("Authenticating");
        pDialog.setIndeterminate(true);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnLogin) {
            login();
        }
    }

    /**
     * Collect the values inside the text box and send them to the authentication method
     * If there are none, an error message will be prompted out
     */
    public void login() {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if (email.trim().length() > 0 && password.trim().length() > 0) {
            authenticate(email, password);
        } else {
            // user didn't entered username or password
            // Show alert asking him to enter the details
            alert.showAlertDialog(LoginActivity.this, "Login failed", "Please enter username and password", false);
        }
    }

    /**
     * Disable the user's ability to go back to the main page once log out
     */
    @Override
    public void onBackPressed() {
        // Disable going back to the Main Activity
        moveTaskToBack(true);
    }

    /**
     * This functions is to authenticate the user credentials
     * If it is correct, they will be redirect to the main page
     * else the system will prompt error message
     * @param email - user email
     * @param password - user password
     */
    private void authenticate(final String email, final String password) {
        String url = getApplicationContext().getString(R.string.raspberrypi_address) + getApplicationContext().getString(R.string.retrieve_user);
        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject modeResponse = new JSONObject(response);
                            boolean error = modeResponse.getBoolean("error");

                            if (!error) {
                                pDialog.show();

                                String email = modeResponse.getString("useremail");
                                String name = modeResponse.getString("username");
                                String position = modeResponse.getString("userposition");

                                user = new User(email, name, position);
                                session.createLoginSession(user.getName(), user.getEmail(),
                                        user.getPosition());

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        logging();
                                        pDialog.dismiss();
                                    }
                                }, 3000);
                            } else {
                                // Prompt error if username / password doesn't match
                                alert.showAlertDialog(LoginActivity.this, "Login failed",
                                        "Username/Password is incorrect", false);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "JSON Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            // Handle and prompt if there are any connection error
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LoginActivity", "Login Error: " + error.getMessage());
                if(error instanceof NoConnectionError) {
                    alert.showAlertDialog(LoginActivity.this, "Login failed", "Error connecting to the server", false);
                } else if (error instanceof TimeoutError) {
                    alert.showAlertDialog(LoginActivity.this, "Login failed", "Connection time out", false);
                }
            }
        }) {
            /**
             * Use to POST the parameter to the server
             * @return - return variable contains the user credentials
             */
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("useremail", email);
                params.put("userpassword", password);

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }

    /**
     * Function: Open the main activity class or show error to the user
     */
    private void logging() {
        if (user != null) {

            session.createLoginSession(user.getName(), user.getEmail(), user.getPosition());

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

