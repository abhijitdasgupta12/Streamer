package com.example.streamer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class user_profile extends AppCompatActivity
{
    TextView userNameTextView;
    CircleImageView userImages;

    DatabaseReference databaseReference;

    String UID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setTitle("Your Profile");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        userImages= findViewById(R.id.userCircularImageView);
        userNameTextView= findViewById(R.id.userNameTextView);

        databaseReference= FirebaseDatabase.getInstance().getReference("streamer").child("users");

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        UID= firebaseUser.getUid();

        //Checking profile information status & displaying in the views
        databaseReference.child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    userNameTextView.setText(snapshot.child("user_name").getValue().toString());
                    Glide.with(getApplicationContext()).load(snapshot.child("user_image").getValue().toString()).into(userImages);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /************************** OnClick Events **************************/
    public void goToProfileSettings(View view)
    {
        startActivity(new Intent(getApplicationContext(),profile_settings.class));
    }

    public void goToMyUploads(View view)
    {
        startActivity(new Intent(getApplicationContext(),timeline_posts.class));
    }

    public void logoutProcess(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),index.class));
        Toast.makeText(getApplicationContext(), "You have been signed out", Toast.LENGTH_SHORT).show();
    }

    /************************** Menu Configuration & Setting Up **************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.menu_items_for_dashboard,menu);

        menu.findItem(R.id.user_profile).setVisible(false);
        menu.findItem(R.id.search_menu_item).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.cloud_upload:
                startActivity(new Intent(getApplicationContext(),addVideo.class));
                break;
            case R.id.about:
                startActivity(new Intent(getApplicationContext(),information.class));
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