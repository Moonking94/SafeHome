package com.fyp.imranabdulhadi.safehome;

import android.app.Activity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.os.Bundle;
import android.widget.ImageButton;

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
     * Functions that stops the surveillance once the button is click
     */
    private void stopSurveillance() {
        //String stopSurveillanceUrl = getString(R.string.raspberrypi_address) + getString(R.string.stopSurveillance);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == btnStopSurveillance) {
            stopSurveillance();
        }
    }
}