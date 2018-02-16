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
import com.eldonets.kivuzik.Activities.Profile_Screen_visit;
import com.eldonets.kivuzik.Models.Search_model_item;
import com.eldonets.kivuzik.R;
import com.eldonets.kivuzik.Utilities.Station_Util;
import com.eldonets.kivuzik.admobadapter.ViewWrapper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ST_004 on 27-01-2017.
 */

public class Adapter_recent_users_recycler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Search_model_item> items = new ArrayList<Search_model_item>();

    private Context mContext;

    public Adapter_recent_users_recycler(Context context) {
        mContext = context;
    }

    @Override
    public ViewWrapper<RecyclerViewExampleItem> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewWrapper<RecyclerViewExampleItem>(new RecyclerViewExampleItem(mContext));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        RecyclerViewExampleItem rvei = (RecyclerViewExampleItem) viewHolder.itemView;
        Search_model_item Item = items.get(position);
        String image = Item.getImage();
        String title =Item.getUsername();

        rvei.bind(image,title);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // int position = mContext.getAdapterPosition();
                Search_model_item item = items.get(position);
                Intent i = new Intent(mContext, Profile_Screen_visit.class);
                i.putExtra("position", position);
                i.putExtra("profile_detail", item);
                mContext.startActivity(i);
            }
        });
    }

    public class RecyclerViewExampleItem extends RelativeLayout {
        protected ImageView thumbnail;
        protected TextView titlesong;

        public RecyclerViewExampleItem(Context context) {
            super(context);
            inflate(context, R.layout.list_category_row_two, this);
            this.thumbnail = (ImageView) findViewById(R.id.thumbnail);
            this.titlesong = (TextView) findViewById(R.id.title);
            this.titlesong.setSelected(true);
        }

        public void bind(String image,String title) {
            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(Color.BLACK)
                    .cornerRadiusDp(3)
                    .oval(false)
                    .build();
            Picasso.with(mContext).load(Station_Util.IMAGE_URL_AVATARS+ image).fit().transform(transformation)
                    .placeholder(R.drawable.adele)
                    .into(thumbnail);
            titlesong.setText(title);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Search_model_item getItem(int position) {
        return items.get(position);
    }

    public void addAll(List<Search_model_item> lst) {
        items.addAll(lst);
    }
}