package com.example.nooble;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.hanks.library.bang.SmallBangView;

public class MainActivity extends AppCompatActivity {

    Spotify mSpotifyAppRemote = null;
    Context context;
    private static final int REQUEST_CODE = 1337;
    private static ViewPager2 viewPager;
    private static final String REDIRECT_URI = "http://nooble.example.com/callback";
    private  static final String CLIENT_ID="c9e56c05a76f41a886258a1222a973d8";
    private static final AuthorizationRequest.Builder builder =
            new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
    final AuthorizationRequest request = builder.setScopes(new String[]{"streaming"}).build();
    AuthorizationResponse tokens = null;

    static int count = 0;
    AudioAdapter adapter;
    private RequestQueue mRequestQueue;
    List<AudioItems> audioItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.audioViewPager);
        audioItems = new ArrayList<>();

        context = this;
        mRequestQueue = Volley.newRequestQueue(this);
        AudioItems item = new AudioItems();
        item.setAlbum("Loading");
        item.setArtist("Loading");
        item.setImage("Loading");
        item.setName("Loading");

        audioItems.add(item);


        adapter = new AudioAdapter(audioItems);

        viewPager.setAdapter(adapter);




        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(mSpotifyAppRemote!=null){

                    mSpotifyAppRemote.play(context, audioItems.get(position).track);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });


        initiateItemFetch();


    }



    public void initiateItemFetch(){
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
    }






    public void getItems(){
        //Log.d("VolleyError", "Enter");
        String playlistUrl = "https://api.spotify.com/v1/browse/featured-playlists";
        JSONObject json = new JSONObject();
        try {
            json.put("limit", "50");

        }
        catch (JSONException e){
            Log.e("JSONERROR", "unexpected JSON exception", e);
        }
        JsonObjectRequest spotifyRequest = new JsonObjectRequest(Request.Method.GET, playlistUrl, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray list = response.getJSONObject("playlists").getJSONArray("items");
                    List<String> playlists = new ArrayList<>();
                    for(int i = 0;i<list.length();i++)
                    {
                        playlists.add(list.getJSONObject(i).getJSONObject("tracks").getString("href"));
                    }


                    int playlistNumber = (int) Math.floor(Math.random()*((playlists.size() - 1)-(0)+1)+(0)) ;


                    String playlist = playlists.get(playlistNumber);
                    getTracks(playlist);
                }
                catch (JSONException e){
                    Log.e("JSONERROR", "unexpected JSON exception", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", error.getMessage());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization:", "Bearer " + tokens.getAccessToken());
                params.put("Accept-Language", "fr");

                return params;
            }
        };

        mRequestQueue.add(spotifyRequest);

    }

    private void getTracks(String playlist) {



        String playlistUrl = playlist;
        JSONObject json = new JSONObject();
        try {
            json.put("limit", "50");
            json.put("market","In");
            json.put("fields","items(track(name%2Curi%2Cimages%2Calbum(name)%2Cartists(name)))");
        }
        catch (JSONException e){
            Log.e("JSONERROR", "unexpected JSON exception", e);
        }
        JsonObjectRequest spotifyRequest = new JsonObjectRequest(Request.Method.GET, playlistUrl, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray list = response.getJSONArray("items");
                    int flag = 0;
                    if(audioItems.size() == 1)
                    {
                        flag = 1;
                        audioItems.remove(0);
                    }
                    String name = "";
                    String image = "";
                    String artist = "";
                    String album = "";
                    String link = "";
                    for(int i  = 0;i<list.length();i++)
                    {
                        AudioItems newAi = new AudioItems();
                        name = list.getJSONObject(i).getJSONObject("track").getString("name");
                        link = list.getJSONObject(i).getJSONObject("track").getString("uri");
                        image =  album = list.getJSONObject(i).getJSONObject("track").getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
                        album = list.getJSONObject(i).getJSONObject("track").getJSONObject("album").getString("name");
                        artist = list.getJSONObject(i).getJSONObject("track").getJSONArray("artists").getJSONObject(0).getString("name");
                        newAi.setImage(image);
                        newAi.setArtist(artist);
                        newAi.setName(name);
                        newAi.setTrack(link);
                        newAi.setAlbum(album);
                        audioItems.add(newAi);
                    }
                    adapter.notifyDataSetChanged();

                    if(flag == 1)
                    {
                        mSpotifyAppRemote.play(context, audioItems.get(0).track);
                    }
                }
                catch (JSONException e){
                    Log.e("JSONERROR", "unexpected JSON excfeption", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization:", "Bearer " + tokens.getAccessToken());
                params.put("Accept-Language", "fr");

                return params;
            }
        };

        mRequestQueue.add(spotifyRequest);


    }


    @Override
    protected void onStart() {
        super.onStart();

        if(mSpotifyAppRemote == null) {
            mSpotifyAppRemote = new Spotify();
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpotifyAppRemote.pause();
        mSpotifyAppRemote.disconnect();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

            switch (response.getType()) {

                case TOKEN:
                    tokens = response;
                    getItems();
                    break;
                case ERROR:
                    Log.d("Error","Failed");

                    break;
                default:
                    Log.d("Default","HMMM");

            }
        }
    }
}


//"The access token expired"