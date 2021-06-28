package com.example.nooble;

import android.content.Context;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class Spotify {
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://nooble.example.com/callback";
    private  static final String CLIENT_ID="c9e56c05a76f41a886258a1222a973d8";


    public SpotifyAppRemote mSpotifyAppRemote = null;
    public String currentImage = "" ;
    public String currentArtist = "";
    public String currentTrackName = "";
    public String currentAlbum = "";

    void connect(Context context, String track){
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        player(track);
                    }
                    public void onFailure(Throwable throwable) {
                        Log.d("MyActivity", throwable.getMessage(), throwable);
                    }
                });
    }

    void play(Context context,String track){
        if(mSpotifyAppRemote == null || !mSpotifyAppRemote.isConnected())
        {

            connect(context, track);
        }
        else
        {
            player(track);
        }

    }

    void pause(){
        if(mSpotifyAppRemote == null) return;
        mSpotifyAppRemote.getPlayerApi().pause();
    }

    void resume(){
        if(mSpotifyAppRemote == null) return;
        mSpotifyAppRemote.getPlayerApi().resume();
    }

    private void player(String track){

        mSpotifyAppRemote.getPlayerApi().play(track);
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback((new Subscription.EventCallback<PlayerState>() {
                    @Override
                    public void onEvent(PlayerState playerState) {
                        final Track track = playerState.track;

                        if (track != null) {



                            Log.d("MainActivity", track.name + " by " + track.artist.name);
                        }
                    }
                }));
    }


    void disconnect(){
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}
