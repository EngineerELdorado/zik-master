package com.eldonets.kivuzik.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.eldonets.kivuzik.Adapters.Explore_Tracks_Adapter;
import com.eldonets.kivuzik.Models.Explore_model_item;
import com.eldonets.kivuzik.Preferences.MusicPlayer_Prefrence;
import com.eldonets.kivuzik.R;
import com.eldonets.kivuzik.Utilities.Singering_Database;
import com.eldonets.kivuzik.Utilities.Station_Util;
import com.eldonets.kivuzik.admobadapter.expressads.AdmobExpressAdapterWrapper;
import com.eldonets.kivuzik.admobadapter.expressads.NativeExpressAdViewHolder;
import com.eldonets.kivuzik.circleMenu.CircleActivity;
import com.eldonets.kivuzik.service.RadiophonyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class Explore extends AppCompatActivity implements AdapterView.OnItemClickListener {
    //public static ArrayList<Explore_model_item> adapter_data;
    private Explore_Tracks_Adapter list_Adapter;
    ListView listview_tracks;
    ProgressBar progressBarInitial;
    Toolbar toolbar;
    public RelativeLayout pDialog;
    Singering_Database db;
    private AdView mAdView;
    AdmobExpressAdapterWrapper adapterWrapper;
    SearchView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }

        db = new Singering_Database(Explore.this);
        initDeclare();
        // MobileAds.initialize(getApplicationContext(), getString(R.string.admob_express_unit_id));

        getData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search1);
        sv = new SearchView(((Explore.this)).getSupportActionBar().getThemedContext());
        sv.setIconifiedByDefault(false);
        sv.setQueryHint(Html.fromHtml("<font color = #757575>" + getResources().getString(R.string.action_search) + "</font>"));
        changeSearchViewTextColor(sv);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, sv);
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
        return true;
    }

    protected boolean isAlwaysExpanded() {
        return true;
    }

    private void setupSearchView(MenuItem searchItem) {
        if (isAlwaysExpanded()) {
            sv.setIconifiedByDefault(false);
        } else {
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List searchables = searchManager.getSearchablesInGlobalSearch();


        }

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_search1: {

                sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        System.out.println("search query submit");
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        System.out.println("tap");
                        list_Adapter.filter(newText.toString().trim());
                        listview_tracks.invalidate();
                        return true;
                    }
                });
                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void changeSearchViewTextColor(SearchView sv) {
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) sv.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(android.R.color.white));
        searchAutoComplete.setTextSize(17);
        searchAutoComplete.setTextColor(Color.WHITE);
    }

    private void initDeclare() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.Explore);
        toolbar.setTitleTextColor(0xffffffff);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

      /*  mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);*/


        pDialog = (RelativeLayout) findViewById(R.id.progressBarHolder);
        progressBarInitial = (ProgressBar) findViewById(R.id.progressBarInitial);
        listview_tracks = (ListView) findViewById(R.id.list_view_tracks);
        listview_tracks.setOnItemClickListener(this);

        list_Adapter = new Explore_Tracks_Adapter(this, CircleActivity.adapter_data, 0, false);
    }

    private void getData() {

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        client.get(Station_Util.URL + "tracks.php?tracks=true", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Explore_model_item model;
                try {
                    CircleActivity.adapter_data.clear();

                    JSONArray array = response.getJSONArray("tracks");
                    for (int i = 0; i < array.length(); i++) {
                        model = new Explore_model_item();

                        JSONObject obj = array.getJSONObject(i);
                        model.id = obj.getString("id");
                        model.uid = obj.getString("uid");
                        model.title = obj.getString("title");
                        model.description = obj.getString("description");
                        model.first_name = obj.getString("first_name");
                        model.last_name = obj.getString("last_name");
                        model.name = Station_Util.TRACK_PATH + obj.getString("name");
                        model.tag = obj.getString("tag");
                        model.art = obj.getString("art");
                        model.buy = obj.getString("buy");
                        model.record = obj.getString("record");
                        model.release = obj.getString("release");
                        model.license = obj.getString("license");
                        model.size = obj.getString("size");
                        model.download = obj.getString("download");
                        model.time = obj.getString("time");
                        model.likes = obj.getString("likes");
                        model.downloads = obj.getString("downloads");
                        model.views = obj.getString("views");
                        model.Public = obj.getString("public");

                        if (!model.title.equalsIgnoreCase("")) {
                            CircleActivity.adapter_data.add(model);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (pDialog.getVisibility() == View.VISIBLE) {
                    pDialog.setVisibility(View.INVISIBLE);
                    listview_tracks.setVisibility(View.VISIBLE);
                    list_Adapter.setData(CircleActivity.adapter_data);
                    if (getResources().getBoolean(R.bool.Ads_check)==true)
                    {
                        initListViewItems();
                    }else {
                        listview_tracks.setAdapter(list_Adapter);
                    }
                } else {
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
        });
    }


    private void initListViewItems() {

        //creating your adapter, it could be a custom adapter as well
        //your test devices' ids
        String[] testDevicesIds = new String[]{getString(R.string.testDeviceID), AdRequest.DEVICE_ID_EMULATOR};
        //when you'll be ready for release please use another ctor with admobReleaseUnitId instead.
        adapterWrapper = new AdmobExpressAdapterWrapper(this, testDevicesIds) {
            @Override
            protected ViewGroup wrapAdView(NativeExpressAdViewHolder adViewHolder, ViewGroup parent, int viewType) {

                //get ad view
                NativeExpressAdView adView = adViewHolder.getAdView();

                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                        AbsListView.LayoutParams.WRAP_CONTENT);
                RelativeLayout container = new RelativeLayout(Explore.this);
                container.setLayoutParams(lp);
                container.setBackgroundColor(Color.BLACK);

                TextView textView = new TextView(Explore.this);
                textView.setLayoutParams(lp);
                textView.setTextColor(Color.RED);

                container.addView(textView);
                //wrapping
                container.addView(adView);
                //return wrapper view
                return container;
            }
        };
        //By default the ad size is set to FULL_WIDTHx150
        //To set a custom size you should use an appropriate ctor
        //adapterWrapper = new AdmobExpressAdapterWrapper(this, testDevicesIds, new AdSize(AdSize.FULL_WIDTH, 150));

        adapterWrapper.setAdapter(list_Adapter); //wrapping your adapter with a AdmobExpressAdapterWrapper.

        //Sets the max count of ad blocks per dataset, by default it equals to 3 (according to the Admob's policies and rules)
        adapterWrapper.setLimitOfAds(10);

        //Sets the number of your data items between ad blocks, by default it equals to 10.
        //You should set it according to the Admob's policies and rules which says not to
        //display more than one ad block at the visible part of the screen,
        // so you should choose this parameter carefully and according to your item's height and screen resolution of a target devices
        adapterWrapper.setNoOfDataBetweenAds(10);

        adapterWrapper.setFirstAdIndex(3);

        listview_tracks.setAdapter(adapterWrapper); // setting an AdmobAdapterWrapper to a ListView
        adapterWrapper.notifyDataSetChanged();

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object o = listview_tracks.getItemAtPosition(position);
        Explore_model_item track_detail = (Explore_model_item) o;
        if (RadiophonyService.exoPlayer != null) {
            RadiophonyService.exoPlayer.stop();
            stopService(new Intent(Explore.this, RadiophonyService.class));
        }
        MusicPlayer_Prefrence.getInstance(Explore.this).save(Explore.this, track_detail);
        MusicPlayer_Prefrence.getInstance(Explore.this).save_position(position);

        startService(new Intent(Explore.this, RadiophonyService.class));
        RadiophonyService.initialize(Explore.this, track_detail, 1);
        play(true);
        Intent i = new Intent(this, Player.class);
        i.putExtra("position", position);
        i.putExtra("from", "song_lists");
        i.putExtra("track_detail", track_detail);
        startActivity(i);
    }

    public void play(boolean play) {
        if (!play) {
            stopService(new Intent(Explore.this, RadiophonyService.class));
        } else {
            startService(new Intent(Explore.this, RadiophonyService.class));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
/*
        sendBroadcast(new Intent(CircleActivity.RECIEVER_DATA));
*/
    }


}


/*@Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }*/
