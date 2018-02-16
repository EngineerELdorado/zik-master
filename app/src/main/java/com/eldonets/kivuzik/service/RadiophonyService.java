package com.eldonets.kivuzik.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.Util;
import com.eldonets.kivuzik.Models.Explore_model_item;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;

public class RadiophonyService extends Service {

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    private static final int NOTIFICATION_ID = 1;
    private static MediaCodecAudioTrackRenderer audioRenderer;
    private static Context context;
    static ProgressDialog dialog;
    public static ExoPlayer exoPlayer;
    private static int inwhich;
    public static int list;
    private static RadiophonyService service;
    private static Explore_model_item station;
    static ProgressTask task;
    private WifiLock wifiLock;
    private String userAgent;
    private Uri uri;
/*    public static double startTime = 0;
    public static double finalTime = 0;*/

    static public void initialize(Context context, Explore_model_item station, int inwhich) {
        RadiophonyService.context = context;
        RadiophonyService.station = station;
        RadiophonyService.inwhich = inwhich;
        //exoPlayer = ExoPlayer.Factory.newInstance(1);
        Log.e("inwhich", "" + inwhich);
    }

    static public RadiophonyService getInstance() {
        if (service == null) {
            service = new RadiophonyService();
        }
        return service;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        exoPlayer = ExoPlayer.Factory.newInstance(1);

/*        RadiophonyService.exoPlayer.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                //  System.out.println("---state----"+RadiophonyService.exoPlayer.getPlaybackState());
                // System.out.println("---current pos----"+RadiophonyService.exoPlayer.getCurrentPosition());
                Station_Util.checkended = playbackState;

                if (playbackState == ExoPlayer.STATE_ENDED) {


              *//*      Station_Util.state_ended = true;

                    boolean guest = getSharedPreferences("guest_check", MODE_PRIVATE).getBoolean("guest", false);
                    if (guest == true){
                        Guest_activity.frame_footer.setVisibility(View.GONE);
                    }else{
                        CircleActivity.frame_footer.setVisibility(View.GONE);
                    }
                    Player.Iv_btn_PlayPause.setImageResource(R.drawable.ic_play_button);
                    Player.mVuMeterView.pause();
                    startService(new Intent(context, Notification_Serviceplay.class));
                    Player.message = false;*//*
                    RadiophonyService.exoPlayer.removeListener(this);
                }
            }
            @Override
            public void onPlayWhenReadyCommitted() {
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }
        });*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (station.getName() != null) {
                task = new ProgressTask();
                task.execute(station.getName());
            } else {
                Log.e("No track found", "NO TRACK FOUND");
            }
        }catch (RuntimeException e)
        {
            Log.e("Error",String.valueOf(e));
        }

        return START_NOT_STICKY;
    }

    public void pause() {
        // exoPlayer.stop();
        exoPlayer.setPlayWhenReady(false);
    }

    public void start() {
        exoPlayer.setPlayWhenReady(true);
    }

    public void onDestroy() {
        exoPlayer.stop();
        Log.e("Destroyed", "Called");
    }

    public void onStop() {
        /*if (dialog != null && dialog.isShowing()) {
            task.cancel(true);
        }*/
    }

    public void stop() {
        if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
            this.wifiLock.release();
            this.stopForeground(true);
        }
    }


    public static void Mute() {
        exoPlayer.sendMessage(audioRenderer, MediaCodecAudioTrackRenderer.MSG_SET_VOLUME, 0f);
    }


    public boolean isPlaying() {
        return exoPlayer != null && exoPlayer.getPlayWhenReady();
    }

    private class ProgressTask extends AsyncTask<String, Void, Boolean> {

        public ProgressTask() {
            //dialog = new ProgressDialog(context);
        }

        protected void onPreExecute() {

        }

        protected Boolean doInBackground(final String... args) {
            try {
                if (station.getName().endsWith(".m3u")) {
                    uri = Uri.parse(Parser.parse(station.getName()));
                } else {
                    uri = Uri.parse(station.getName());
                }
                Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
                String userAgent = Util.getUserAgent(context, "ExoPlayerDemo");
                OkHttpClient okHttpClient = new OkHttpClient();

                DataSource dataSource = new DefaultUriDataSource(context, null,
                        new OkHttpDataSource(okHttpClient, userAgent, null, null, CacheControl.FORCE_NETWORK));
                ExtractorSampleSource sampleSource = new ExtractorSampleSource(uri, dataSource, allocator,
                        BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
                MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                        MediaCodecSelector.DEFAULT, null, true, null, null,
                        AudioCapabilities.getCapabilities(context), AudioManager.STREAM_MUSIC);

                exoPlayer.prepare(audioRenderer);

                return true;
            } catch (IllegalArgumentException | SecurityException | IllegalStateException e1) {
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                wifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                        .createWifiLock(WifiManager.WIFI_MODE_FULL, "RadiophonyLock");
                wifiLock.acquire();
                if (exoPlayer != null) {
                    exoPlayer.setPlayWhenReady(true);
                }
            }
        }
    }


    public void playprevious(Explore_model_item radio) {

        RadiophonyService.station = radio;

        if (station.getName().endsWith(".m3u")) {
            uri = Uri.parse(Parser.parse(station.getName()));
        } else {
            uri = Uri.parse(station.getName());
        }

        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        userAgent = Util.getUserAgent(context, "ExoPlayerDemo");
        OkHttpClient okHttpClient = new OkHttpClient();
        //DataSource dataSource = new DefaultUriDataSource(context, null, userAgent);

        DataSource dataSource = new DefaultUriDataSource(context, null,
                new OkHttpDataSource(okHttpClient, userAgent, null, null, CacheControl.FORCE_NETWORK));
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(uri, dataSource, allocator,
                BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);

        // audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
        audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                MediaCodecSelector.DEFAULT, null, true, null, null,
                AudioCapabilities.getCapabilities(context), AudioManager.STREAM_MUSIC);

        exoPlayer.prepare(audioRenderer);
        exoPlayer.setPlayWhenReady(true);

    }

    public void playNext(Explore_model_item radio) {
        RadiophonyService.station = radio;

        if (station.getName().endsWith(".m3u")) {
            uri = Uri.parse(Parser.parse(station.getName()));
        } else {
            uri = Uri.parse(station.getName());
        }

        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        userAgent = Util.getUserAgent(context, "ExoPlayerDemo");
        OkHttpClient okHttpClient = new OkHttpClient();
        //DataSource dataSource = new DefaultUriDataSource(context, null, userAgent);

        DataSource dataSource = new DefaultUriDataSource(context, null,
                new OkHttpDataSource(okHttpClient, userAgent, null, null, CacheControl.FORCE_NETWORK));
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(uri, dataSource, allocator,
                BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);

        // audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
        audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                MediaCodecSelector.DEFAULT, null, true, null, null,
                AudioCapabilities.getCapabilities(context), AudioManager.STREAM_MUSIC);

        exoPlayer.prepare(audioRenderer);
        exoPlayer.setPlayWhenReady(true);

    }

    public Explore_model_item getPlayingRadioStation() {
        return station;
    }

}
