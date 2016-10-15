package com.fyp.imranabdulhadi.safehome;

import android.app.Activity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.os.Bundle;
import android.widget.Button;

public class SurveillanceCameraActivity extends Activity implements View.OnClickListener {

    private Button btnStopSurveillance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surveillance_camera);

        WebView streamView = (WebView) findViewById(R.id.streamview);
        btnStopSurveillance = (Button)findViewById(R.id.stopstream);

        displayHtmlText(getHtml(), streamView);

        btnStopSurveillance.setOnClickListener(this);
    }

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

    private void displayHtmlText(String htmlContent, WebView streamView) {
        WebSettings settings = streamView.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        streamView.setWebChromeClient(new WebChromeClient());
        streamView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
    }

    private void stopSurveillance() {
        //String stopSurveillanceUrl = getString(R.string.raspberrypi_address) + getString(R.string.stopSurveillance);
        finish();
    }

    @Override
    public void onClick(View v) {
        if(v == btnStopSurveillance) {
            stopSurveillance();
        }
    }
}