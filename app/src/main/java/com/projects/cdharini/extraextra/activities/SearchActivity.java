package com.projects.cdharini.extraextra.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.projects.cdharini.extraextra.R;
import com.projects.cdharini.extraextra.adapters.NewsArticleAdapter;
import com.projects.cdharini.extraextra.fragments.FilterDialogFragment;
import com.projects.cdharini.extraextra.models.NewsArticle;
import com.projects.cdharini.extraextra.utils.EndlessRecyclerViewScrollListener;
import com.projects.cdharini.extraextra.utils.ExtraExtraConstants;
import com.projects.cdharini.extraextra.utils.ExtraExtraUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/*
 * Main activity that displays articles for user
 */
public class SearchActivity extends AppCompatActivity implements FilterDialogFragment.FilterDialogFragmentListener {

    // Views
    RecyclerView rvNewsGrid;

    List<NewsArticle> mNewsList;
    NewsArticleAdapter mAdapter;
    EndlessRecyclerViewScrollListener mScrollListener;

    // User preference filters
    String mSortPref;
    String mBeginDatePref;
    String mNewsDeskPref;
    String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Set support action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Get views and populate them with default i.e empty article list
        rvNewsGrid = (RecyclerView) findViewById(R.id.rvNewsGrid);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mNewsList = new ArrayList<>();
        mAdapter = new NewsArticleAdapter(this, mNewsList);

        mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                refreshArticles(false, page, mQuery);
            }
        };
        rvNewsGrid.setLayoutManager(layoutManager);
        rvNewsGrid.setAdapter(mAdapter);
        rvNewsGrid.addOnScrollListener(mScrollListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                refreshArticles(true, 0, query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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
            //Show dialog fragment
            FragmentManager fm = getSupportFragmentManager();
            FilterDialogFragment filterDialogFragment = FilterDialogFragment.newInstance("Testing", "test");
            filterDialogFragment.show(fm, "Filter");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishFilterDialog() {
        // Get shared prefs and save in this context
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mBeginDatePref = preferences.getString(ExtraExtraConstants.BEGIN_DATE_PREF, ExtraExtraConstants.BEGIN_DATE_DEFAULT);
        mNewsDeskPref = preferences.getString(ExtraExtraConstants.NEWS_DESK_PREF, "");
        mSortPref = preferences.getString(ExtraExtraConstants.SORT_ORDER_PREF, ExtraExtraConstants.SORT_DEFAULT);

        // Refresh articles
        refreshArticles(true, 0, mQuery);
    }

    /*
     * Fetches articles and refreshes adapter
     */
    public void refreshArticles(boolean clearExisting, int page, String query) {

        if (clearExisting) {
            mNewsList.clear();
            mAdapter.notifyDataSetChanged();
            mScrollListener.resetState();
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("api-key", ExtraExtraConstants.API_KEY);
        requestParams.put("page", "" + page);
        requestParams.put("q", query);
        requestParams.put(ExtraExtraConstants.BEGIN_DATE_PREF, mBeginDatePref);
        requestParams.put(ExtraExtraConstants.SORT_ORDER_PREF, mSortPref);
        requestParams.put(ExtraExtraConstants.NEWS_DESK_PREF, "news_desk:(" + mNewsDeskPref + ")");

        client.get(ExtraExtraConstants.SEARCH_URL, requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject searchResponse = response.getJSONObject("response");
                    JSONArray news = searchResponse.getJSONArray("docs");

                    mNewsList.addAll(NewsArticle.fromJSONArray(news));
                    mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), mNewsList.size() - 1);
                } catch (JSONException e) {
                    Log.d("DEBUG", "couldn't parse response");
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(SearchActivity.this, "Failed to fetch articles", Toast.LENGTH_LONG).show();
                if (!ExtraExtraUtils.isNetworkAvailable(SearchActivity.this) ||
                        !ExtraExtraUtils.isOnline()) {
                    Toast.makeText(SearchActivity.this,
                            "Unfortunately, there is no Internet now..Try again later",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });
    }
}
