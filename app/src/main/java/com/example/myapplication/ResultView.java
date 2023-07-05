package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class ResultView extends AppCompatActivity {

    WebView webView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_view);
       webView=findViewById(R.id.webviewResult);
       String url=getIntent().getStringExtra("MainUrl");
       String regno=getIntent().getStringExtra("regno");
       String aadhar=getIntent().getStringExtra("aadhar");
        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        String id="'aadhaar'";
        String id1="'regno'";
        String btn="'cut'";

        String javascript="document.getElementById("+id+").value='"+aadhar+"';document.getElementById("+id1+").value='"+regno+"';";
       webView.setWebViewClient(new WebViewClient(){

           @Override
           public void onPageFinished(WebView view, String url) {


               new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       webView.evaluateJavascript(javascript,null);
                   }
               },1000);

               new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       webView.evaluateJavascript("document.getElementById("+btn+").click();",null);
                   }
               },1500);



    Log.i("WebView",javascript);
               super.onPageFinished(view, url);
           }


       });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WebView", consoleMessage.message());
                return true;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        String url = request.getUrl().toString();
                        if (url.endsWith(".pdf")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(url), "application/pdf");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            try {
                                view.getContext().startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                // Handle case where no PDF viewer application is installed
                                Toast.makeText(view.getContext(), "No PDF viewer installed", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                        return super.shouldOverrideUrlLoading(view, request);
                    }
                });

            }
        },1200);



        webView.loadUrl(url);


    }
}