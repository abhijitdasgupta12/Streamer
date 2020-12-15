package com.example.streamer;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyViewHolder extends RecyclerView.ViewHolder
{
    CircleImageView uploaderCircleImageView;
    TextView uploadUserName, uploadDateTime, videoTitleTextView, amountOfLikesTextView, amountOfCommentsTextView;
    SimpleExoPlayer simpleExoPlayer;
    SimpleExoPlayerView simpleExoPlayerView;
    ImageView likeButton, commentButton;

    DatabaseReference likeDatabaseReference, commentDatabaseReference;

    public MyViewHolder(@NonNull View itemView)
    {
        super(itemView);

        //TypeCasting from single_row.xml
        uploaderCircleImageView= itemView.findViewById(R.id.uploaderUserImage);
        uploadUserName= itemView.findViewById(R.id.uploadertUserNameTextView);
        uploadDateTime= itemView.findViewById(R.id.uploaderDateTimeTextView);
        videoTitleTextView= itemView.findViewById(R.id.vtitle);
        amountOfLikesTextView= itemView.findViewById(R.id.LikeText);
        amountOfCommentsTextView= itemView.findViewById(R.id.commentText);
        likeButton= itemView.findViewById(R.id.likebutton);
        commentButton= itemView.findViewById(R.id.commentButton);
        simpleExoPlayerView= itemView.findViewById(R.id.exoplayerView);

    }

    //Checking like button status for each logged in user
    public void getLikeButtonStatus(final String videoID, final String userID)
    {
        likeDatabaseReference = FirebaseDatabase.getInstance().getReference("streamer").child("likes");
        likeDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.child(videoID).hasChild(userID))
                {
                    int likeCount= (int)snapshot.child(videoID).getChildrenCount(); //Counting sub-children under a particular child
                    amountOfLikesTextView.setText(String.valueOf(likeCount));
                    likeButton.setImageResource(R.drawable.ic_baseline_favorite_24);
                }
                else
                {
                    int likeCount= (int)snapshot.child(videoID).getChildrenCount(); //Counting sub-children under a particular child
                    amountOfLikesTextView.setText(String.valueOf(likeCount));
                    likeButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Get Amount of Total Comments In Video
    public void getCommentStatus(final String videoID)
    {
        commentDatabaseReference= FirebaseDatabase.getInstance().getReference("streamer").child("videos").child(videoID).child("comments");
        commentDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.hasChildren())
                {
                    int commentCount= (int) snapshot.getChildrenCount();
                    amountOfCommentsTextView.setText(String.valueOf(commentCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //User defined method: Preparing the SimpleExoPlayer
    void prepareSimpleExoPlayer(Application application, String videoTitle, String vURL)
    {
        try
        {
            videoTitleTextView.setText(videoTitle);

            //Copy the code
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            simpleExoPlayer =(SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(application,trackSelector);

            Uri videoURI = Uri.parse(vURL);

            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);

            simpleExoPlayerView.setPlayer(simpleExoPlayer);
            simpleExoPlayer.prepare(mediaSource);
            simpleExoPlayer.setPlayWhenReady(false);
        }
        catch (Exception ex)
        {
            Log.d("Exoplayer ERROR", ex.getMessage().toString());
        }
    }
}
