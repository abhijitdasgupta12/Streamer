package com.example.streamer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile_settings extends AppCompatActivity
{

    Button update, reset;
    TextInputLayout setUserName, setUserEmail, setUserCountry;
    CircleImageView userImage;

    DatabaseReference databaseReference;
    StorageReference storageReference;

    Bitmap bitmap;
    Uri filepath;

    String UID="";
    String UEMAIL="";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        setTitle("Profile Settings");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        update= findViewById(R.id.updateButton);
        reset= findViewById(R.id.resetFieldsButton);
        setUserName= findViewById(R.id.profileNameTextInputLayout);
        setUserEmail= findViewById(R.id.setUserEmailTextInputLayout);
        setUserCountry= findViewById(R.id.setCountryTextInputLayout);
        userImage= findViewById(R.id.setProfileImageCircularImageView);

        //Initializing the DatabaseReference & StorageReference objects
        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference("streamer").child("users");

        //Fetching Uid & Email of current logged in user from firebase
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        UID= firebaseUser.getUid();
        UEMAIL= firebaseUser.getEmail();
        setUserEmail.getEditText().setText(UEMAIL); //This will fetch the Email of the current logged in user

        //Checking profile information status & displaying in the views
        databaseReference.child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    setUserCountry.getEditText().setText(snapshot.child("user_country").getValue().toString());
                    Glide.with(getApplicationContext()).load(snapshot.child("user_image").getValue().toString()).into(userImage);
                    setUserName.getEditText().setText(snapshot.child("user_name").getValue().toString());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Browse Image
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Dexter.withActivity(profile_settings.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response)
                            {
                                Intent intent= new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent,101);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        //To update the profile
        update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateProcess();
            }
        });

        //Resets the fields except email
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                setUserName.getEditText().setText("");
                setUserCountry.getEditText().setText("");
                userImage.setImageResource(R.drawable.user);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode==101 && resultCode==RESULT_OK)
        {
            filepath= data.getData();

            try
            {
                InputStream inputStream= getContentResolver().openInputStream(filepath);
                bitmap= BitmapFactory.decodeStream(inputStream);
                userImage.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /************************** Menu Configuration & Setting Up **************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_items_for_dashboard, menu);

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

    //User defined process
    private void updateProcess()
    {
        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Upload");
        progressDialog.show();

        final StorageReference reference= storageReference.child("streamer_uploads/userImage/"+System.currentTimeMillis()+".jpg");
        reference.putFile(filepath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                final Map<String, Object> map=new HashMap<>();
                                map.put("user_country",setUserCountry.getEditText().getText().toString());
                                map.put("user_email",UEMAIL);
                                map.put("user_image",uri.toString());
                                map.put("user_name", setUserName.getEditText().getText().toString());

                                databaseReference.child(UID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                    {

                                        if (snapshot.exists() || snapshot.hasChildren())
                                        {
                                                databaseReference.child(UID).updateChildren(map);
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Profile Update Success", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            databaseReference.child(UID).setValue(map);
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Profile Information Uploaded", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error)
                                    {
                                        Toast.makeText(profile_settings.this, "Error: "+error.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        });
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot)
                    {
                        final float percent= (100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        progressDialog.setMessage("Image upload "+(int)percent+"%");
                    }
                });
    }
}