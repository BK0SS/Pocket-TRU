package com.example.pockettru;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WolfpackScheduleFragment extends Fragment {

    private WebView wolfpack_webview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wolfpack, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        wolfpack_webview = view.findViewById(R.id.wolfpack_webview);
        wolfpack_webview.setWebViewClient(new WebViewClient());

        WebSettings webSettings = wolfpack_webview.getSettings();

        webSettings.setJavaScriptEnabled(true);

        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowFileAccess(true);

        String wolfpackScheduleUrl = "https://gowolfpack.ca/calendar";
        wolfpack_webview.loadUrl(wolfpackScheduleUrl);
    }
}