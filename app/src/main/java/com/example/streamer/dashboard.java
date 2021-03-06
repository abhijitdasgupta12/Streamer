package com.example.streamer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class dashboard extends AppCompatActivity
{

    RecyclerView recyclerView;
    DatabaseReference likeDatabaseReference, uploadUserReference;
    boolean likeButtonTestClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Database references
        likeDatabaseReference= FirebaseDatabase.getInstance().getReference("streamer").child("likes");
        uploadUserReference= FirebaseDatabase.getInstance().getReference("streamer").child("users");

        //Fetching information from database to display inside recyclerview
        FirebaseRecyclerOptions<filemodel> options =
                new FirebaseRecyclerOptions.Builder<filemodel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("streamer").child("videos"), filemodel.class)
                        .build();

        //Working on Adapter
        FirebaseRecyclerAdapter<filemodel, MyViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<filemodel, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull filemodel model)
            {
                holder.uploadUserName.setText(model.getUser_name());
                holder.uploadDateTime.setText(model.getDate()+", "+model.getTime());

                Glide.with(holder.uploaderCircleImageView.getContext()).load(model.getUser_image()).into(holder.uploaderCircleImageView);

                //Video is ready to be shown in SimpleExoPlayer
                holder.prepareSimpleExoPlayer(getApplication(),model.getVideo_title(),model.video_url);

                FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                final String userID= firebaseUser.getUid();
                final String videoID= getRef(position).getKey();

                //get like & comment status
                holder.getLikeButtonStatus(videoID, userID);
                holder.getCommentStatus(videoID);

                //Like button process
                holder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        likeButtonTestClick=true;
                        likeDatabaseReference.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                if (likeButtonTestClick==true)
                                {
                                    //User ID will be stored in "likes" table under every video ID whenever an user likes video.
                                    if (snapshot.child(videoID).hasChild(userID))
                                    {
                                        likeDatabaseReference.child(videoID).child(userID).removeValue(); //Removes the user's like status from liked video in database
                                        likeButtonTestClick=false;
                                    }
                                    else
                                    {
                                        likeDatabaseReference.child(videoID).child(userID).setValue(true); //Adds a like from the user in database
                                        likeButtonTestClick=false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error)
                            {

                            }
                        });
                    }
                });

                //Add & View Comments
                holder.commentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent(getApplicationContext(),commentPanel.class);
                        intent.putExtra("videoID",videoID);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row,parent,false);
                return new MyViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    //Menu items
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.menu_items_for_dashboard,menu);
        menu.findItem(R.id.search_menu_item).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    //Menu items functions
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.cloud_upload:
                startActivity(new Intent(getApplicationContext(),addVideo.class));
                finish();
                break;
            case R.id.user_profile:
                startActivity(new Intent(getApplicationContext(),user_profile.class));
                finish();
                break;
            case R.id.about:
                startActivity(new Intent(getApplicationContext(),information.class));
                finish();

                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),index.class));
                Toast.makeText(this, "You have been signed out", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}