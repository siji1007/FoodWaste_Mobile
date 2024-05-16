package com.example.foodwaste;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CustomLink extends DialogFragment {

    // URL of the webpage to load
    private String url;

    public CustomLink() {
        // Required empty public constructor
    }

    // Setter for the URL
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.custom_link, container, false);
        // Get WebView reference
        WebView webView = view.findViewById(R.id.webview);

        // Load URL in the WebView
        webView.loadUrl(url);

        // Enable JavaScript
        webView.getSettings().setJavaScriptEnabled(true);

        // Set WebView client to handle page navigation
        webView.setWebViewClient(new WebViewClient());

        return view;
    }
}
