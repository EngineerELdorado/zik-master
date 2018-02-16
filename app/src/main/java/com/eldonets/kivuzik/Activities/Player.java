package com.eldonets.kivuzik.Activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.eldonets.kivuzik.Adapters.Playlist_Adapter;
import com.eldonets.kivuzik.Models.Explore_model_item;
import com.eldonets.kivuzik.Models.Playlist_model_item;
import com.eldonets.kivuzik.Notifications.Notification_Serviceplay;
import com.eldonets.kivuzik.Preferences.MusicPlayer_Prefrence;
import com.eldonets.kivuzik.R;
import com.eldonets.kivuzik.Utilities.Blur;
import com.eldonets.kivuzik.Utilities.Singering_Database;
import com.eldonets.kivuzik.Utilities.Station_Util;
import com.eldonets.kivuzik.circleMenu.CircleActivity;
import com.eldonets.kivuzik.circleMenu.Guest_activity;
import com.eldonets.kivuzik.service.Parser;
import com.eldonets.kivuzik.service.RadiophonyService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cz.msebera.android.httpclient.Header;
import io.gresse.hugo.vumeterlibrary.VuMeterView;
import rm.com.audiowave.AudioWaveView;
import rm.com.audiowave.OnProgressListener;
import rm.com.audiowave.OnSamplingListener;


public class Player extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, OnProgressListener {
    ImageButton imgbtn_back_musicplayer, ImageButton_playlist;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();
    TextView Tv_start_time, Tv_end_time, txt_song_category, txt_song_name;
    public static ImageView Iv_btn_PlayPause, Iv_song_image;
    public static boolean message = true;
    public static String message2 = "";
    private static Context context;
    Explore_model_item model;
    String id, uid1, uid, user_id, title, description, first_name, last_name, name, tag, art, buy, record, release, license, size, download, time, likes, downloads, views;
    int position;
    RadiophonyService service;
    Timer timer;
    MyTimerTask myTimerTask;
    ImageView Iv_Next_Song, Iv_Previous_song, Iv_Lyrics, Iv_song_image_back, Iv_repeat, Iv_favorite, Iv_share_song, Iv_Comments, Iv_download_track;
    Uri uri;
    PopupWindow pwindo_menu;
    public static boolean clicked = false;
    public static boolean favourite = false;
    RequestParams requestParams;
    private static String KEY_SUCCESS = "success";
    String list_check, text_in_playlist_edittxt;
    Singering_Database db;
    String from = "";
    Explore_model_item radio;
    public ArrayList<Explore_model_item> array_list_player = new ArrayList<>();
    public Data_Recieverabc recieverabc;
    SharedPreferences pref;
    public static final String RECIEVER_NOTI_PLAYPAUSE1 = "Player1";
    public static ArrayList<Playlist_model_item> playlist_adapter_data = new ArrayList<>();
    EditText edttxt_create_playlist;
    Playlist_Adapter playlist_adapter;
    ListView listview_playlist;
    TextView txtv_no_playlist;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;
    // private ProgressDialog mProgressDialog;
    File file;
    DownloadTask downloadTask;
    Bitmap bitmap, bitmap_back;
    public static VuMeterView mVuMeterView;
    public static boolean mEnableAnimation;
    private Target loadtarget;
    String imageART;
    View rootView;
    static SecretKey yourKey = null;
    private static String algorithm = "AES";
    byte[] fileplay_temp;
    public DonutProgress donut_progress;
    FrameLayout progress_area;
    boolean disableEvent;
    AudioWaveView waveView;
    SeekBar seekbar;
    byte[] result;
    byte[] music;
    private File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_player);
        initDeclare();
        disableEvent = false;

        Window window = Player.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Player.this.getResources().getColor(R.color.grey_dark));
        }


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        db = new Singering_Database(Player.this);                      ////////////DATABASE

        context = this;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        Iv_btn_PlayPause.setImageResource(R.drawable.ic_pause_button);
        message = true;

        recieverabc = new Data_Recieverabc();
        registerReceiver(recieverabc, new IntentFilter(RECIEVER_NOTI_PLAYPAUSE1));

        if (RadiophonyService.exoPlayer == null) {
            service = RadiophonyService.getInstance();
            play(true);
        }
        phone_state_listener_check();


        if (getIntent().hasExtra("from")) {
            from = getIntent().getStringExtra("from");
        }
        array_list_player.clear();
        if (from.equalsIgnoreCase("fav_list")) {
            array_list_player.clear();
            array_list_player = Likes_activity.fav_adapter_data;
            Log.e("list_fav", String.valueOf(array_list_player.size()));

            pref = context.getSharedPreferences("logincheck", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(Likes_activity.fav_adapter_data);
            edit.putString("list_check", "fav_list");
            edit.putString("arraylist", json);
            edit.apply();

            intentData();
        } else if (from.equalsIgnoreCase("my_downloads")) {
            array_list_player.clear();
            array_list_player = My_Downloads.list_stations;
            Log.e("list_my_download", String.valueOf(array_list_player.size()));

            pref = context.getSharedPreferences("logincheck", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(My_Downloads.list_stations);
            edit.putString("list_check", "my_downloads");
            edit.putString("arraylist", json);
            edit.apply();

            intentData_two();
        } else if (from.equalsIgnoreCase("popular_list")) {
            array_list_player.clear();
            boolean guest = getSharedPreferences("guest_check", MODE_PRIVATE).getBoolean("guest", false);
            if (guest == true) {
                array_list_player = Guest_activity.list_recycler_mst_popular;
            } else {
                array_list_player = CircleActivity.list_recycler_mst_popular;
            }
            Log.e("list_popular", String.valueOf(array_list_player.size()));

            pref = context.getSharedPreferences("logincheck", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(array_list_player);
            edit.putString("list_check", "popular_list");
            edit.putString("arraylist", json);
            edit.apply();

            intentData();
        } else if (from.equalsIgnoreCase("song_lists")) {
            array_list_player.clear();
            array_list_player = CircleActivity.adapter_data;
            Log.e("list_song", String.valueOf(array_list_player.size()));

            pref = context.getSharedPreferences("logincheck", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(CircleActivity.adapter_data);
            edit.putString("list_check", "song_lists");
            edit.putString("arraylist", json);
            edit.apply();

            intentData();
        } else if (from.equalsIgnoreCase("recycler_fav_list")) {
            array_list_player.clear();
            array_list_player = CircleActivity.list_recycler_fav;
            Log.e("list_recycler_fav", String.valueOf(array_list_player.size()));

            pref = context.getSharedPreferences("logincheck", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(CircleActivity.list_recycler_fav);
            edit.putString("list_check", "recycler_fav_list");
            edit.putString("arraylist", json);
            edit.apply();

            intentData();
        } else if (from.equalsIgnoreCase("recent_recycler")) {
            array_list_player.clear();

            boolean guest = getSharedPreferences("guest_check", MODE_PRIVATE).getBoolean("guest", false);
            if (guest == true) {
                array_list_player = Guest_activity.list_recycler_recent;
            } else {
                array_list_player = CircleActivity.list_recycler_recent;
            }
            Log.e("list_recent", String.valueOf(array_list_player.size()));

            pref = context.getSharedPreferences("logincheck", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(array_list_player);
            edit.putString("list_check", "recent_recycler");
            edit.putString("arraylist", json);
            edit.apply();

            intentData();
        } else if (from.equalsIgnoreCase("list_most_like")) {
            array_list_player.clear();

            boolean guest = getSharedPreferences("guest_check", MODE_PRIVATE).getBoolean("guest", false);
            if (guest == true) {
                array_list_player = Guest_activity.list_recycler_most_likes;
            } else {
                array_list_player = CircleActivity.list_recycler_most_likes;
            }

            Station_Util.check_category = "list_most_like";
            Log.e("list_most_likes", String.valueOf(array_list_player.size()));

            pref = context.getSharedPreferences("logincheck", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(array_list_player);
            edit.putString("list_check", "list_most_like");
            edit.putString("arraylist", json);
            edit.apply();

            intentData();
        } else if (from.equals("drawer_player")) {                                       //////////FROM DRAWER
            radio = MusicPlayer_Prefrence.getInstance(Player.this).getSave(Player.this);
            position = MusicPlayer_Prefrence.getInstance(Player.this).Get_Saved_Position();

            id = radio.getId();
            Log.e("saved_track_id", id);

            boolean check_from_downloads = getSharedPreferences("logincheck", MODE_PRIVATE).getBoolean("check_from_downloads", false);

            if (check_from_downloads == true) {
                imageART = radio.getImageART();
                Iv_song_image.setImageResource(R.drawable.mic_icon);
                CircleActivity.Iv_track_image.setImageResource(R.drawable.mic_icon);
                Picasso.with(context).load(R.drawable.mic_icon).transform(new Blur(Player.this, 25)).into(Iv_song_image_back);

                Iv_Comments.setVisibility(View.GONE);
                Iv_favorite.setVisibility(View.GONE);
                ImageButton_playlist.setVisibility(View.GONE);
                Iv_Lyrics.setVisibility(View.GONE);
                Iv_share_song.setVisibility(View.GONE);
            } else {
                Transformation transformation1 = new RoundedTransformationBuilder()
                        .borderColor(Color.BLACK)
                        .cornerRadiusDp(5)
                        .oval(false)
                        .build();
                if (radio.getArt().contains(".gif")) {
                    Picasso.with(context).load(Station_Util.IMAGE_URL_MEDIA + radio.getArt()).fit().transform(transformation1).into(Iv_song_image);
                    Picasso.with(context).load(Station_Util.IMAGE_URL_MEDIA + radio.getArt()).into(Iv_song_image_back);
                } else {
                    Picasso.with(context).load(Station_Util.IMAGE_URL_MEDIA + radio.getArt()).fit().transform(transformation1).into(Iv_song_image);
                    Picasso.with(context).load(Station_Util.IMAGE_URL_MEDIA + radio.getArt()).transform(new Blur(Player.this, 25)).into(Iv_song_image_back);
                }

                boolean guest = getSharedPreferences("guest_check", MODE_PRIVATE).getBoolean("guest", false);
                if (guest == true) {
                    Picasso.with(context).load(Station_Util.IMAGE_URL_MEDIA + radio.getArt()).fit()
                            .transform(transformation1).into(Guest_activity.Iv_track_image);
                } else {
                    Picasso.with(context).load(Station_Util.IMAGE_URL_MEDIA + radio.getArt()).fit()
                            .transform(transformation1).into(CircleActivity.Iv_track_image);
                }

                Station_Util.Song_image = Station_Util.IMAGE_URL_MEDIA + radio.getArt();
            }


            Iv_song_image.setAlpha(0.6f);
            txt_song_name.setText(radio.getTitle());
            txt_song_name.setSelected(true);
            txt_song_category.setText(radio.getTag());

            Station_Util.Song_name = radio.getTitle();
            Station_Util.tag = radio.getTag();
            Station_Util.Song_image = Station_Util.IMAGE_URL_MEDIA + radio.getArt();

            uid1 = radio.getUid();


            boolean guest = getSharedPreferences("guest_check", MODE_PRIVATE).getBoolean("guest", false);
            if (guest == true) {
                Guest_activity.frame_footer.setVisibility(View.VISIBLE);
                Guest_activity.Tv_footer_track_name.setText(radio.getTitle());
                Guest_activity.Tv_footer_track_name.setSelected(true);
                Guest_activity.Tv_footer_tag_name.setText(radio.getTag());
                Guest_activity.Tv_footer_tag_name.setSelected(true);
                Iv_download_track.setVisibility(View.GONE);
                Iv_Comments.setVisibility(View.GONE);
                Iv_favorite.setVisibility(View.GONE);
                ImageButton_playlist.setVisibility(View.GONE);
            } else {
                CircleActivity.frame_footer.setVisibility(View.VISIBLE);
                CircleActivity.Tv_footer_track_name.setText(radio.getTitle());
                CircleActivity.Tv_footer_track_name.setSelected(true);
                CircleActivity.Tv_footer_tag_name.setText(radio.getTag());
                CircleActivity.Tv_footer_tag_name.setSelected(true);

                String download = getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("public", "");
                if (radio.getDownload().equalsIgnoreCase("0")) {
                    Iv_download_track.setVisibility(View.GONE);
                } else if (radio.getDownload().equalsIgnoreCase("")) {
                    Iv_download_track.setVisibility(View.GONE);
                } else if (download.equalsIgnoreCase("1")) {
                    Iv_download_track.setVisibility(View.VISIBLE);
                } else {
                    Iv_download_track.setVisibility(View.VISIBLE);
                }
            }


        /*    if (Station_Util.checkended == ExoPlayer.STATE_ENDED) {
                //  Station_Util.state_ended = true;

                boolean guest1 = getSharedPreferences("guest_check", MODE_PRIVATE).getBoolean("guest", false);
                if (guest1 == true) {
                    Guest_activity.frame_footer.setVisibility(View.GONE);
                } else {
                    CircleActivity.frame_footer.setVisibility(View.GONE);
                }
                Player.Iv_btn_PlayPause.setImageResource(R.drawable.ic_play_button);
                Player.mVuMeterView.pause();
                startService(new Intent(context, Notification_Serviceplay.class));
                Player.message = false;
            } else {
            }*/

            if (radio.getName().endsWith(".m3u")) {
                uri = Uri.parse(Parser.parse(radio.getName()));
            } else {
                uri = Uri.parse(radio.getName());
            }

            if (message == true) {
                Iv_btn_PlayPause.setImageResource(R.drawable.ic_pause_button);
            } else if (message == false) {
                Iv_btn_PlayPause.setImageResource(R.drawable.ic_play_button);
            }

            String check1 = db.get_like(radio.getId());
            if (check1.equalsIgnoreCase("true")) {
                Iv_favorite.setImageResource(R.drawable.ic_favorite_fill);
            } else if (check1.equalsIgnoreCase("false") || check1.equalsIgnoreCase("")) {
                Iv_favorite.setImageResource(R.drawable.ic_favorite_empty);
            }

            list_check = getSharedPreferences("logincheck", MODE_PRIVATE).getString("list_check", "");
            List_CHECK_FROM_WHERE(list_check);

            if (position == 0) {
                if (array_list_player.size() == 1) {
                    Iv_Previous_song.setClickable(false);
                    Iv_Next_Song.setClickable(false);
                    Iv_Previous_song.setAlpha(0.5f);
                    Iv_Next_Song.setAlpha(0.5f);
                } else {
                    Iv_Previous_song.setAlpha(0.5f);
                    Iv_Previous_song.setClickable(false);
                    Iv_Next_Song.setClickable(true);
                    Iv_Next_Song.setAlpha(1f);
                }
            } else if (position == array_list_player.size() - 1) {
                Iv_Next_Song.setAlpha(0.5f);
                Iv_Next_Song.setClickable(false);
                Iv_Previous_song.setAlpha(1f);
                Iv_Previous_song.setClickable(true);
            }
            startService(new Intent(Player.this, Notification_Serviceplay.class));
        }          ///from drawer ends

        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000);

        if (clicked == false) {
            Iv_repeat.setImageResource(R.drawable.ic_repeat_button_unclicked);
        } else if (clicked == true) {
            Iv_repeat.setImageResource(R.drawable.ic_repeat_button_clicked);
        }

    }     ///////////////////////////////////////////////////////////////////////////// END OF ONCREATE

    private void List_CHECK_FROM_WHERE(String list_check) {
        if (list_check.equalsIgnoreCase("song_lists")) {
            pref = context.getSharedPreferences("logincheck", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = pref.getString("arraylist", null);
            Type type = new TypeToken<ArrayList<Explore_model_item>>() {
            }.getType();
            array_list_player = gson.fromJson(json, type);
            Log.e("Arraylist_check", String.valueOf(array_list_player.size()));
        } else if (list_check.equalsIgnoreCase("recycler_fav_list")) {
            pref = context.getSharedPreferences("logincheck", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = pref.getString("arraylist", null);
            Type type = new TypeToken<ArrayList<Explore_model_item>>() {
            }.getType();
            array_list_player = gson.fromJson(json, type);
            Log.e("Arraylist_recyclrfav", String.valueOf(array_list_player.size()));
        } else if (list_check.equalsIgnoreCase("fav_list")) {
            pref = context.getSharedPreferences("logincheck", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = pref.getString("arraylist", null);
            Type type = new TypeToken<ArrayList<Explore_model_item>>() {
            }.getType();
            array_list_player = gson.fromJson(json, type);
            Log.e("Arraylist_checkfav", String.valueOf(array_list_player.size()));
        } else if (list_check.equalsIgnoreCase("my_downloads")) {
            pref = context.getSharedPreferences("logincheck", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = pref.getString("arraylist", null);
            Type type = new TypeToken<ArrayList<Explore_model_item>>() {
            }.getType();
            array_list_player = gson.fromJson(json, type);
            Log.e("Arraylist_checkmydownd", String.valueOf(array_list_player.size()));
        } else if (list_check.equalsIgnoreCase("recent_recycler")) {
            pref = context.getSharedPreferences("logincheck", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = pref.getString("arraylist", null);
            Type type = new TypeToken<ArrayList<Explore_model_item>>() {
            }.getType();
            array_list_player = gson.fromJson(json, type);
            Log.e("Arraylist_recntrecy", String.valueOf(array_list_player.size()));
        } else if (list_check.equalsIgnoreCase("popular_list")) {
            pref = context.getSharedPreferences("logincheck", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = pref.getString("arraylist", null);
            Type type = new TypeToken<ArrayList<Explore_model_item>>() {
            }.getType();
            array_list_player = gson.fromJson(json, type);
            Log.e("Arraylist_populr", String.valueOf(array_list_player.size()));
        } else if (list_check.equalsIgnoreCase("list_most_like")) {
            pref = context.getSharedPreferences("logincheck", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = pref.getString("arraylist", null);
            Type type = new TypeToken<ArrayList<Explore_model_item>>() {
            }.getType();
            array_list_player = gson.fromJson(json, type);
            Log.e("Arraylist_mstlikes", String.valueOf(array_list_player.size()));
        }
    }


    public ExoPlayer.Listener onCompletionListener = new ExoPlayer.Listener() {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == ExoPlayer.STATE_ENDED) {
                Log.e("---state----", "get" + RadiophonyService.exoPlayer.getPlaybackState());
                Log.e("---current pos----", "get" + RadiophonyService.exoPlayer.getCurrentPosition());

                if (clicked == true) {
                } else {
                    Play_next_song();
                    RadiophonyService.exoPlayer.removeListener(this);
                }
            }
        }

        @Override
        public void onPlayWhenReadyCommitted() {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }
    };


    private void phone_state_listener_check() {
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {

                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    try {
                        if (RadiophonyService.exoPlayer != null) {
                            service = RadiophonyService.getInstance();
                            if (message == true) {
                                Iv_btn_PlayPause.setImageResource(R.drawable.ic_play_button);
                                service.pause();
                                mVuMeterView.pause();
                                boolean guest = getSharedPreferences("guest_check", MODE_PRIVATE).getBoolean("guest", false);
                                if (guest == true) {
                                    Guest_activity.frame_footer.setVisibility(View.GONE);
                                } else {
                                    CircleActivity.frame_footer.setVisibility(View.GONE);
                                }

                                message = false;
                                startService(new Intent(Player.this, Notification_Serviceplay.class));
                            }
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }
                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    try {
                        if (RadiophonyService.exoPlayer != null) {
                            service = RadiophonyService.getInstance();
                            if (message == false) {


                            } else {
                                Iv_btn_PlayPause.setImageResource(R.drawable.ic_pause_button);
                                service.start();
                                boolean guest = getSharedPreferences("guest_check", MODE_PRIVATE).getBoolean("guest", false);
                                if (guest == true) {
                                    Guest_activity.frame_footer.setVisibility(View.VISIBLE);
                                } else {
                                    CircleActivity.frame_footer.setVisibility(View.VISIBLE);
                                }
                                mVuMeterView.resume(mEnableAnimation);
                                message = true;
                                startService(new Intent(Player.this, Notification_Serviceplay.class));
                            }
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }
                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    try {
                        if (RadiophonyService.exoPlayer != null) {
                            service = RadiophonyService.getInstance();
                            if (message == true) {
                                Iv_btn_PlayPause.setImageResource(R.drawable.ic_play_button);
                                service.pause();
                                mVuMeterView.pause();
                                boolean guest = getSharedPreferences("guest_check", MODE_PRIVATE).getBoolean("guest", false);
                                if (guest == true) {
                                    Guest_activity.frame_footer.setVisibility(View.GONE);
                                } else {
                                    CircleActivity.frame_footer.setVisibility(View.GONE);
                                }
                                message = false;
                                startService(new Intent(Player.this, Notification_Serviceplay.class));
                            }
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }
                /*    try {
                        if (RadiophonyService.getInstance().isPlaying()) {
                            RadiophonyService.Mute();
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }*/

                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (tManager != null)
            tManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);

        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private void intentData_two() {

        if (getIntent().hasExtra("track_detail")) {
            model = (Explore_model_item) getIntent().getSerializableExtra("track_detail");

            position = getIntent().getIntExtra("position", 0);
            id = model.getId();
            Log.e("id_check", id);
            uid1 = model.getUid();
            title = model.getTitle();
            description = model.getDescription();
            first_name = model.getFirst_name();
            last_name = model.getLast_name();
            name = model.getName();
            tag = model.getTag();
            art = model.getArt();
            buy = model.getBuy();
            record = model.getRecord();
            release = model.getRelease();
            license = model.getLicense();
            size = model.getSize();
            download = model.getDownload();
            time = model.getTime();
            likes = model.getLikes();
            downloads = model.getDownloads();
            views = model.getViews();
            imageART = model.getImageART();
            //   bitmap = StringToBitMap(imageART);
            //   bitmap_back = StringToBitMap(imageART);


            pref = context.getSharedPreferences("logincheck", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            edit.putBoolean("check_from_downloads", true);
            edit.apply();

            //    Station_Util.check_from_downloads=true;
            //  Bitmap bitmap =model.getBitmap();
        }
        Iv_download_track.setVisibility(View.GONE);
        Iv_Comments.setVisibility(View.GONE);
        Iv_favorite.setVisibility(View.GONE);
        ImageButton_playlist.setVisibility(View.GONE);
        Iv_Lyrics.setVisibility(View.GONE);
        Iv_share_song.setVisibility(View.GONE);

        Iv_song_image.setAlpha(0.6f);
        txt_song_name.setText(title);
        txt_song_name.setSelected(true);

       /* Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),R.drawable.mic_icon);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, bao);
        byte [] ba = bao.toByteArray();
        String ba1=Base64.encodeToString(ba,Base64.DEFAULT);*/

        Station_Util.Song_name = title;
        Station_Util.Song_image = "";
        Station_Util.tag = tag;

        CircleActivity.Tv_footer_track_name.setText(title);
        CircleActivity.Tv_footer_track_name.setSelected(true);
        CircleActivity.Tv_footer_tag_name.setText(tag);
        CircleActivity.Tv_footer_tag_name.setSelected(true);

        Iv_song_image.setImageResource(R.drawable.mic_icon);
        CircleActivity.Iv_track_image.setImageResource(R.drawable.mic_icon);
        Picasso.with(context).load(R.drawable.mic_icon).transform(new Blur(Player.this, 25)).into(Iv_song_image_back);

        //  Iv_song_image.setImageBitmap(bitmap);
        // CircleActivity.Iv_track_image.setImageBitmap(bitmap);
        // Bitmap blurredBitmap = new Blur(this, 25).transform(bitmap_back);
        // Iv_song_image_back.setImageBitmap(blurredBitmap);


        startService(new Intent(Player.this, Notification_Serviceplay.class));
        txt_song_category.setText(tag);

        if (position == 0) {
            if (array_list_player.size() == 1) {
                Iv_Previous_song.setClickable(false);
                Iv_Next_Song.setClickable(false);
                Iv_Previous_song.setAlpha(0.5f);
                Iv_Next_Song.setAlpha(0.5f);
            } else {
                Iv_Previous_song.setAlpha(0.5f);
                Iv_Previous_song.setClickable(false);
                Iv_Next_Song.setClickable(true);
                Iv_Next_Song.setAlpha(1f);
            }
        } else if (position == array_list_player.size() - 1) {
            Iv_Next_Song.setAlpha(0.5f);
            Iv_Next_Song.setClickable(false);
            Iv_Previous_song.setAlpha(1f);
            Iv_Previous_song.setClickable(true);
        }
        // decodeFile();
    }                                                     ////////////    END oF Intentdata_two


    @Override
    public void onStartTracking(float v) {
        Log.e("Progress_check", String.valueOf(v));
        Log.e("Progress_check_", "" + v);

        startTime = RadiophonyService.exoPlayer.getCurrentPosition();
        waveView.setProgress((float) startTime);
    }

    @Override
    public void onStopTracking(float v) {

    }

    @Override
    public void onProgressChanged(float v, boolean b) {

        Log.e("Progress_check1", String.valueOf(v));
        Log.e("Progress_check1_", "" + v);

        RadiophonyService.exoPlayer.seekTo((long) v);

    }


    public class Data_Recieverabc extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("-----player reciever-----");
            service = RadiophonyService.getInstance();
            if (message == true) {
                Iv_btn_PlayPause.setImageResource(R.drawable.ic_pause_button);
                service.start();
            } else if (message == false) {
                Iv_btn_PlayPause.setImageResource(R.drawable.ic_play_button);
                service.pause();
            }
        }
    }

    public void play(boolean toPlay) {
        if (!toPlay) {
            stopService(new Intent(Player.this, RadiophonyService.class));
        } else {
            startService(new Intent(Player.this, RadiophonyService.class));
        }
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalTime = RadiophonyService.exoPlayer.getDuration();
                    if (finalTime == -1) {
                        timer = new Timer();
                        myTimerTask = new MyTimerTask();
                        timer.schedule(myTimerTask, 1000);
                    } else {
                        finalTime = RadiophonyService.exoPlayer.getDuration();
                        startTime = RadiophonyService.exoPlayer.getCurrentPosition();
                        Log.e("+++TIME_CHECK+++start", (int) startTime + "++++final" + (int) finalTime);

                        Tv_end_time.setText(String.format(Locale.ENGLISH, "%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));
                        Tv_start_time.setText(String.format(Locale.ENGLISH, "%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));

                        seekbar.setProgress((int) startTime);

                        //waveView.setProgress((int) startTime);

                        seekbar.setMax((int) finalTime);
                        myHandler.postDelayed(UpdateSongTime, 100);
                        RadiophonyService.exoPlayer.addListener(onCompletionListener);
                    }
                }
            });
        }
    }

    private void intentData() {
        if (getIntent().hasExtra("track_detail")) {
            model = (Explore_model_item) getIntent().getSerializableExtra("track_detail");
            position = getIntent().getIntExtra("position", 0);
            id = model.getId();
            Log.e("id_check", id);
            uid1 = model.getUid();
            title = model.getTitle();
            description = model.getDescription();
            first_name = model.getFirst_name();
            last_name = model.getLast_name();
            name = model.getName();
            tag = model.getTag();
            art = model.getArt();
            buy = model.getBuy();
            record = model.getRecord();
            release = model.getRelease();
            license = model.getLicense();
            size = model.getSize();
            download = model.getDownload();
            time = model.getTime();
            likes = model.getLikes();
            downloads = model.getDownloads();
            views = model.getViews();

            pref = context.getSharedPreferences("logincheck", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            edit.putBoolean("check_from_downloads", false);
            edit.apply();


            final byte[] data = {1, 3, 37, 117, 69, 0, 0, 58, 45, 36, 54, 26, 84, 24, 74, 32, 23, 65, 34, 54};

            waveView.setScaledData(data);
            waveView.setRawData(data, new OnSamplingListener() {
                @Override
                public void onComplete() {

                }
            });

        }
        Log.e("TRACK_PATH", name);
      /*  String artgif=art.substring(art.lastIndexOf(".gif"));
        if (artgif.contains(".gif"))*/

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .cornerRadiusDp(5)
                .oval(false)
                .build();
        if (art.contains(".gif")) {
            Picasso.with(context).load(Station_Util.IMAGE_URL_MEDIA + art).fit().transform(transformation).into(Iv_song_image);
            Picasso.with(context).load(Station_Util.IMAGE_URL_MEDIA + art).into(Iv_song_image_back);
        } else {
            Picasso.with(context).load(Station_Util.IMAGE_URL_MEDIA + art).fit().transform(transformation).into(Iv_song_image);
            Picasso.with(context).load(Station_Util.IMAGE_URL_MEDIA + art)
                    .transform(new Blur(Player.this, 25)).into(Iv_song_image_back);
        }

        try {
            PASS_TRACK_PATH();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iv_song_image.setAlpha(0.6f);
        txt_song_name.setText(title);
        txt_song_name.setSelected(true);

        boolean guest = getSharedPreferences("guest_check", MODE_PRIVATE).getBoolean("guest", false);
        if (guest == true) {
            Iv_download_track.setVisibility(View.GONE);
            Iv_Comments.setVisibility(View.GONE);
            Iv_favorite.setVisibility(View.GONE);
            ImageButton_playlist.setVisibility(View.GONE);
            Guest_activity.frame_footer.setVisibility(View.VISIBLE);
            Guest_activity.Tv_footer_track_name.setText(title);
            Guest_activity.Tv_footer_track_name.setSelected(true);
            Guest_activity.Tv_footer_tag_name.setText(tag);
            Guest_activity.Tv_footer_tag_name.setSelected(true);
            Picasso.with(context).load(Station_Util.IMAGE_URL_MEDIA + art).fit()
                    .transform(transformation).into(Guest_activity.Iv_track_image);
        } else {
            CircleActivity.frame_footer.setVisibility(View.VISIBLE);
            CircleActivity.Tv_footer_track_name.setText(title);
            CircleActivity.Tv_footer_track_name.setSelected(true);
            CircleActivity.Tv_footer_tag_name.setText(tag);
            CircleActivity.Tv_footer_tag_name.setSelected(true);
            Picasso.with(context).load(Station_Util.IMAGE_URL_MEDIA + art).fit()
                    .transform(transformation).into(CircleActivity.Iv_track_image);

            if (download.equalsIgnoreCase("0")) {
                Iv_download_track.setVisibility(View.GONE);
            } else {
                Iv_download_track.setVisibility(View.VISIBLE);
            }

        }

        Station_Util.Song_name = title;
        Station_Util.Song_image = Station_Util.IMAGE_URL_MEDIA + art;
        Station_Util.tag = tag;

        startService(new Intent(Player.this, Notification_Serviceplay.class));
        txt_song_category.setText(tag);

        if (position == 0) {
            if (array_list_player.size() == 1) {
                Iv_Previous_song.setClickable(false);
                Iv_Next_Song.setClickable(false);
                Iv_Previous_song.setAlpha(0.5f);
                Iv_Next_Song.setAlpha(0.5f);
            } else {
                Iv_Previous_song.setAlpha(0.5f);
                Iv_Previous_song.setClickable(false);
                Iv_Next_Song.setClickable(true);
                Iv_Next_Song.setAlpha(1f);
            }
        } else if (position == array_list_player.size() - 1) {
            Iv_Next_Song.setAlpha(0.5f);
            Iv_Next_Song.setClickable(false);
            Iv_Previous_song.setAlpha(1f);
            Iv_Previous_song.setClickable(true);
        }


        String check = db.get_like(model.getId());
        if (check.equalsIgnoreCase("true")) {
            favourite = true;
            Iv_favorite.setImageResource(R.drawable.ic_favorite_fill);
        } else if (check.equalsIgnoreCase("false") || check.equalsIgnoreCase("")) {
            favourite = false;
            Iv_favorite.setImageResource(R.drawable.ic_favorite_empty);
        }
        set_VIEW_COUNT();

    }               ///////////////////END OF INTENTDATA();

    public static byte[] convertStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[10240];
        int i = Integer.MAX_VALUE;
        while ((i = is.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }

        return baos.toByteArray(); // be sure to close InputStream in calling function
    }


    public byte[] inputStreamToByteArray(InputStream inStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inStream.read(buffer)) > 0) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }

    private void set_VIEW_COUNT() {
        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        client.get(Station_Util.URL + "total_views.php?userid=" + uid + "&trackid=" + id, new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("VIEW COUNT", String.valueOf(response));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //  Toast.makeText(Player.this, "Error Check Connection", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void set_download_count() {
        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        client.get(Station_Util.URL + "total_downloads.php?userid=" + uid + "&trackid=" + id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("VIEW Download", String.valueOf(response));
                disableEvent = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //  Toast.makeText(Player.this, "Error Check Connection", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void initDeclare() {
        waveView = (AudioWaveView) findViewById(R.id.wave);
        waveView.setOnProgressListener(this);

        progress_area = (FrameLayout) findViewById(R.id.progress_area);
        donut_progress = (DonutProgress) findViewById(R.id.donut_progress);
        mVuMeterView = (VuMeterView) findViewById(R.id.vumeter);
        ImageButton_playlist = (ImageButton) findViewById(R.id.ImageButton_playlist);
        imgbtn_back_musicplayer = (ImageButton) findViewById(R.id.imgbtn_back_musicplayer);
        Iv_favorite = (ImageView) findViewById(R.id.Iv_favorite);
        Iv_Comments = (ImageView) findViewById(R.id.Iv_Comments);
        Iv_download_track = (ImageView) findViewById(R.id.Iv_download_track);
        //Tv_start_time = (TextView) findViewById(R.id.Tv_start_time);
        Iv_share_song = (ImageView) findViewById(R.id.Iv_share_song);
        txt_song_name = (TextView) findViewById(R.id.txt_song_name);
        Iv_repeat = (ImageView) findViewById(R.id.Iv_repeat);
        Iv_song_image_back = (ImageView) findViewById(R.id.Iv_song_image_back);
        Iv_Previous_song = (ImageView) findViewById(R.id.Iv_Previous_song);
        Iv_Next_Song = (ImageView) findViewById(R.id.Iv_Next_Song);
        Iv_Lyrics = (ImageView) findViewById(R.id.Iv_Lyrics);
        txt_song_category = (TextView) findViewById(R.id.txt_song_category);
        Iv_btn_PlayPause = (ImageView) findViewById(R.id.Iv_btn_PlayPause);
        Iv_song_image = (ImageView) findViewById(R.id.Iv_song_image);
        Tv_start_time = (TextView) findViewById(R.id.Tv_start_time);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        Tv_end_time = (TextView) findViewById(R.id.Tv_end_time);
        Iv_btn_PlayPause.setOnClickListener(this);
        seekbar.setOnSeekBarChangeListener(this);
        Iv_Next_Song.setOnClickListener(this);
        Iv_Previous_song.setOnClickListener(this);
        Iv_share_song.setOnClickListener(this);
        Iv_Lyrics.setOnClickListener(this);
        Iv_repeat.setOnClickListener(this);
        Iv_favorite.setOnClickListener(this);
        imgbtn_back_musicplayer.setOnClickListener(this);
        ImageButton_playlist.setOnClickListener(this);
        Iv_Comments.setOnClickListener(this);
        Iv_download_track.setOnClickListener(this);

        //mProgressDialog = new ProgressDialog(Player.this);
    }

    @Override
    public void onClick(View v) {
        if (v == imgbtn_back_musicplayer) {
            onBackPressed();
        } else if (v == Iv_Comments) {
            Intent i = new Intent(Player.this, Comments_activity.class);
            i.putExtra("trackid", id);
            i.putExtra("userid", uid1);
            startActivity(i);
        } else if (v == Iv_download_track) {
           /* mProgressDialog.setMessage("Downloading file. Please wait...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);*/
            downloadTask = new DownloadTask(Player.this);
            downloadTask.execute(name);
            disableEvent = true;
            //progress_area.setVisibility(View.VISIBLE); // Showing the stylish material progressbar

        } else if (v == Iv_btn_PlayPause) {
            service = RadiophonyService.getInstance();
            if (RadiophonyService.exoPlayer != null) {
                if (message == true) {
                    Iv_btn_PlayPause.setImageResource(R.drawable.ic_play_button);
                    service.pause();
                    mVuMeterView.pause();

                    boolean guest = getSharedPreferences("guest_check", MODE_PRIVATE).getBoolean("guest", false);
                    if (guest == true) {
                        Guest_activity.frame_footer.setVisibility(View.GONE);
                    } else {
                        CircleActivity.frame_footer.setVisibility(View.GONE);
                    }
                    message = false;
                    startService(new Intent(Player.this, Notification_Serviceplay.class));
                } else if (message == false) {
                 /*   if (Station_Util.state_ended == true) {
                        stopService(new Intent(Player.this, RadiophonyService.class));
                        startService(new Intent(Player.this, RadiophonyService.class));
                        timer = new Timer();
                        myTimerTask = new MyTimerTask();

                        boolean guest = getSharedPreferences("guest_check", MODE_PRIVATE).getBoolean("guest", false);
                        if (guest == true) {
                            Guest_activity.frame_footer.setVisibility(View.GONE);
                        } else {
                            CircleActivity.frame_footer.setVisibility(View.GONE);
                        }
                        timer.schedule(myTimerTask, 1000);
                        Iv_btn_PlayPause.setImageResource(R.drawable.ic_pause_button);
                        message = true;
                        Station_Util.state_ended = false;
                    } else*/
                    Iv_btn_PlayPause.setImageResource(R.drawable.ic_pause_button);
                    service.start();

                    boolean guest = getSharedPreferences("guest_check", MODE_PRIVATE).getBoolean("guest", false);
                    if (guest == true) {
                        Guest_activity.frame_footer.setVisibility(View.VISIBLE);
                    } else {
                        CircleActivity.frame_footer.setVisibility(View.VISIBLE);
                    }
                    mVuMeterView.resume(mEnableAnimation);
                    message = true;
                    startService(new Intent(Player.this, Notification_Serviceplay.class));
                }
            }
        } else if (v == ImageButton_playlist) {
            show_pop_up_playlist();
        } else if (v == Iv_Next_Song) {
            if (array_list_player != null) {
                Play_next_song();
            } else {
                Toast.makeText(context, "No track found", Toast.LENGTH_SHORT).show();
            }
        } else if (v == Iv_Previous_song) {
            if (array_list_player != null) {
                Play_previous_song();
            } else {
                Toast.makeText(context, "No track found", Toast.LENGTH_SHORT).show();
            }
        } else if (v == Iv_share_song) {
            takeScreenshot();
     /*       if (from.equals("drawer_player")) {
                *//*Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "I am listening to " + radio.getTitle() + " on Singering. Download the app from playstore : https://play.google.com/store/apps/details?id=com.sekhontech";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Singering");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));*//*
            } else {
              *//*  Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "I am listening to " + title + " on Singering.
                Download the app from playstore : https://play.google.com/store/apps/details?id=com.sekhontech";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Singering");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));*//*
            }*/
        } else if (v == Iv_Lyrics) {
            if (from.equals("drawer_player")) {
                if (radio.getDescription().equalsIgnoreCase("")) {
                    Toast.makeText(Player.this, "No Info Available", Toast.LENGTH_SHORT).show();
                } else {
                    String text_station = radio.getDescription();
                    show_pop_up(text_station);
                }
            } else {
                if (description.equalsIgnoreCase("")) {
                    Toast.makeText(Player.this, "No Info Available", Toast.LENGTH_SHORT).show();
                } else {
                    String song_desc = description;
                    show_pop_up(song_desc);
                }
            }
        } else if (v == Iv_repeat) {
            if (clicked == false) {
                Iv_repeat.setImageResource(R.drawable.ic_repeat_button_clicked);
                clicked = true;
            } else if (clicked == true) {
                Iv_repeat.setImageResource(R.drawable.ic_repeat_button_unclicked);
                clicked = false;
            }
        } else if (v == Iv_favorite) {
            if (favourite == true) {
                Remove_fav_data();
            } else if (favourite == false) {
                Set_Favorite_data();
            }
        }
    }


    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        try {
            // image naming and path  to include sd card  appending name you choose for file
            String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
            File dir = new File(dirPath);
            if (!dir.exists())
                dir.mkdirs();

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().findViewById(android.R.id.content);
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(dirPath + "/" + now + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            shareImage(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }

    private void shareImage(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*/plain");
        String shareBody;

        if (from.equals("drawer_player")) {
            shareBody = "je suis entrain d'écouter " + radio.getTitle() +
                    " sur " + getResources().getString(R.string.app_name) + ". telecharges l'application maintenant sur playstore : " + getResources().getString(R.string.app_link);
        } else {
            shareBody = "je suis entrain d'écouter " + title +
                    " sur " + getResources().getString(R.string.app_name) + ". telecharges l'application maintenant sur playstore :" + getResources().getString(R.string.app_link);
        }
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(intent, "Partager La capture d'écran"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Application non disponible", Toast.LENGTH_SHORT).show();
        }
    }


    private void PASS_TRACK_PATH() throws IOException {

        File folder = new File(Environment.getExternalStorageDirectory() +
                "/" + getResources().getString(R.string.app_name));
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            file = new File(folder, "Downloads");
            file.mkdir();
        }
    }


    private void show_pop_up_playlist() {

        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) Player.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_playlist, (ViewGroup) findViewById(R.id.popup1));

            RelativeLayout mainlayout = (RelativeLayout) layout.findViewById(R.id.popup1);

            playlist_adapter = new Playlist_Adapter(Player.this, playlist_adapter_data, 0);

            edttxt_create_playlist = (EditText) layout.findViewById(R.id.edttxt_create_playlist);
            listview_playlist = (ListView) layout.findViewById(R.id.listview_playlist);
            // TextView txt_station_detail = (TextView) layout.findViewById(R.id.txt_station_detail);
            // txt_station_detail.setText(description);
            txtv_no_playlist = (TextView) layout.findViewById(R.id.txtv_no_playlist);

            Button btn_add_playlist = (Button) layout.findViewById(R.id.btn_add_playlist);
            btn_add_playlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text_in_playlist_edittxt = edttxt_create_playlist.getText().toString();
                    send_playlist_data(text_in_playlist_edittxt);
                }
            });

            listview_playlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Playlist_model_item model = playlist_adapter_data.get(position);
                    add_to_playlist(model.id);

                }
            });

            get_playlist_data();

            mainlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//   pwindo_menu.dismiss();
                }
            });
            Button btn_close_playlist = (Button) layout.findViewById(R.id.btn_close_playlist);
            btn_close_playlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo_menu.dismiss();
                }
            });
            RelativeLayout rel = (RelativeLayout) layout.findViewById(R.id.closelayout);
            rel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // pwindo_menu.dismiss();
                }
            });
            pwindo_menu = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
            pwindo_menu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));////custom dialog Box
            pwindo_menu.showAtLocation(layout, Gravity.CENTER, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void add_to_playlist(String id_playlist) {

        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        requestParams = new RequestParams();
        requestParams.add("playlistid", id_playlist);
        requestParams.add("trackid", id);


        final ProgressDialog pDialog = new ProgressDialog(Player.this);

        client.post(this, Station_Util.URL + "playlist.php?playlistentery=true", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                pDialog.setMessage("veillez patienter...");
                pDialog.setCancelable(true);
                pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                //Log.d("Tag12345", statusCode + "");
                // Log.d("Response_image", String.valueOf(response));
                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {
                            Toast.makeText(getApplicationContext(), "ajouté sur la playlist", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "ca existe deja", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDialog.dismiss();
                // Toast.makeText(Player.this, "Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDialog.dismiss();
                //   Toast.makeText(Player.this, "Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                pDialog.dismiss();
                //  Toast.makeText(Player.this, "Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pDialog.dismiss();
            }
        });
    }


    private void send_playlist_data(String playlist_name) {

        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        requestParams = new RequestParams();
        requestParams.add("uid", uid);
        requestParams.add("listname", playlist_name);
        requestParams.add("description", "");
        requestParams.add("access", "1");

        final ProgressDialog pDialog = new ProgressDialog(Player.this);

        client.post(this, Station_Util.URL + "playlist.php?playlist=true", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(true);
                pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("Tag12345", statusCode + "");
                Log.d("Response_image", String.valueOf(response));
                edttxt_create_playlist.setText("");

                get_playlist_data();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDialog.dismiss();
                //  Toast.makeText(Player.this, "Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDialog.dismiss();
                //  Toast.makeText(Player.this, "Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pDialog.dismiss();
            }
        });
    }


    private void get_playlist_data() {

        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        requestParams = new RequestParams();
        requestParams.add("uid", uid);

        //  final ProgressDialog pDialog = new ProgressDialog(Player.this);

        client.post(this, Station_Util.URL + "playlist.php?showlist=show", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                //   pDialog.setMessage("Please wait...");
                //   pDialog.setCancelable(true);
                //  pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // Log.d("Tag12345", statusCode + "");
                //Log.d("Response_image", String.valueOf(response));
                try {
                    playlist_adapter_data.clear();

                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("show-list");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String id = obj.getString("id");
                                String by = obj.getString("by");
                                String name = obj.getString("name");
                                String description = obj.getString("description");
                                String value_check = obj.getString("public");
                                playlist_adapter_data.add(new Playlist_model_item(id, by, name, description, value_check, "", ""));

                                hasDuplicates(playlist_adapter_data);
                            }
                        }
                    } else {
                        Toast.makeText(Player.this, "No result found", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (playlist_adapter_data.isEmpty()) {
                    txtv_no_playlist.setVisibility(View.VISIBLE);
                    listview_playlist.setVisibility(View.GONE);
                } else {
                    listview_playlist.setVisibility(View.VISIBLE);
                    txtv_no_playlist.setVisibility(View.GONE);
                    playlist_adapter.setData(playlist_adapter_data);
                    listview_playlist.setAdapter(playlist_adapter);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //   Toast.makeText(Player.this, "Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void hasDuplicates(List<Playlist_model_item> p_cars) {
        final List<String> usedNames = new ArrayList<>();
        Iterator<Playlist_model_item> it = p_cars.iterator();
        while (it.hasNext()) {
            Playlist_model_item car = it.next();
            final String name = car.getId();
            if (usedNames.contains(name)) {
                it.remove();
            } else {
                usedNames.add(name);
            }
        }
    }

    private void Play_previous_song() {                        //////////PLAY PREVIOUS TRACK

        if (RadiophonyService.exoPlayer != null) {
            RadiophonyService.exoPlayer.stop();
            stopService(new Intent(Player.this, RadiophonyService.class));
        }
        startService(new Intent(Player.this, RadiophonyService.class));

        service = RadiophonyService.getInstance();
        Explore_model_item radio = RadiophonyService.getInstance().getPlayingRadioStation();
        Log.e("list", String.valueOf(array_list_player.size()));

        position = array_list_player.indexOf(radio);
        if (position == 0) {
            Iv_Next_Song.setAlpha(1f);
            Iv_Next_Song.setClickable(true);
            Iv_Previous_song.setAlpha(0.5f);
            Iv_Previous_song.setClickable(false);
        } else {
            position = position - 1;
        }


        if (position >= 0) {
            if (array_list_player.get(position).getName() != null) {
                if (array_list_player.get(position).getName().endsWith(".m3u")) {
                    uri = Uri.parse(Parser.parse(array_list_player.get(position).getName()));
                } else {
                    uri = Uri.parse(array_list_player.get(position).getName());
                }
                txt_song_name.setText(array_list_player.get(position).getTitle());
                txt_song_category.setText(array_list_player.get(position).getTag());
                service.playprevious(array_list_player.get(position));
                Iv_btn_PlayPause.setImageResource(R.drawable.ic_pause_button);
                mVuMeterView.resume(mEnableAnimation);
                message = true;
                Station_Util.Song_name = array_list_player.get(position).getTitle();
                Station_Util.Song_image = Station_Util.IMAGE_URL_MEDIA + array_list_player.get(position).getArt();
                Station_Util.tag = array_list_player.get(position).getTag();
                startService(new Intent(Player.this, Notification_Serviceplay.class));

                check_from_Downloads();
            }
        }
        if (position < array_list_player.size() - 1) {
            Iv_Next_Song.setAlpha(1f);
            Iv_Next_Song.setClickable(true);
            Iv_Previous_song.setAlpha(1f);
            Iv_Previous_song.setClickable(true);
        }
        if (position == 0) {
            Iv_Next_Song.setAlpha(1f);
            Iv_Next_Song.setClickable(true);
            Iv_Previous_song.setAlpha(0.5f);
            Iv_Previous_song.setClickable(false);
        }
        method_to_save_next_track_info();
    }


    public void Play_next_song() {            /////////////////PLAY NEXT TRACK

        if (RadiophonyService.exoPlayer != null) {
            RadiophonyService.exoPlayer.stop();
            stopService(new Intent(Player.this, RadiophonyService.class));
        }
        startService(new Intent(Player.this, RadiophonyService.class));
        service = RadiophonyService.getInstance();
        Explore_model_item radio = RadiophonyService.getInstance().getPlayingRadioStation();
        Log.e("list", String.valueOf(array_list_player.size()));
        position = array_list_player.indexOf(radio);

        if (position == array_list_player.size() - 1) {
            Iv_Next_Song.setAlpha(0.5f);
            Iv_Next_Song.setClickable(false);
            Iv_Previous_song.setAlpha(1f);
            Iv_Previous_song.setClickable(true);
        } else {
            position = position + 1;
        }

        if (position < array_list_player.size()) {
            if (array_list_player.get(position).getName() != null) {
                if (array_list_player.get(position).getName().endsWith(".m3u")) {
                    uri = Uri.parse(Parser.parse(array_list_player.get(position).getName()));
                } else {
                    uri = Uri.parse(array_list_player.get(position).getName());
                }

                txt_song_name.setText(array_list_player.get(position).getTitle());
                txt_song_category.setText(array_list_player.get(position).getTag());
                service.playNext(array_list_player.get(position));
                Iv_btn_PlayPause.setImageResource(R.drawable.ic_pause_button);
                mVuMeterView.resume(mEnableAnimation);
                message = true;
                Station_Util.Song_name = array_list_player.get(position).getTitle();
                Station_Util.Song_image = Station_Util.IMAGE_URL_MEDIA + array_list_player.get(position).getArt();
                Station_Util.tag = array_list_player.get(position).getTag();
                startService(new Intent(Player.this, Notification_Serviceplay.class));

                check_from_Downloads();
            }
            if (position > 0) {
                Iv_Previous_song.setAlpha(1f);
                Iv_Previous_song.setClickable(true);
            }
            method_to_save_next_track_info();
        }
    }

    private void check_from_Downloads() {


        boolean check_from_downloads = getSharedPreferences("logincheck", MODE_PRIVATE).getBoolean("check_from_downloads", false);
        if (check_from_downloads == true) {
            imageART = array_list_player.get(position).getImageART();

            Iv_song_image.setImageResource(R.drawable.mic_icon);
            CircleActivity.Iv_track_image.setImageResource(R.drawable.mic_icon);
            CircleActivity.Tv_footer_track_name.setText(array_list_player.get(position).getTitle());
            CircleActivity.Tv_footer_track_name.setSelected(true);
            Station_Util.Song_image = "";
            CircleActivity.Tv_footer_tag_name.setText(array_list_player.get(position).getTag());
            CircleActivity.Tv_footer_tag_name.setSelected(true);
            Picasso.with(context).load(R.drawable.mic_icon).transform(new Blur(Player.this, 25)).into(Iv_song_image_back);

            Iv_Comments.setVisibility(View.GONE);
            Iv_favorite.setVisibility(View.GONE);
            ImageButton_playlist.setVisibility(View.GONE);
            Iv_Lyrics.setVisibility(View.GONE);
            Iv_share_song.setVisibility(View.GONE);

        } else {
            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(Color.BLACK)
                    .cornerRadiusDp(5)
                    .oval(false)
                    .build();
            if (array_list_player.get(position).getArt().contains(".gif")) {
                Picasso.with(context).load(Station_Util.IMAGE_URL_MEDIA + array_list_player.get(position).getArt())
                        .fit().transform(transformation).into(Iv_song_image);
                Picasso.with(context).load(Station_Util.IMAGE_URL_MEDIA + array_list_player.get(position).getArt())
                        .into(Iv_song_image_back);
            } else {
                Picasso.with(context).load(Station_Util.IMAGE_URL_MEDIA + array_list_player.get(position).getArt())
                        .fit().transform(transformation).into(Iv_song_image);
                Picasso.with(context).load(Station_Util.IMAGE_URL_MEDIA + array_list_player.get(position).getArt())
                        .transform(new Blur(Player.this, 25)).into(Iv_song_image_back);
            }
            Iv_song_image.setAlpha(0.6f);


            boolean guest = getSharedPreferences("guest_check", MODE_PRIVATE).getBoolean("guest", false);
            if (guest == true) {
                Guest_activity.frame_footer.setVisibility(View.VISIBLE);
                Guest_activity.Tv_footer_track_name.setText(array_list_player.get(position).getTitle());
                Guest_activity.Tv_footer_track_name.setSelected(true);
                Guest_activity.Tv_footer_tag_name.setText(array_list_player.get(position).getTag());
                Guest_activity.Tv_footer_tag_name.setSelected(true);
                Picasso.with(context).load(Station_Util.IMAGE_URL_MEDIA + array_list_player.get(position).getArt()).fit()
                        .transform(transformation).into(Guest_activity.Iv_track_image);
            } else {
                CircleActivity.frame_footer.setVisibility(View.VISIBLE);
                CircleActivity.Tv_footer_track_name.setText(array_list_player.get(position).getTitle());
                CircleActivity.Tv_footer_track_name.setSelected(true);
                CircleActivity.Tv_footer_tag_name.setText(array_list_player.get(position).getTag());
                CircleActivity.Tv_footer_tag_name.setSelected(true);
                Picasso.with(context).load(Station_Util.IMAGE_URL_MEDIA + array_list_player.get(position).getArt()).fit()
                        .transform(transformation).into(CircleActivity.Iv_track_image);

                if (array_list_player.get(position).getDownload().equalsIgnoreCase("0")) {
                    Iv_download_track.setVisibility(View.GONE);
                } else if (array_list_player.get(position).getDownload().equalsIgnoreCase("")) {
                    Iv_download_track.setVisibility(View.GONE);
                } else {
                    Iv_download_track.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void method_to_save_next_track_info() {
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000);

        model = new Explore_model_item();
        model.id = array_list_player.get(position).getId();
        id = model.id;
        model.uid = array_list_player.get(position).getUid();
        uid1 = model.uid;
        model.title = array_list_player.get(position).getTitle();
        title = model.title;
        model.description = array_list_player.get(position).getDescription();
        description = model.description;
        from = "";
        model.first_name = array_list_player.get(position).getFirst_name();
        model.last_name = array_list_player.get(position).getLast_name();
        model.name = array_list_player.get(position).getName();
        name = model.name;
        model.tag = array_list_player.get(position).getTag();
        model.art = array_list_player.get(position).getArt();
        model.buy = array_list_player.get(position).getBuy();
        model.record = array_list_player.get(position).getRecord();
        model.release = array_list_player.get(position).getRelease();
        model.license = array_list_player.get(position).getLicense();
        model.size = array_list_player.get(position).getSize();
        model.download = array_list_player.get(position).getDownload();
        model.time = array_list_player.get(position).getTime();
        model.likes = array_list_player.get(position).getLikes();
        model.downloads = array_list_player.get(position).getDownloads();
        model.views = array_list_player.get(position).getViews();
        model.imageART = array_list_player.get(position).getImageART();

        MusicPlayer_Prefrence.getInstance(Player.this).save(Player.this, model);
        MusicPlayer_Prefrence.getInstance(Player.this).save_position(position);

        String check = db.get_like(array_list_player.get(position).getId());
        if (check.equalsIgnoreCase("true")) {
            favourite = true;
            Iv_favorite.setImageResource(R.drawable.ic_favorite_fill);
        } else if (check.equalsIgnoreCase("false") || check.equalsIgnoreCase("")) {
            favourite = false;
            Iv_favorite.setImageResource(R.drawable.ic_favorite_empty);
        }
    }


    private void show_pop_up(String description) {

        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) Player.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.pop_up_hint,
                    (ViewGroup) findViewById(R.id.menupopup1));

            RelativeLayout mainlayout = (RelativeLayout) layout.findViewById(R.id.menupopup1);

            TextView txt_station_detail = (TextView) layout.findViewById(R.id.txt_station_detail);
            txt_station_detail.setText(description);

            mainlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //   pwindo_menu.dismiss();
                }
            });

            LinearLayout lin_exit = (LinearLayout) layout.findViewById(R.id.linear_exit);
            lin_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo_menu.dismiss();
                }
            });
            pwindo_menu = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
            pwindo_menu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));////custom dialog Box
            pwindo_menu.showAtLocation(layout, Gravity.CENTER, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = RadiophonyService.exoPlayer.getCurrentPosition();
            Tv_start_time.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime))));
            // Tv_start_time.setBase((long) startTime);
            seekbar.setProgress((int) startTime);

            // waveView.setProgress((int) startTime);
            seekbar.setSecondaryProgress((int) RadiophonyService.exoPlayer.getBufferedPosition());


            //  Log.e("Percentage", String.valueOf(RadiophonyService.exoPlayer.getBufferedPosition()));
            myHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (RadiophonyService.exoPlayer != null && fromUser) {
            RadiophonyService.exoPlayer.seekTo(progress);
        }
        int check = Double.compare(startTime, finalTime);

        if (check > 0) {
           /* Iv_btn_PlayPause.setImageResource(R.drawable.ic_play_button);
            message = false;*/
            if (clicked == true) {
                stopService(new Intent(Player.this, RadiophonyService.class));
                startService(new Intent(Player.this, RadiophonyService.class));
                timer = new Timer();
                myTimerTask = new MyTimerTask();
                timer.schedule(myTimerTask, 1000);
                Iv_btn_PlayPause.setImageResource(R.drawable.ic_pause_button);
                message = true;
            } else {
               /* Iv_btn_PlayPause.setImageResource(R.drawable.ic_play_button);
                message = false;*/
            }
        } else if (check < 0) {
          /*  Iv_btn_PlayPause.setImageResource(R.drawable.ic_pause_button);
            message = true;*/
        } else {
          /*  Iv_btn_PlayPause.setImageResource(R.drawable.ic_play_button);
            message = false;*/
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(recieverabc);
        myHandler.removeCallbacksAndMessages(UpdateSongTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(reciever);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // unregisterReceiver(reciever);
    }

    @Override
    public void onBackPressed() {
        if (disableEvent == false) {
            super.onBackPressed();
            sendBroadcast(new Intent(Profile_likes_added.RECIEVER_DATA_1));
            sendBroadcast(new Intent(CircleActivity.RECIEVER_DATA));
        } else {

        }
    }


    private void Set_Favorite_data() {
        Iv_favorite.setClickable(false);
        user_id = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        //requestParams = new RequestParams();
        // requestParams.add("trackid", id);

        //  Log.e("url_like", "http://singering.com/myapi/likes.php?like=true&userid=" + user_id + "&trackid=" + id);

        client.get(Station_Util.URL + "push.php?request=getrequest&type=likes&track=" + id + "&by=" + user_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("Tag", statusCode + "");
                Log.d("Response", String.valueOf(response));
                //  Toast.makeText(context,String.valueOf(response),Toast.LENGTH_SHORT).show();
                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {
                            Iv_favorite.setImageResource(R.drawable.ic_favorite_fill);
                            favourite = true;

                            db.updateLikes(id, "true");
                            Iv_favorite.setClickable(true);
                            getFavourites();
                        } else {
                            Iv_favorite.setImageResource(R.drawable.ic_favorite_empty);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Iv_favorite.setClickable(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Iv_favorite.setClickable(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Iv_favorite.setClickable(true);
            }
        });
    }

    private void getFavourites() {

        if (db.tableexist("liketable") == true) {
            db.deleteLikes();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        client.get(Station_Util.URL + "likes.php?usid=" + user_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                //Log.d("Tag1", statusCode + "");
                //Log.d("get_favourites", String.valueOf(response));
                try {
                    Likes_activity.fav_adapter_data.clear();

                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("like");
                            Log.e("length_of_fav", String.valueOf(array.length()));
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String id = obj.getString("id");
                                String uid = obj.getString("uid");
                                String title = obj.getString("title");
                                String description = obj.getString("description");
                                String first_name = obj.getString("first_name");
                                String last_name = obj.getString("last_name");
                                String name = obj.getString("name");
                                String tag = obj.getString("tag");
                                String art = obj.getString("art");
                                String buy = obj.getString("buy");
                                String record = obj.getString("record");
                                String release = obj.getString("release");
                                String license = obj.getString("license");
                                String size = obj.getString("size");
                                String download = obj.getString("download");
                                String time = obj.getString("time");
                                String likes = obj.getString("likes");
                                String downloads = obj.getString("downloads");
                                String Public = obj.getString("public");
                                String views = obj.getString("views");

                                if (!title.equalsIgnoreCase("")) {
                                    Likes_activity.fav_adapter_data.add(new Explore_model_item(id, uid, title, description,
                                            first_name, last_name, Station_Util.TRACK_PATH + name, tag, art, buy,
                                            record, release, license, size, download, time, likes, downloads, views, Public, true));
                                }
                                db.insertLikes(id, "true");
                            }
                        } else {
                            Log.d("NoLikes", "NoLikes");
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

    private void Remove_fav_data() {
        Iv_favorite.setClickable(false);

        user_id = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        // requestParams = new RequestParams();
        //requestParams.add("trackid", id);
        //   Log.e("url_unlike", "http://singering.com/myapi/likes.php?unlike=yes&userid=" + user_id + "&trackid=" + id);

        client.get(Station_Util.URL + "likes.php?unlike=yes&userid=" + user_id + "&trackid=" + id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("Tag", statusCode + "");
                Log.d("Response", String.valueOf(response));

                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {
                            Iv_favorite.setImageResource(R.drawable.ic_favorite_empty);
                            favourite = false;
//array_list_player.remove(RadiophonyService.getInstance().getPlayingRadioStation());
                            db.updateLikes(id, "false");
                            // int num = Integer.parseInt(str_likes) - 1;
                            // str_likes = String.valueOf(num);
                            //setLikesNum();
                            Iv_favorite.setClickable(true);
                            getFavourites();
                        } else {
                            Iv_favorite.setImageResource(R.drawable.ic_favorite_fill);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Iv_favorite.setClickable(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Iv_favorite.setClickable(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Iv_favorite.setClickable(true);
            }
        });
    }

/*    void saveFile(byte[] stringToSave) {
        try {
            File file1 = new File(file + "/" + "test.mp3");
            if (!file1.exists())
                file1.createNewFile();

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file1));
            yourKey = generateKey();
            byte[] filesBytes = encodeFile(yourKey, stringToSave);
            bos.write(filesBytes);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        // Generate a 256-bit key
        final int outputKeyLength = 256;
        SecureRandom secureRandom = new SecureRandom();
        // Do *not* seed secureRandom! Automatically seeded from system entropy.
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(outputKeyLength, secureRandom);
        yourKey = keyGenerator.generateKey();
        return yourKey;
    }

    public static byte[] encodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] encrypted = null;
        byte[] data = yourKey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length, algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        encrypted = cipher.doFinal(fileData);
        return encrypted;
    }

    int count = 0;

    private void decodeFile() {
        try {
            if (count == 0) {
                byte[] decodedData = decodeFile(yourKey, readFile());
                String str = new String(decodedData);
                System.out.println("DECODED FILE CONTENTS : " + str);
                Log.d("Decoded file", str);
                fileplay_temp = decodedData;
                playMp3(decodedData);
                count = count + 1;
            } else {
                play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void play() throws IOException {

        File tempMp33 = File.createTempFile("kurchina", "mp3", getCacheDir());
        tempMp33.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(tempMp33);
        fos.write(fileplay_temp);
        fos.close();
        // Tried reusing instance of media player
        // but that resulted in system crashes...
        MediaPlayer mediaPlayer = new MediaPlayer();
        FileInputStream fis = new FileInputStream(tempMp33);
        mediaPlayer.setDataSource(fis.getFD());
        mediaPlayer.prepare();
        mediaPlayer.start();
    }


    private void playMp3(byte[] mp3SoundByteArray) {
        try {
            // create temp file that will hold byte array
            File tempMp3 = File.createTempFile("kurchina", "mp3", getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();
            // Tried reusing instance of media player
            // but that resulted in system crashes...
            MediaPlayer mediaPlayer = new MediaPlayer();
            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static byte[] decodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] decrypted = null;
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, yourKey, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        decrypted = cipher.doFinal(fileData);
        return decrypted;
    }


    public byte[] readFile() {
        byte[] contents = null;
        // File file1 = new File(file + "/" + name);
        File file1 = new File(Environment.getExternalStorageDirectory() +
                "/" + getResources().getString(R.string.app_name) + "/Downloads/test.mp3");
        int size = (int) file1.length();
        contents = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file1));
            try {
                buf.read(contents);
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return contents;
    }


    private class DownloadTask extends AsyncTask<String, String, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            donut_progress.setProgress(0);
            progress_area.setVisibility(View.VISIBLE); // Showing the stylish material progressbar
            //  mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            donut_progress.setProgress(Integer.parseInt(values[0])); //Updating progress
            //   mProgressDialog.setIndeterminate(false);
            //  mProgressDialog.setMax(100);
            // mProgressDialog.setProgress(values[0]);
        }


        @Override
        protected void onPostExecute(String s) {
            mWakeLock.release();
            // mProgressDialog.dismiss();
            if (s != null)
                Toast.makeText(context, "Erreur de telechargement: " + s, Toast.LENGTH_LONG).show();
            else
                progress_area.setVisibility(View.GONE); // Showing the stylish material progressbar
            disableEvent = false;
            Toast.makeText(context, "fichier telechargé", Toast.LENGTH_LONG).show();
            // Utility.showAlert(Player.this, "Alert", "File downloaded to My Downloads section", null, "Ok");
            set_download_count();
            // Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
        }

/*
        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();
                // download the file
                input = connection.getInputStream();
                //output = new FileOutputStream(file + "/" + title + ".mp3");
                byte[] audio_data = null;
                audio_data = new byte[fileLength];
                int bytesRead;
                ByteArrayOutputStream output1 = new ByteArrayOutputStream();
                while ((bytesRead = input.read(audio_data)) != -1) {
                    output1.write(audio_data, 0, bytesRead);
                }
                saveFile(output1.toByteArray());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }*/


        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(file + "/" + title + ".mp3");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress(String.valueOf((total * 100 / fileLength)));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
    }
}








/*    private void setLikesNum() {

        if (str_likes != null) {
            int likes = Integer.parseInt(str_likes);
            if (likes > 999) {
                // 1k
                char str1 = str_likes.charAt(0);
                str_likes = str_likes.replace(str_likes, str1 + "k");
                RadioUtil.likes = str_likes;
            } else if (likes > 9999) {
                //2k
                char str1 = str_likes.charAt(0);
                char str2 = str_likes.charAt(1);
                str_likes = str_likes.replace(str_likes, str1 + str2 + "k");
                RadioUtil.likes = str_likes;
            } else if (likes > 99999) {
                //1 m
                char str1 = str_likes.charAt(0);
                str_likes = str_likes.replace(str_likes, str1 + "m");
                RadioUtil.likes = str_likes;
            } else {
                RadioUtil.likes = str_likes;
            }
        } else {

        }
    }*/


  /*      try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(song);

            mediaPlayer.prepare();
            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            mediaPlayer.start();
            count=1;
            Iv_btn_PlayPause.setImageResource(R.drawable.ic_pause_button);
        } catch (Exception e) {

            System.out.println("---fff------"+e);
            // TODO: handle exception
        }*/





  /*      if (fav_adapter_data.size() > 0) {
            if (checkFavItem(model)) {
                favourite = true;
                Iv_favorite.setImageResource(R.drawable.ic_favorite_fill);
            } else {
                favourite = false;
                Iv_favorite.setImageResource(R.drawable.ic_favorite_empty);
            }
        } else {
            favourite = false;
            Iv_favorite.setImageResource(R.drawable.ic_favorite_empty);
        }*/