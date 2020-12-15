package com.example.streamer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class information extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        setTitle("Info");
    }

    /********* MENU OPTIONS START *********/
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

}