package com.eldonets.kivuzik.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.eldonets.kivuzik.Activities.Chat_Screen;
import com.eldonets.kivuzik.Activities.My_following_activity;
import com.eldonets.kivuzik.Adapters.Followers_adapter;
import com.eldonets.kivuzik.Models.Search_model_item;
import com.eldonets.kivuzik.R;
import com.eldonets.kivuzik.Utilities.Station_Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ST_004 on 23-12-2016.
 */

public class Message_online_friends  extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    View view;
    ListView listview_message;
    Toolbar toolbar_message;
    FrameLayout framelay_no_message;
    public static ArrayList<Search_model_item> online_list = new ArrayList<>();
    private static String KEY_SUCCESS = "success";
    String uid;
    Followers_adapter following_list_Adapter;


    public Message_online_friends() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_message__person_list, container, false);

        initdeclare(view);


        following_list_Adapter = new Followers_adapter(getActivity(), online_list,1);
        get_pref_values();
        set_values_from_pref();

        get_Online_person();
        return view;
    }


    private void get_pref_values() {

    }

    private void set_values_from_pref() {


    }
    private void get_Online_person() {



        uid = getActivity().getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        // final ProgressDialog pDialog = new ProgressDialog(My_Profile_Screen.this);

        client.get(Station_Util.URL + "online_users.php?idu=" + uid, new JsonHttpResponseHandler() {
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
                //  Log.d("Tag", statusCode + "");
                //  Log.d("Response121", String.valueOf(response));

                Search_model_item model;
                try {
                    online_list.clear();

                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            My_following_activity.following_adapter_data.clear();

                            JSONArray array = response.getJSONArray("online");
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

                                online_list.add(model);

                            }
                        } else {

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (online_list.isEmpty())
                {
                    framelay_no_message.setVisibility(View.VISIBLE);
                    listview_message.setVisibility(View.GONE);
                } else
                {
                    listview_message.setVisibility(View.VISIBLE);
                    framelay_no_message.setVisibility(View.GONE);
                    following_list_Adapter.setData(online_list);
                    listview_message.setAdapter(following_list_Adapter);
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

    private void initdeclare(View view) {
        listview_message = (ListView) view.findViewById(R.id.listview_message);
        listview_message.setOnItemClickListener(this);
        framelay_no_message = (FrameLayout) view.findViewById(R.id.framelay_no_message);

    }


    @Override
    public void onClick(View v) {

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object o = listview_message.getItemAtPosition(position);
        Search_model_item profile_detail = (Search_model_item) o;

        Intent i = new Intent(getActivity(), Chat_Screen.class);
        i.putExtra("position", position);
        i.putExtra("from","message_activity");
        i.putExtra("profile_detail", profile_detail);
        startActivity(i);
    }
}
