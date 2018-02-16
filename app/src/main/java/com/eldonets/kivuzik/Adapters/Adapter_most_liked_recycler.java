package com.eldonets.kivuzik.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.eldonets.kivuzik.Activities.Player;
import com.eldonets.kivuzik.Models.Explore_model_item;
import com.eldonets.kivuzik.Preferences.MusicPlayer_Prefrence;
import com.eldonets.kivuzik.R;
import com.eldonets.kivuzik.Utilities.Station_Util;
import com.eldonets.kivuzik.admobadapter.ViewWrapper;
import com.eldonets.kivuzik.service.RadiophonyService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ST_004 on 27-01-2017.
 */

public class Adapter_most_liked_recycler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Explore_model_item> items = new ArrayList<Explore_model_item>();

    private Context mContext;

    public Adapter_most_liked_recycler(Context context) {
        mContext = context;
    }

    @Override
    public ViewWrapper<RecyclerViewExampleItem> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewWrapper<RecyclerViewExampleItem>(new RecyclerViewExampleItem(mContext));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        RecyclerViewExampleItem rvei = (RecyclerViewExampleItem) viewHolder.itemView;
        Explore_model_item Item = items.get(position);
        String image = Item.getArt();
        String title = Item.getTitle();

        rvei.bind(image, title);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // int position = mContext.getAdapterPosition();
                Explore_model_item item = items.get(position);
                if (RadiophonyService.exoPlayer != null) {
                    RadiophonyService.exoPlayer.stop();
                    mContext.stopService(new Intent(mContext, RadiophonyService.class));
                }
                MusicPlayer_Prefrence.getInstance(mContext).save(mContext, item);
                MusicPlayer_Prefrence.getInstance(mContext).save_position(position);

                mContext.startService(new Intent(mContext, RadiophonyService.class));
                RadiophonyService.initialize(mContext, item, 1);
                play(true);
                Intent i = new Intent(mContext, Player.class);
                i.putExtra("position", position);
                i.putExtra("from", "list_most_like");
                i.putExtra("track_detail", item);
                mContext.startActivity(i);
            }
        });


    }

    public void play(boolean play) {
        if (!play) {
            mContext.stopService(new Intent(mContext, RadiophonyService.class));
        } else {
            mContext.startService(new Intent(mContext, RadiophonyService.class));
        }
    }

    public class RecyclerViewExampleItem extends RelativeLayout {
        protected ImageView thumbnail;
        protected TextView titlesong;

        public RecyclerViewExampleItem(Context context) {
            super(context);
            inflate(context, R.layout.listcategory_row, this);
            this.thumbnail = (ImageView) findViewById(R.id.thumbnail);
            this.titlesong = (TextView) findViewById(R.id.title);
            this.titlesong.setSelected(true);
        }

        public void bind(String image, String title) {
            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(Color.BLACK)
                    .cornerRadiusDp(3)
                    .oval(false)
                    .build();
            Picasso.with(mContext).load(Station_Util.IMAGE_URL_MEDIA + image).fit().transform(transformation)
                    .placeholder(R.drawable.adele)
                    .into(thumbnail);
            titlesong.setText(title);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Explore_model_item getItem(int position) {
        return items.get(position);
    }

    public void addAll(List<Explore_model_item> lst) {
        items.addAll(lst);
    }
}
