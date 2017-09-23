package com.projects.cdharini.extraextra.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.projects.cdharini.extraextra.R;
import com.projects.cdharini.extraextra.models.NewsArticle;
import com.projects.cdharini.extraextra.utils.ExtraExtraConstants;

import org.parceler.Parcels;

/*
 * Activity that hosts a webView to display article
 */
public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        NewsArticle newsArticle = (NewsArticle)
                Parcels.unwrap(getIntent().getParcelableExtra(ExtraExtraConstants.ARTICLE));

        WebView wvArticle = (WebView) findViewById(R.id.wvArticle);
        // Configure related browser settings
        wvArticle.getSettings().setLoadsImagesAutomatically(true);
        wvArticle.getSettings().setJavaScriptEnabled(true);
        wvArticle.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // Configure the client to use when opening URLs
        wvArticle.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        // Load the URL
        wvArticle.loadUrl(newsArticle.getWebUrl());
    }
}
