package com.projects.cdharini.extraextra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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


        // Set support action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /* if (!ExtraExtraUtils.isNetworkAvailable(this) || !ExtraExtraUtils.isOnline()) {
            Toast.makeText(this, "Unfortunately, there is no Internet now..Try again later",
                    Toast.LENGTH_LONG).show();
            return;
        }*/

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        ShareActionProvider miShare = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        // get reference to WebView
        WebView wvArticle = (WebView) findViewById(R.id.wvArticle);
        // pass in the URL currently being used by the WebView
        shareIntent.putExtra(Intent.EXTRA_TEXT, wvArticle.getUrl());

        miShare.setShareIntent(shareIntent);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {

        }

        return super.onOptionsItemSelected(item);
    }
}
