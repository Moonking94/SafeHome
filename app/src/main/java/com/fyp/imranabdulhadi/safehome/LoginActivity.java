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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    // Email and Password  EditText
    EditText txtEmail, txtPassword;

    // Login Button
    Button btnLogin;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    private User user;
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

    public void login() {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if(email.trim().length() > 0 && password.trim().length() > 0){
            //
            authenticate(email, password);
            pDialog.show();

            //
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    logging();
                    pDialog.dismiss();
                }
            }, 3000);
        }else{
            // user didn't entered username or password
            // Show alert asking him to enter the details
            alert.showAlertDialog(LoginActivity.this, "Login failed", "Please enter username and password", false);
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the Main Activity
        moveTaskToBack(true);
    }

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
                                String email = modeResponse.getString("useremail");
                                String name = modeResponse.getString("username");
                                String position = modeResponse.getString("userposition");

                                user = new User(email, name, position);
                                session.createLoginSession(user.getName(), user.getEmail(), user.getPosition());
                            } else {
                                user = null;
                                String errorMsg = modeResponse.getString("error_msg");
                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
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

    private void logging() {
        if(user!=null){

            session.createLoginSession(user.getName(), user.getEmail(), user.getPosition());

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();

        }else{
            // username / password doesn't match
            alert.showAlertDialog(LoginActivity.this, "Login failed", "Username/Password is incorrect", false);
        }
    }
}

