package com.projects.cdharini.extraextra.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for NYT news article
 * Created by dharinic on 9/19/17.
 */
@Parcel
public class NewsArticle{
    private static final String URL_PREFIX = "http://www.nytimes.com/";

    String mThumbnail;
    String mTitleText;
    String mSynopsis;
    String mWebUrl;

    /*
     * Constructor for Parceler library
     */
    public NewsArticle() {

    }

    public static NewsArticle fromJSON(JSONObject object) throws JSONException{
        NewsArticle newsArticle = new NewsArticle();
        newsArticle.mTitleText = object.getJSONObject("headline").getString("main");
        newsArticle.mSynopsis = object.getString("snippet");
        newsArticle.mWebUrl = object.getString("web_url");

        JSONArray multimedia = object.getJSONArray("multimedia");
        for (int i = 0; i < multimedia.length(); i ++) {
            JSONObject obj = multimedia.getJSONObject(i);
            if (obj.getString("type").equals("image")) {
                newsArticle.mThumbnail = obj.getString("url");
                break;
            }
        }

        return newsArticle;
    }

    public static List<NewsArticle> fromJSONArray(JSONArray array) {
        List<NewsArticle> newsArticles = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                newsArticles.add(fromJSON(array.getJSONObject(i)));
            } catch (JSONException e) {
                Log.e("DEBUG", "JSON exception while creating model");
            }
        }
        return newsArticles;
    }

    public String getThumbnail() {
        return (mThumbnail != null)? (URL_PREFIX + mThumbnail) : null;
    }

    public String getTitleText() {
        return mTitleText;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public String getWebUrl() {
        return mWebUrl;
    }
}
