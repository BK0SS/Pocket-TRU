package com.example.pockettru;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MyTRUFragment extends Fragment {

    private WebView mytru_webview;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(mytru_webview == null){
            mytru_webview = new WebView(requireContext());


            mytru_webview.setWebViewClient(new WebViewClient());
            WebSettings webSettings = mytru_webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
            webSettings.setDatabaseEnabled(true);
            webSettings.setAllowFileAccess(true);

            if(savedInstanceState == null) {
                String myTruUrl = "https://mytru.tru.ca";
                mytru_webview.loadUrl(myTruUrl);
            }
        }
    }
    public void refresh(){
        if (mytru_webview != null){
            String myTruUrl = "https://mytru.tru.ca";
            mytru_webview.loadUrl(myTruUrl);
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_mytru, container, false);
        return mytru_webview;
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        //Save state of the webview
        if (mytru_webview != null){
            mytru_webview.saveState(outState);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);

            if(savedInstanceState != null){
                mytru_webview.restoreState(savedInstanceState);
            }

//        mytru_webview = view.findViewById(R.id.mytru_webview);
//        mytru_webview.setWebViewClient(new WebViewClient());
//
//        WebSettings webSettings = mytru_webview.getSettings();
//
//        webSettings.setJavaScriptEnabled(true);
//
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//
//        webSettings.setDatabaseEnabled(true);
//        webSettings.setAllowFileAccess(true);
//
//        String myTruUrl = "https://mytru.tru.ca";
//        mytru_webview.loadUrl(myTruUrl);
    }


        @Override
        public void onDestroyView(){
        if(mytru_webview != null){
            ViewGroup parent = (ViewGroup) mytru_webview.getParent();
            if(parent != null){
                parent.removeView(mytru_webview);
            }
        }
        super.onDestroyView();
    }
}