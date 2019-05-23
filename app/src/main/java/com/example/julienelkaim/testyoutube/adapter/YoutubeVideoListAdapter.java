package com.example.julienelkaim.testyoutube.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.julienelkaim.testyoutube.R;
import com.example.julienelkaim.testyoutube.controller.MotherActivity.YoutubeThumbnailListDisplayerActivity;
import com.example.julienelkaim.testyoutube.controller.YoutubePlayListDisplayerActivity;
import com.example.julienelkaim.testyoutube.model.VideoDetails;
import com.example.julienelkaim.testyoutube.toolbox.Constants;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;



public class YoutubeVideoListAdapter extends BaseAdapter {

    private YoutubeThumbnailListDisplayerActivity mActivity;
    private ArrayList<VideoDetails> mVideoDetailsArrayList;
    private LayoutInflater mLayoutInflater;



    public YoutubeVideoListAdapter(YoutubeThumbnailListDisplayerActivity activity, ArrayList<VideoDetails> videoDetailsArrayList){
        mActivity = activity;
        mVideoDetailsArrayList = videoDetailsArrayList;
    }



    @Override
    public int getCount() {
        return mVideoDetailsArrayList.size();
    }



    @Override
    public Object getItem(int position) {
        return mVideoDetailsArrayList.get(position);
    }



    @Override
    public long getItemId(int position) {
        return (long)position;
    }



    @SuppressLint("InflateParams")
    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {

        if(mLayoutInflater==null){  mLayoutInflater = mActivity.getLayoutInflater();    }
        if(convertView == null){    convertView = mLayoutInflater.inflate(R.layout.custom_video_item,null); }


        final VideoDetails videoDetails = mVideoDetailsArrayList.get(position);

        ImageView imageView = convertView.findViewById(R.id.thumbnailsImageView);
        Picasso.get().load(videoDetails.getUrl()).into(imageView);

        TextView textView = convertView.findViewById(R.id.video_title);
        textView.setText(videoDetails.getTitle());

        TextView secondTextView = convertView.findViewById(R.id.video_description);
        secondTextView.setText(videoDetails.getDescription());

        LinearLayout linearLayout = convertView.findViewById(R.id.root);

        final Intent i = new Intent(mActivity, YoutubePlayListDisplayerActivity.class);
        i.putExtra(Constants.YOUTUBE_VIDEO_ID_FROM_RESEARCH,videoDetails.getVideoId());

        if(mActivity.mIsListModifiable){
            //Single playlist display part    =================== DIFFERENT INTENT QUE DANS LE ELSE !!
            addASupprButton(mActivity,convertView, videoDetails);
            linearLayout.setOnClickListener(new LinearLayout.OnClickListener() {
                @Override
                public void onClick(View v) {
                    i.putExtra(Constants.YOUTUBE_DISPLAYER_MODE, Constants.YOUTUBE_DISPLAYER_MODE_VIEW);
                    mActivity.startActivity(i);
                }
            });

        }else{
            linearLayout.setOnClickListener(new LinearLayout.OnClickListener() {
                @Override
                public void onClick(View v) {
                    i.putExtra(Constants.YOUTUBE_DISPLAYER_MODE,  Constants.YOUTUBE_DISPLAYER_MODE_ADD);
                    mActivity.startActivity(i);
                }
            });
        }

        mActivity.incrementCountVideoDisplayed(); // Can be used as we want to add new behavior for a specific video... For instance, load further videos at the last position
        return convertView;
    }

    private void addASupprButton(final YoutubeThumbnailListDisplayerActivity activity, View convertView, final VideoDetails videoDetails) {


        ImageButton supprBtn = new ImageButton(activity);
        supprBtn.setLayoutParams(new ViewGroup.LayoutParams(50,50));
        supprBtn.setBackgroundResource(R.drawable.suppr_button);
        supprBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.modifyYourList(videoDetails.getVideoId());
                activity.finish();
                activity.startActivity(activity.getIntent()); //Relancer l'activité elle même

            }
        });

        LinearLayout ll = convertView.findViewById(R.id.desc_and_button);
        if(ll.getChildCount() == 1 ){
            ll.addView(supprBtn);
        }else{
            ll.removeViewAt(1);
            ll.addView(supprBtn);
        }
    }

}
