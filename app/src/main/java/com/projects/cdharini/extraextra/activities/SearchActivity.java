package com.projects.cdharini.extraextra.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.projects.cdharini.extraextra.R;
import com.projects.cdharini.extraextra.adapters.NewsArticleAdapter;
import com.projects.cdharini.extraextra.models.NewsArticle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    EditText etSearchText;
    Button btnSearch;
    RecyclerView rvNewsGrid;
    List<NewsArticle> mNewsList;
    NewsArticleAdapter mAdapter;
    public static final String SEARCH_URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    public static final String API_KEY = "06f27f79f417483384e386c31ffc47a9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etSearchText = (EditText) findViewById(R.id.etSearch);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        rvNewsGrid = (RecyclerView) findViewById(R.id.rvNewsGrid);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mNewsList = new ArrayList<NewsArticle>();
        mAdapter = new NewsArticleAdapter(this, mNewsList);
        rvNewsGrid.setLayoutManager(layoutManager);
        rvNewsGrid.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSearchClick(View view) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("api-key", API_KEY);
        requestParams.put("page", "1");
        requestParams.put("q", etSearchText.getText().toString());
        client.get(SEARCH_URL, requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject searchResponse = response.getJSONObject("response");
                    JSONArray news = searchResponse.getJSONArray("docs");
                    mNewsList.clear();
                    mNewsList.addAll(NewsArticle.fromJSONArray(news));
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.d("DEBUG", "couldnt parse response");
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    /*public List<NewsArticle> getData() {
        List<NewsArticle> news = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            news.add(new NewsArticle("ic_launcher", "Title is " + i, "Synopsis is this is a new article"));
        }
        return news;
    }*/
}
