package com.tstudioz.fax.fme.fragments;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tstudioz.fax.fme.R;

import butterknife.BindView;
import butterknife.ButterKnife;



public class Right extends Fragment {


    final String url = "https://login.microsoftonline.com/login.srf?wa=wsignin1.0&rpsnv=4&ct=1484490683&rver=6.7.6640.0&wp=MBI_SSL&wreply=https%3a%2f%2foutlook.office365.com";
    @BindView(R.id.no_conn) TextView noConn;
    @BindView(R.id.web) WebView webView;
    @BindView(R.id.btn_refresh) Button btnRefresh;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //set the layout you want to display in First Fragment
        View view = inflater.inflate(R.layout.right_tab,
                container, false);
        setHasOptionsMenu(true);

        ButterKnife.bind(this, view);

        //web
        webView = (WebView) view.findViewById(R.id.web);
        webView.getSettings().setJavaScriptEnabled(true);


        final Activity activity = getActivity();
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }
        });
        webView.setWebViewClient(new MyWebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Ups! Došlo je do pogreške.", Toast.LENGTH_SHORT).show();
                loadPage();
            }
        });

       loadPage();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshPage();
            }
        });


        return view;

    }

    public class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.refresMe).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    public void loadPage() {


        if (isNetworkAvailable()) {
            webView.loadUrl(url);
        } else {

            webView.setVisibility(View.GONE);

            noConn.setVisibility(View.VISIBLE);


            btnRefresh.setVisibility(View.VISIBLE);

        }
    }

    public void refreshPage() {


        if (isNetworkAvailable()) {

            if (webView.getVisibility() == View.GONE
                    && noConn.getVisibility() == View.VISIBLE
                    && btnRefresh.getVisibility() == View.VISIBLE) {


                noConn.setVisibility(View.GONE);
                btnRefresh.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);

                if (webView.getUrl() == null){
                    webView.loadUrl(url);
                } else {
                    webView.loadUrl(webView.getUrl().toString());
                }

            }
            else { webView.loadUrl(webView.getUrl().toString());

            }

        }

        else {

            webView.setVisibility(View.GONE);
            noConn.setVisibility(View.VISIBLE);
            btnRefresh.setVisibility(View.VISIBLE);

        }

    }



    public void onPause(){
        super.onPause();


    }

}