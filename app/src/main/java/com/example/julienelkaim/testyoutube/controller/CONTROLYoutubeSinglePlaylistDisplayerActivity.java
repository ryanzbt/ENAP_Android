package com.example.julienelkaim.testyoutube.controller;

import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.julienelkaim.testyoutube.R;
import com.example.julienelkaim.testyoutube.adapter.YoutubeVideoListAdapter;
import com.example.julienelkaim.testyoutube.controller.MotherActivity.YoutubeThumbnailListDisplayerActivity;
import com.example.julienelkaim.testyoutube.model.Playlist;
import com.example.julienelkaim.testyoutube.model.VideoDetails;
import com.example.julienelkaim.testyoutube.toolbox.Constants;
import com.example.julienelkaim.testyoutube.toolbox.YoutubeHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CONTROLYoutubeSinglePlaylistDisplayerActivity extends YoutubeThumbnailListDisplayerActivity {

    private Playlist mPlaylist;
    private ListView mListDisplayer;
    private YoutubeVideoListAdapter mYoutubeVideoListAdapter;
    private ArrayList<VideoDetails> mVideoDetailsArrayList;
    private String mMyAPIRequestForThisPlaylist;


    protected void onStart() {
        super.onStart();
        Constants.windowAndSystemSettings(this);
    }


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        mIsListModifiable = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlyoutube_single_playlist_displayer);

        mPlaylist = (Playlist) getIntent().getSerializableExtra(Constants.YOUTUBE_ACTUAL_MODIFIED_PLAYLIST);
        initializeViews();


    }

    private void initializeViews(){
        mMyAPIRequestForThisPlaylist = YoutubeHelper.setGoogleApiVideoListDataRetriever(mPlaylist.getVideoIdList()); // Get les infos

        TextView playlistTitle = findViewById(R.id.title_playlist);
        playlistTitle.setText(mPlaylist.getTitle());
        mListDisplayer = findViewById(R.id.listOfVideos);
        mVideoDetailsArrayList = new ArrayList<>();
        mYoutubeVideoListAdapter = new YoutubeVideoListAdapter(this, mVideoDetailsArrayList);
        launchVideosResearch();

        TextView emptyText = findViewById(android.R.id.empty);
        mListDisplayer.setEmptyView(emptyText);

        if (mPlaylist.getVideoIdList().size() != 0){
            ((ViewGroup) emptyText.getParent()).removeView(emptyText);
            //Permet de ne pas afficher ce empty-message  si on est juste entrain d'attendre les videos
        }


    }


    @Override
    public void modifyYourList(String videoId) {
        System.out.println("DEBUG::: " + videoId);
        ArrayList<String> vList =  new ArrayList<>(mPlaylist.getVideoIdList());
        vList.remove(videoId);


        mPlaylist.setVideoIdList(vList);
        initializeViews();
        YoutubeHelper.updateListOfPlaylist(this, mPlaylist, YoutubeHelper.retrieveListOfPlaylist(this));

    }

    private void launchVideosResearch() {
        System.out.println(mMyAPIRequestForThisPlaylist);
        RequestQueue rqQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRqQueue = new StringRequest(
                Request.Method.GET,
                mMyAPIRequestForThisPlaylist,
                new Response.Listener<String>() {@Override public void onResponse(String response) { processRequestResponse(response) ; } },
                new Response.ErrorListener() {@Override public void onErrorResponse(VolleyError error) { Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show(); } }
        );
        rqQueue.add(stringRqQueue);
    }

    private void processRequestResponse(String response) {
        try {
            JSONArray jArray = new JSONObject(response).getJSONArray("items");
            for (int i=0; i< jArray.length();i++) {
                JSONObject jsonObject = jArray.getJSONObject(i);

                //charger les détails de la video
                System.out.println("CONTROLYoutubeSinglePlaylistDisplayerActivity::TEST:: NOUS EN SOMME A ID : " + i);
                System.out.println("CONTROLYoutubeSinglePlaylistDisplayerActivity::TEST:: On a trouvé l'ID suivante : "+ jsonObject.getString("id"));
               YoutubeHelper.loadVideoDetailsInAList(jsonObject,mVideoDetailsArrayList, "ControlPlaylistDisplayer");
            }

            YoutubeHelper.displayAYoutubeVideoList(mListDisplayer, mYoutubeVideoListAdapter);
            System.out.println("CONTROLYoutubeSinglePlaylistDisplayerActivity::processRequestResponse::DEBUG::TryBlock::SUCCESS:: Fial Adapter");



        } catch (JSONException e) {
            System.out.println("CONTROLYoutubeSinglePlaylistDisplayerActivity::processRequestResponse::DEBUG::TryBlock::FAIL:: On est dans JSON error");
            e.printStackTrace();
        }

    }
}
