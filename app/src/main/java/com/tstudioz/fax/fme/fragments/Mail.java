package com.tstudioz.fax.fme.fragments;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.databinding.MailTabBinding;


public class Mail extends Fragment {

    private final String url = "https://login.microsoftonline.com/login.srf?wa=wsignin1" +
            ".0&rpsnv=4&ct=1484490683&rver=6.7.6640.0&wp=MBI_SSL&wreply=https%3a%2f%2foutlook" +
            ".office365.com";

    private MailTabBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //set the layout you want to display in First Fragment
        binding = MailTabBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);

        //web
        binding.web.getSettings().setJavaScriptEnabled(true);


        final Activity activity = getActivity();
        binding.web.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }
        });
        binding.web.setWebViewClient(new MyWebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                                        String failingUrl) {
                Toast.makeText(activity, "Ups! Došlo je do pogreške.", Toast.LENGTH_SHORT).show();
                loadPage();
            }
        });

        loadPage();

        binding.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshPage();
            }
        });


        return binding.getRoot();

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
        ConnectivityManager manager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    public void loadPage() {


        if (isNetworkAvailable()) {
            binding.web.loadUrl(url);
        } else {

            binding.web.setVisibility(View.GONE);

            binding.noConn.setVisibility(View.VISIBLE);


            binding.noConn.setVisibility(View.VISIBLE);

        }
    }

    public void refreshPage() {


        if (isNetworkAvailable()) {

            if (binding.web.getVisibility() == View.GONE
                    && binding.noConn.getVisibility() == View.VISIBLE
                    && binding.btnRefresh.getVisibility() == View.VISIBLE) {


                binding.noConn.setVisibility(View.GONE);
                binding.btnRefresh.setVisibility(View.GONE);
                binding.web.setVisibility(View.VISIBLE);

                if (binding.web.getUrl() == null) {
                    binding.web.loadUrl(url);
                } else {
                    binding.web.loadUrl(binding.web.getUrl().toString());
                }

            } else {
                binding.web.loadUrl(binding.web.getUrl().toString());

            }

        } else {

            binding.web.setVisibility(View.GONE);
            binding.noConn.setVisibility(View.VISIBLE);
            binding.btnRefresh.setVisibility(View.VISIBLE);

        }

    }


    public void onPause() {
        super.onPause();


    }

}