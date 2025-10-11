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

public class MyTRUFragment extends Fragment {

    private WebView mytru_webview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mytru, container, false);
    }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
            super.onViewCreated(view, savedInstanceState);

            mytru_webview = view.findViewById(R.id.mytru_webview);
            mytru_webview.setWebViewClient(new WebViewClient());

            WebSettings webSettings = mytru_webview.getSettings();

            webSettings.setJavaScriptEnabled(true);

            webSettings.setDomStorageEnabled(true);
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

            webSettings.setDatabaseEnabled(true);
            webSettings.setAllowFileAccess(true);

            String wolfpackScheduleUrl = "https://mytru.tru.ca";
            mytru_webview.loadUrl(wolfpackScheduleUrl);
        }

}