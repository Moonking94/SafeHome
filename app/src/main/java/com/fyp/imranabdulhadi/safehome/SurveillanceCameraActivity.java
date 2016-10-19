package com.fyp.imranabdulhadi.safehome;

import android.app.Activity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.os.Bundle;
import android.widget.ImageButton;
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
 * Project the live stream using HTML code to the web view
 */
public class SurveillanceCameraActivity extends Activity implements View.OnClickListener {

    // Button to stop the surveillance
    private ImageButton btnStopSurveillance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surveillance_camera);

        WebView streamView = (WebView) findViewById(R.id.streamview);
        btnStopSurveillance = (ImageButton) findViewById(R.id.btnStopSurveillance);

        displayHtmlText(getHtml(), streamView);

        btnStopSurveillance.setOnClickListener(this);
    }

    /**
     * Construct the html code for viewing the stream
     *
     * @return - return the constructed html code
     */
    private String getHtml() {

        String html_head = "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "    <head>" +
                "        <meta http-equiv=\"Content-Type\" content=\"text/html;charset=iso-8859-1\">\n" +
                "    </head>\n";
        String html_body = "    <body style=\"background-color:black;\">\n" +
                "        <img id=\"stream\" width=\"100%\" height=\"100%\" src=\"" + getString(R.string.raspberrypi_address) + getString(R.string.stream_port) + "\"/>\n" +
                "    </body>\n";
        String html_close = "</html>";
        String html = html_head + html_body + html_close;

        return html;
    }

    /**
     * Binds the constructed html code and project it to the web view
     */
    private void displayHtmlText(String htmlContent, WebView streamView) {
        WebSettings settings = streamView.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        streamView.setWebChromeClient(new WebChromeClient());
        streamView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
    }

    /**
     *  This function to stop the stream when the user press start surveillance button
     */
    private void stopStream() {
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
                params.put("StopSurveillance", "StopSurveillance");
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }

    /**
     * Functions that stops the surveillance once the button is click
     */
    private void stopSurveillance() {
        stopStream();
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == btnStopSurveillance) {
            stopSurveillance();
        }
    }
}