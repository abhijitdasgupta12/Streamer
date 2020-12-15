package com.example.streamer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class commentPanel extends AppCompatActivity
{

    TextInputLayout commentTextInputLayout;
    ImageButton sendComment;
    RecyclerView recyclerView;

    DatabaseReference userReference, commentReference;

    String videoID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_panel);

        setTitle("Comments");

        //Fetching important information
        videoID=getIntent().getStringExtra("videoID");
        userReference= FirebaseDatabase.getInstance().getReference("streamer").child("users");
        commentReference= FirebaseDatabase.getInstance().getReference("streamer").child("videos").child(videoID).child("comments");

        //Getting current user's information
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        final String userID= currentUser.getUid();

        //Typecasting
        commentTextInputLayout= findViewById(R.id.addComments);
        sendComment= findViewById(R.id.sendCommentImageButton);
        recyclerView= findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Setting up default status of send button
        sendComment.setEnabled(false);
        sendComment.setImageResource(R.drawable.ic_baseline_cancel_schedule_send_24);

        commentTextInputLayout.getEditText().addTextChangedListener(checkTexts); //Checking whenever user enters text in the editText



        //Onclick event
        sendComment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //This will fetch the user ID & user's Image from database to add in the comment details
                userReference.child(userID).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.exists())
                        {
                            String username= snapshot.child("user_name").getValue().toString();
                            String userimage= snapshot.child("user_image").getValue().toString();
                            uploadComment(username,userimage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            //User defined method for uploading comment
            private void uploadComment(String username, String userimage)
            {
                String addedComment= commentTextInputLayout.getEditText().getText().toString();
                String commentID= userID+"_"+new Random().nextInt(1000);

                Calendar calendar= Calendar.getInstance(); //Fetches current date & time

                //Current Date
                SimpleDateFormat dateFormat= new SimpleDateFormat("dd-MMMM-YYYY"); //This will display date-month_name-year
                String uploadDate= dateFormat.format(calendar.getTime());

                //Current Time
                SimpleDateFormat timeFormat= new SimpleDateFormat("HH:mm"); //24 hour format
                String uploadTime= timeFormat.format(calendar.getTime());

                HashMap hashMap= new HashMap();
                hashMap.put("user_id",userID);
                hashMap.put("user_image",userimage);
                hashMap.put("user_name",username);
                hashMap.put("time",uploadTime);
                hashMap.put("message",addedComment);
                hashMap.put("date",uploadDate);

                commentReference.child(commentID).updateChildren(hashMap)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task)
                            {
                                if (task.isSuccessful())
                                {
                                    commentTextInputLayout.getEditText().setText("");
                                    sendComment.setEnabled(false);
                                    sendComment.setImageResource(R.drawable.ic_baseline_cancel_schedule_send_24);
                                    Toast.makeText(getApplicationContext(), "Comment added", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(), "Error: "+task.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {

                            }
                        });
            }

        });
    }

    /********* MENU OPTIONS STARTs *********/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_items_for_dashboard, menu);

        menu.findItem(R.id.user_profile).setVisible(false);
        menu.findItem(R.id.search_menu_item).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.about:
                startActivity(new Intent(getApplicationContext(),information.class));
                break;
            case R.id.cloud_upload:
                startActivity(new Intent(getApplicationContext(),addVideo.class));
                break;
            case R.id.user_profile:
                startActivity(new Intent(getApplicationContext(),user_profile.class));
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),index.class));
                Toast.makeText(this, "You have been signed out", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /********* MENU OPTIONS ENDS *********/

    //TextWatcher object implementation to detect texts when user enters
    private TextWatcher checkTexts= new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (commentTextInputLayout.getEditText().getText()!= null)
            {
                sendComment.setEnabled(true);
                sendComment.setImageResource(R.drawable.ic_baseline_send_24);
            }
        }

        @Override
        public void afterTextChanged(Editable s)
        {

        }
    };


    @Override
    protected void onStart()
    {
        super.onStart();

        //Fetching information from database to display inside recyclerview
        FirebaseRecyclerOptions<comments_model> options =
                new FirebaseRecyclerOptions.Builder<comments_model>()
                        .setQuery(commentReference, comments_model.class)
                        .build();

        FirebaseRecyclerAdapter<comments_model,MyViewHolder_DisplayComments> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<comments_model, MyViewHolder_DisplayComments>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder_DisplayComments holder, int position, @NonNull comments_model model) {
                holder.commentUserName.setText(model.getUser_name());
                holder.commentDateTime.setText(model.getDate()+", "+model.getTime());
                holder.commentMessage.setText(model.getMessage());

                Glide.with(holder.circleImageView.getContext()).load(model.getUser_image()).into(holder.circleImageView);
            }

            @NonNull
            @Override
            public MyViewHolder_DisplayComments onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.comments, parent, false);
                return new MyViewHolder_DisplayComments(view);
            }
        };

        //This will start loading comments
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}