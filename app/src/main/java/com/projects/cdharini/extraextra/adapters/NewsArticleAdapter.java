package com.projects.cdharini.extraextra.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.cdharini.extraextra.R;
import com.projects.cdharini.extraextra.models.NewsArticle;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by dharinic on 9/19/17.
 */

public class NewsArticleAdapter extends RecyclerView.Adapter<NewsArticleAdapter.ViewHolder> {

    private Context mContext;
    private List<NewsArticle> mNewsArticles;

    public NewsArticleAdapter(Context context, List<NewsArticle> news) {
        mContext = context;
        mNewsArticles = news;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom layout
        View newsView = inflater.inflate(R.layout.item_news, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(newsView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NewsArticle newsArticle = mNewsArticles.get(position);
        holder.bind(newsArticle);
    }

    @Override
    public int getItemCount() {
        return mNewsArticles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivThumbnail;
        public TextView tvTitle;
        public TextView tvSynopsis;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ivThumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvSynopsis = (TextView) itemView.findViewById(R.id.tvSynopsis);
        }

        public void bind(NewsArticle newsArticle) {
                Picasso.with(mContext).load(newsArticle.getThumbnail())
                        .fit().centerInside().error(R.mipmap.ic_launcher).into(ivThumbnail);
            tvTitle.setText(newsArticle.getTitleText());
            tvSynopsis.setText(newsArticle.getSynopsis());
        }

        @Override
        public void onClick(View v) {
            Log.d("Dharini", "toasting click " + getOldPosition());
            Toast.makeText(mContext, tvTitle.getText().toString() + " is clicked!", Toast.LENGTH_SHORT).show();
        }
    }
}
