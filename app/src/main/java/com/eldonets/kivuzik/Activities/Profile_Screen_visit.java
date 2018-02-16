package com.eldonets.kivuzik.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.eldonets.kivuzik.Models.Search_model_item;
import com.eldonets.kivuzik.R;
import com.eldonets.kivuzik.Utilities.Singering_Database;
import com.eldonets.kivuzik.Utilities.Station_Util;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Screen_visit extends AppCompatActivity implements View.OnClickListener {
    ImageButton btn_follow;
    ImageView Iv_cover, Iv_website, Iv_facebook, Iv_google, Iv_twitter, Iv_youtube, Iv_soundcloud, Iv_myspace, Iv_lastfm, Iv_vimeo, Iv_tumblr;
    TextView tv_count_tracks, txt_likes_count, txt_username, txt_username_profile, txt_city_country, tv_edit_settings, txt_about, tv_txt_person_detail;
    CircleImageView circleImageView;
    ImageButton Ib_backbtn;
    String str_user_id;
    ImageButton Ib_profile_image_edit;
    int position;
    Search_model_item model;
    public static boolean follow_check = false;
    private static String KEY_SUCCESS = "success";
    RequestParams requestParams;
    String my_user_id, uid;
    String str_facebook, str_website, str_twitter, str_google, str_youtube, str_soundcloud, str_myspace, str_lastfm, str_vimeo, str_tumblr, str_description;
    Singering_Database db;

    LinearLayout btn_tracks_list, btn_following_list, btn_likes_list, btn_follower_list;
    TextView txt_track_count, txt_following_count, txt_count_likes, txt_followers_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile__screen_visit);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }

        init_declare();
        db = new Singering_Database(Profile_Screen_visit.this);                      ////////////DATABASE

        intent_data_recieve();

       /* if (db.tableexist("followtable") == true) {
            db.deleteFollow();
        }*/

    }

    private void intent_data_recieve() {
        if (getIntent().hasExtra("profile_detail")) {
            model = (Search_model_item) getIntent().getSerializableExtra("profile_detail");

            position = getIntent().getIntExtra("position", 0);
            str_user_id = model.getUser_id();
        }
        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        if (str_user_id.equalsIgnoreCase(uid)) {
            btn_follow.setVisibility(View.GONE);
        }

        get_profile_data(str_user_id);
        get_Following_person_userprofile(str_user_id);


        String check1 = db.get_Follow(str_user_id);
        if (check1.equalsIgnoreCase("true")) {
            follow_check = true;
            btn_follow.setImageResource(R.drawable.remove_user);
        } else if (check1.equalsIgnoreCase("false")) {
            follow_check = false;
            btn_follow.setImageResource(R.drawable.add_user1);
        }
    }

    private void get_profile_data(String str_user_id) {
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        // Log.e("str_user_id", str_user_id);

        final ProgressDialog pDialog = new ProgressDialog(Profile_Screen_visit.this);

        client.get(Station_Util.URL + "visitorpro.php?vid=" + str_user_id, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(true);
                pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                String username = "", first_name = "", last_name = "", country = "", website = "", city = "", description = "", facebook = "",
                        twitter = "", gplus = "", youtube = "", vimeo = "", tumblr = "", soundcloud = "", myspace = "", lastfm = "", image = "", cover = "", totaltracks_count = "", follower_count = "", likes_count = "";
                Log.e("prnt", response.toString());
                try {
                    JSONObject obj = new JSONObject(response.toString());
                    String obq = obj.getString("search");
                    JSONArray array = new JSONArray(obq);
                    JSONObject obj1 = array.getJSONObject(0);
                    username = obj1.getString("username");
                    first_name = obj1.getString("first_name");
                    last_name = obj1.getString("last_name");
                    country = obj1.getString("country");
                    website = obj1.getString("website");
                    city = obj1.getString("city");
                    description = obj1.getString("description");
                    facebook = obj1.getString("facebook");
                    twitter = obj1.getString("twitter");
                    gplus = obj1.getString("gplus");
                    youtube = obj1.getString("youtube");
                    vimeo = obj1.getString("vimeo");
                    tumblr = obj1.getString("tumblr");
                    soundcloud = obj1.getString("soundcloud");
                    myspace = obj1.getString("myspace");
                    lastfm = obj1.getString("lastfm");
                    image = obj1.getString("image");
                    cover = obj1.getString("cover");
                    totaltracks_count = obj1.getString("totaltracks");
                    follower_count = obj1.getString("follower");
                    likes_count = obj1.getString("likes");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Picasso.with(getApplicationContext()).load(Station_Util.IMAGE_URL_AVATARS + image).into(circleImageView);
                Picasso.with(getApplicationContext()).load(Station_Util.IMAGE_URL_COVERS + cover).into(Iv_cover);
                txt_username_profile.setText(username);

                if (description.equalsIgnoreCase("")) {
                    txt_about.setVisibility(View.GONE);
                    tv_txt_person_detail.setVisibility(View.GONE);
                } else {
                    tv_txt_person_detail.setVisibility(View.VISIBLE);
                    txt_about.setVisibility(View.VISIBLE);
                    tv_txt_person_detail.setText(description);
                }
                tv_count_tracks.setText(totaltracks_count);
                txt_likes_count.setText(likes_count);
                txt_followers_count.setText(follower_count);
                txt_count_likes.setText(likes_count);
                txt_track_count.setText(totaltracks_count);

                if (city.equalsIgnoreCase("")) {
                    txt_city_country.setText(country);
                    txt_city_country.setVisibility(View.VISIBLE);
                } else if (country.equalsIgnoreCase("")) {
                    txt_city_country.setText(city);
                    txt_city_country.setVisibility(View.VISIBLE);
                } else if (city.equalsIgnoreCase("") && country.equalsIgnoreCase("")) {
                    txt_city_country.setVisibility(View.GONE);
                } else {
                    txt_city_country.setVisibility(View.VISIBLE);
                    txt_city_country.setText(city + "," + country);
                }

                txt_username.setText(first_name + " " + last_name);


                if (facebook.equalsIgnoreCase("")) {
                    Iv_facebook.setVisibility(View.GONE);
                } else {
                    Iv_facebook.setVisibility(View.VISIBLE);
                    str_facebook = facebook;
                }

                if (gplus.equalsIgnoreCase("")) {
                    Iv_google.setVisibility(View.GONE);
                } else {
                    Iv_google.setVisibility(View.VISIBLE);
                    str_google = gplus;
                }

                if (twitter.equalsIgnoreCase("")) {
                    Iv_twitter.setVisibility(View.GONE);
                } else {
                    Iv_twitter.setVisibility(View.VISIBLE);
                    str_twitter = twitter;
                }

                if (youtube.equalsIgnoreCase("")) {
                    Iv_youtube.setVisibility(View.GONE);
                } else {
                    Iv_youtube.setVisibility(View.VISIBLE);
                    str_youtube = youtube;
                }

                if (soundcloud.equalsIgnoreCase("")) {
                    Iv_soundcloud.setVisibility(View.GONE);
                } else {
                    Iv_soundcloud.setVisibility(View.VISIBLE);
                    str_soundcloud = soundcloud;
                }

                if (myspace.equalsIgnoreCase("")) {
                    Iv_myspace.setVisibility(View.GONE);
                } else {
                    Iv_myspace.setVisibility(View.VISIBLE);
                    str_myspace = myspace;
                }

                if (lastfm.equalsIgnoreCase("")) {
                    Iv_lastfm.setVisibility(View.GONE);
                } else {
                    Iv_lastfm.setVisibility(View.VISIBLE);
                    str_lastfm = lastfm;
                }

                if (vimeo.equalsIgnoreCase("")) {
                    Iv_vimeo.setVisibility(View.GONE);
                } else {
                    Iv_vimeo.setVisibility(View.VISIBLE);
                    str_vimeo = vimeo;
                }

                if (tumblr.equalsIgnoreCase("")) {
                    Iv_tumblr.setVisibility(View.GONE);
                } else {
                    Iv_tumblr.setVisibility(View.VISIBLE);
                    str_tumblr = tumblr;
                }

                if (website.equalsIgnoreCase("")) {
                    Iv_website.setVisibility(View.GONE);
                } else {
                    Iv_website.setVisibility(View.VISIBLE);
                    str_website = website;
                }

                pDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(Profile_Screen_visit.this, "Error", Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(Profile_Screen_visit.this, "Error", Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(Profile_Screen_visit.this, "Error", Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }
        });

    }


    private void get_Following_person_userprofile(String str_user_id) {


        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        client.get(Station_Util.URL + "follows.php?followingid=" + str_user_id, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("following");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                Log.e("count123", String.valueOf(array.length()));
                                txt_following_count.setText(String.valueOf(array.length()));
                            }
                        } else {
                            txt_following_count.setText("0");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void init_declare() {
        // btn_follower_list = (Button) findViewById(R.id.btn_follower_list);
        // btn_likes_list = (Button) findViewById(R.id.btn_likes_list);
        // btn_tracks_list = (Button) findViewById(R.id.btn_tracks_list);
        Iv_website = (ImageView) findViewById(R.id.Iv_website);
        Iv_facebook = (ImageView) findViewById(R.id.Iv_facebook);
        Iv_google = (ImageView) findViewById(R.id.Iv_google);
        Iv_twitter = (ImageView) findViewById(R.id.Iv_twitter);
        Iv_youtube = (ImageView) findViewById(R.id.Iv_youtube);
        Iv_soundcloud = (ImageView) findViewById(R.id.Iv_soundcloud);
        Iv_myspace = (ImageView) findViewById(R.id.Iv_myspace);
        Iv_lastfm = (ImageView) findViewById(R.id.Iv_lastfm);
        Iv_vimeo = (ImageView) findViewById(R.id.Iv_vimeo);
        Iv_tumblr = (ImageView) findViewById(R.id.Iv_tumblr);

        btn_follower_list = (LinearLayout) findViewById(R.id.btn_follower_list);
        btn_tracks_list = (LinearLayout) findViewById(R.id.btn_tracks_list);
        btn_following_list = (LinearLayout) findViewById(R.id.btn_following_list);
        btn_likes_list = (LinearLayout) findViewById(R.id.btn_likes_list);
        txt_track_count = (TextView) findViewById(R.id.txt_track_count);
        txt_following_count = (TextView) findViewById(R.id.txt_following_count);
        txt_count_likes = (TextView) findViewById(R.id.txt_count_likes);
        txt_followers_count = (TextView) findViewById(R.id.txt_followers_count);


        tv_txt_person_detail = (TextView) findViewById(R.id.tv_txt_person_detail);
        txt_about = (TextView) findViewById(R.id.txt_about);

        tv_edit_settings = (TextView) findViewById(R.id.tv_edit_settings);
        Iv_cover = (ImageView) findViewById(R.id.Iv_cover);
        txt_username_profile = (TextView) findViewById(R.id.txt_username_profile);
        txt_city_country = (TextView) findViewById(R.id.txt_city_country);
        circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        txt_likes_count = (TextView) findViewById(R.id.txt_likes_count);
        txt_username = (TextView) findViewById(R.id.txt_username);
        Ib_backbtn = (ImageButton) findViewById(R.id.Ib_backbtn);
        tv_count_tracks = (TextView) findViewById(R.id.tv_count_tracks);
        btn_follow = (ImageButton) findViewById(R.id.btn_follow);
        Ib_profile_image_edit = (ImageButton) findViewById(R.id.Ib_profile_image_edit);

        btn_follow.setOnClickListener(this);
        tv_edit_settings.setVisibility(View.GONE);
        circleImageView.setOnClickListener(this);
        Ib_backbtn.setOnClickListener(this);
        Iv_facebook.setOnClickListener(this);
        Iv_google.setOnClickListener(this);
        Iv_twitter.setOnClickListener(this);
        Iv_youtube.setOnClickListener(this);
        Iv_soundcloud.setOnClickListener(this);
        Iv_myspace.setOnClickListener(this);
        Iv_lastfm.setOnClickListener(this);
        Iv_vimeo.setOnClickListener(this);
        Iv_tumblr.setOnClickListener(this);
        Iv_website.setOnClickListener(this);
        btn_follower_list.setOnClickListener(this);
        btn_likes_list.setOnClickListener(this);
        btn_tracks_list.setOnClickListener(this);
        btn_following_list.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == Ib_backbtn) {
            onBackPressed();
        } else if (v == btn_follow) {
            if (follow_check == true) {
                set_Unfollow();
            } else if (follow_check == false) {
                set_follow();
            }
        } else if (v == btn_follower_list) {
            Intent i = new Intent(Profile_Screen_visit.this, Followers.class);
            i.putExtra("profile_user_id", str_user_id);
            startActivity(i);
        } else if (v == btn_following_list) {
            Intent i = new Intent(Profile_Screen_visit.this, Following.class);
            i.putExtra("profile_user_id", str_user_id);
            startActivity(i);
        } else if (v == btn_tracks_list) {
            Intent i = new Intent(Profile_Screen_visit.this, Profile_Tracks_added.class);
            i.putExtra("profile_user_id", str_user_id);
            startActivity(i);
        } else if (v == btn_likes_list) {
            Intent i = new Intent(Profile_Screen_visit.this, Profile_likes_added.class);
            i.putExtra("profile_user_id", str_user_id);
            startActivity(i);
        } else if (v == Iv_facebook) {
            Uri uri = Uri.parse("https://www.facebook.com/" + str_facebook); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (v == Iv_google) {
            Uri uri = Uri.parse("https://plus.google.com/" + str_google); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (v == Iv_twitter) {
            Uri uri = Uri.parse("https://twitter.com/" + str_twitter); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (v == Iv_youtube) {
            Uri uri = Uri.parse("https://www.youtube.com/user/" + str_youtube); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (v == Iv_soundcloud) {
            Uri uri = Uri.parse("https://soundcloud.com/" + str_soundcloud); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (v == Iv_myspace) {
            Uri uri = Uri.parse("https://myspace.com/" + str_myspace); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (v == Iv_lastfm) {
            Uri uri = Uri.parse("http://www.last.fm/user/" + str_lastfm); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (v == Iv_vimeo) {
            Uri uri = Uri.parse("https://vimeo.com/" + str_vimeo); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (v == Iv_tumblr) {
            Uri uri = Uri.parse("http://" + str_tumblr + ".tumblr.com/"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (v == Iv_website) {
            if (str_website.contains("http://")) {
                Uri uri = Uri.parse(str_website); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } else {
                str_website = "http://" + str_website;
                Uri uri = Uri.parse(str_website); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        }

    }

    private void set_follow() {

        my_user_id = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        requestParams = new RequestParams();
        requestParams.add("userid", my_user_id);
        requestParams.add("friendid", str_user_id);

        Log.e("check_values", "my_id=" + my_user_id + " , " + str_user_id);

        client.get(Station_Util.URL+"push.php?request=addfriend", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("Tag", statusCode + "");
                Log.d("Response", String.valueOf(response));

                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {
                            btn_follow.setImageResource(R.drawable.remove_user);
                            follow_check = true;

                            db.updateFollow(str_user_id, "true");


                            get_Following_person();

                            // db.updateLikes(id, "true");

                            //   int num = Integer.parseInt(str_likes) + 1;
                            //  str_likes = String.valueOf(num);
                            //setLikesNum();

                            // model.setCheck(true);

                            //getFavourites();
                        } else {
                            // btn_follow.setImageResource(R.drawable.add_user1);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                super.onSuccess(statusCode, headers, responseString);
            }
        });

    }

    private void set_Unfollow() {

        my_user_id = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        requestParams = new RequestParams();
        requestParams.add("uid", my_user_id);
        requestParams.add("requestid", str_user_id);

        Log.e("check_values", "my_id=" + my_user_id + " , " + str_user_id);

        client.get(Station_Util.URL + "follows.php?unfollow=yes", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("Tag", statusCode + "");
                Log.d("Response", String.valueOf(response));

                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {
                            btn_follow.setImageResource(R.drawable.add_user1);
                            follow_check = false;


                            db.updateFollow(str_user_id, "false");


                            get_Following_person();
                            // db.updateLikes(id, "true");

                            //   int num = Integer.parseInt(str_likes) + 1;
                            //  str_likes = String.valueOf(num);
                            //setLikesNum();

                            // model.setCheck(true);

                            //getFavourites();
                        } else {
                            // btn_follow.setImageResource(R.drawable.add_user1);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                super.onSuccess(statusCode, headers, responseString);
            }
        });

    }


    private void get_Following_person() {

        if (db.tableexist("followtable") == true) {
            db.deleteFollow();
        }

        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        // final ProgressDialog pDialog = new ProgressDialog(My_Profile_Screen.this);

        client.get(Station_Util.URL + "follows.php?followingid=" + uid, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                // pDialog.setMessage("Please wait...");
                // pDialog.setCancelable(true);
                // pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String user_id, username, first_name, last_name, country, city, image;


                Search_model_item model;
                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            My_following_activity.following_adapter_data.clear();

                            JSONArray array = response.getJSONArray("following");
                            for (int i = 0; i < array.length(); i++) {
                                model = new Search_model_item();
                                JSONObject obj = array.getJSONObject(i);
                                model.user_id = obj.getString("idu");
                                model.username = obj.getString("username");
                                model.first_name = obj.getString("first_name");
                                model.last_name = obj.getString("last_name");
                                model.country = obj.getString("country");
                                model.city = obj.getString("city");
                                model.image = obj.getString("image");

                                My_following_activity.following_adapter_data.add(model);


                                // if (checkFavItem(model)) {
                                db.insertFollow(model.user_id, "true");
                               /* } else {
                                    db.insertFollow(model.user_id, "false");
                                }*/

                            }
                        } else {

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //pDialog.dismiss();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }
        });
    }

/*    public boolean checkFavItem(Search_model_item checkfav) {
        boolean check = false;
        //   List<Inicio_station> recents = RadioUtil.arr_recentstations;

        List<Search_model_item> favs = My_following_activity.following_adapter_data;
        if (favs != null) {
            for (Search_model_item station_data : favs) {
                if (station_data.equals(checkfav)) {
                    check = true;
                    System.out.println("-----follow check true---" + check);
                    break;
                }
            }
        }
        System.out.println("-----follow check false---" + check);
        return check;
    }*/


}
