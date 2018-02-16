package com.eldonets.kivuzik.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.eldonets.kivuzik.Models.Recent_Activity_model_item;
import com.eldonets.kivuzik.R;
import com.eldonets.kivuzik.Utilities.Station_Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ST_004 on 15-12-2016.
 */

public class Adapter_Friends_activity extends ArrayAdapter<Recent_Activity_model_item> {
    Context mcontext;
    private List<Recent_Activity_model_item> noti = new ArrayList<>();
    private String TAG_TOP = "TOP";
    private LayoutInflater mInflater;
    Notifications_Adapter.ViewHolder viewHolder;
    public static boolean favourite = false;

    public static class ViewHolder {
        TextView txtv_name, txtv_time;
        ImageView Iv_image;
    }

    public Adapter_Friends_activity(Context context, ArrayList<Recent_Activity_model_item> tracks) {
        super(context, R.layout.notification_friends, tracks);
        this.mcontext = context;
        this.noti = tracks;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public Recent_Activity_model_item getItem(int position) {
        return noti.get(position);
    }

    public void setData(ArrayList<Recent_Activity_model_item> temp_list) {
        this.noti = temp_list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Recent_Activity_model_item user1 = noti.get(position);

        if (convertView == null || convertView.getTag().equals(TAG_TOP)) {
            viewHolder = new Notifications_Adapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.notification_friends, parent, false);
            viewHolder.txtv_name = (TextView) convertView.findViewById(R.id.txtv_name);
            viewHolder.txtv_time = (TextView) convertView.findViewById(R.id.txtv_time);
            viewHolder.Iv_image = (ImageView) convertView.findViewById(R.id.Iv_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Notifications_Adapter.ViewHolder) convertView.getTag();
        }

        String types = user1.getType();
        if (types.equalsIgnoreCase("like")) {
            String name = user1.getUsername();
            String song_title = user1.getTitle();
            String image = user1.getImage();
            String date = user1.getTime();
            String text = "<font color=#ffffff>" + name + "</font> <font color=#8C9591>liked a track - " + song_title + "</font>";
            viewHolder.txtv_name.setText(Html.fromHtml(text));

            String current_date1 = Station_Util.Time_Zone();
            long api_date = getDateInMillis(date);
            long current_time1 = getDateInMillis(current_date1);
           // Log.e("time CURRENT", String.valueOf(current_time1));

            viewHolder.txtv_time.setText(DateUtils.getRelativeTimeSpanString(api_date, current_time1, DateUtils.SECOND_IN_MILLIS));

            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(Color.BLACK)
                    .cornerRadiusDp(7)
                    .oval(false)
                    .build();
            Picasso.with(getContext()).load(Station_Util.IMAGE_URL_AVATARS+ image).fit().transform(transformation).placeholder(R.drawable.mic).into(viewHolder.Iv_image);


        } else if (types.equalsIgnoreCase("comment")) {
            String name = user1.getUsername();
            String image = user1.getImage();
            String song_title = user1.getTitle();
            String date = user1.getTime();
            String text = "<font color=#ffffff>" + name + "</font> <font color=#8C9591>commented on a track - " + song_title + "</font>";
            viewHolder.txtv_name.setText(Html.fromHtml(text));


            String current_date1 = Station_Util.Time_Zone();
            long api_date = getDateInMillis(date);
            long current_time1 = getDateInMillis(current_date1);

            viewHolder.txtv_time.setText(DateUtils.getRelativeTimeSpanString(api_date, current_time1, DateUtils.SECOND_IN_MILLIS));

            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(Color.BLACK)
                    .cornerRadiusDp(7)
                    .oval(false)
                    .build();
            Picasso.with(getContext()).load(Station_Util.IMAGE_URL_AVATARS+ image).fit().transform(transformation).placeholder(R.drawable.mic).into(viewHolder.Iv_image);

        }

        return convertView;
    }


    public static long getDateInMillis(String srcDate) {
        SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        long dateInMillis = 0;
        try {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            Log.e("DATE IN MILLIS", String.valueOf(dateInMillis));
            return dateInMillis;
        } catch (ParseException e) {
            // Log.d("Exception date. " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
}


           /* long final_time = current_time1 - api_date;

            Log.e("time FINAL", String.valueOf(final_time));

            long seconds = TimeUnit.MILLISECONDS.toSeconds(current_time1 - api_date);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(current_time1 - api_date);
            long hours = TimeUnit.MILLISECONDS.toHours(current_time1 - api_date);
            long days = TimeUnit.MILLISECONDS.toDays(current_time1 - api_date);

            if (seconds < 60) {
                System.out.println(seconds + " seconds ago");
                viewHolder.txtv_time.setText(seconds + " seconds ago");
            } else if (minutes < 60) {
                System.out.println(minutes + " minutes ago");
                viewHolder.txtv_time.setText(minutes + " minutes ago");
            } else if (hours < 24) {
                System.out.println(hours + " hours ago");
                viewHolder.txtv_time.setText(hours + " hours ago");
            } else {
                System.out.println(days + " days ago");
                viewHolder.txtv_time.setText(days + " days ago");
            }
*/

