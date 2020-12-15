package com.example.streamer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class addVideo extends AppCompatActivity
{
    VideoView videoView;
    TextInputLayout videoTitleTextInputLayout;
    ImageButton browse, upload;

    Uri videoUri;

    MediaController mediaController;
    StorageReference storageReference;
    DatabaseReference databaseReference, userInfoDatabaseReference;

    String currentUser_ID="";
    String currentUser_name="";
    String currentUserImage="";
    String currentDate="";
    String currentTime="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);

        setTitle("Upload Video");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Typecasting
        videoView= findViewById(R.id.videoView);
        videoTitleTextInputLayout= findViewById(R.id.videoTitle);
        browse= findViewById(R.id.browseImageButton);
        upload= findViewById(R.id.uploadImageButton);

        //If no video is selected then the upload button will be disabled to prevent app crash
        if (videoUri==null)
        {
            upload.setEnabled(false);
            upload.setImageResource(R.drawable.ic_baseline_cloud_off_24);
        }

        //Storage & Database
        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference("streamer").child("videos");
        userInfoDatabaseReference= FirebaseDatabase.getInstance().getReference("streamer").child("users");

        //Setting up video playing process
        mediaController=new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();

        /**** Getting Current User Information and Current Date & Time ****/
        //Getting Current User Information
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        currentUser_ID= firebaseUser.getUid();

        //Checking profile information status
        userInfoDatabaseReference.child(currentUser_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    currentUser_name= snapshot.child("user_name").getValue().toString();
                    currentUserImage=snapshot.child("user_image").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Getting Current Time & Date
        Calendar calendar= Calendar.getInstance(); //Fetches current date & time
        //Current Date
        SimpleDateFormat dateFormat= new SimpleDateFormat("dd-MMMM-YYYY"); //This will display date-month_name-year
        currentDate= dateFormat.format(calendar.getTime());
        //Current Time
        SimpleDateFormat timeFormat= new SimpleDateFormat("HH:mm"); //24 hour format
        currentTime= timeFormat.format(calendar.getTime());

        //OnClick event for browse videos
        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(addVideo.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response)
                            {
                                Intent intent= new Intent();
                                intent.setType("video/*");
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

        //OnClick event for upload process
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                uploadProcess();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==101 && resultCode==RESULT_OK)
        {
            videoUri= data.getData();
            videoView.setVideoURI(videoUri);
            upload.setEnabled(true);
            upload.setImageResource(R.drawable.ic_baseline_cloud_upload_24);
            Toast.makeText(getApplicationContext(), "Playing video from device", Toast.LENGTH_SHORT).show();
        }
    }

    //To fetch the extention type of file
    public String getFileExtention()
    {
        MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(getContentResolver().getType(videoUri));
    }

    //user defined method
    private void uploadProcess()
    {
        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Upload");
        progressDialog.show();

        final StorageReference reference= storageReference.child("streamer_uploads/"+System.currentTimeMillis()+"."+getFileExtention());
        reference.putFile(videoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                filemodel obj= new filemodel(currentDate, currentTime, currentUser_ID, currentUserImage, currentUser_name,
                                                            videoTitleTextInputLayout.getEditText().getText().toString(),
                                                            uri.toString());

                                databaseReference.child(databaseReference.push().getKey()).setValue(obj)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid)
                                            {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Upload success", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(),dashboard.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e)
                                            {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Upload failed! Details: "+e.getMessage(), Toast.LENGTH_LONG).show();
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
                        float progress=  ((100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount());

                        progressDialog.setMessage("Uploading Video "+(int)progress+"%");
                    }
                });

    }

    /********************** Working on menu items & their functions **********************/
    //Menu items
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.menu_items_for_dashboard,menu);

        menu.findItem(R.id.cloud_upload).setVisible(false); //Disabling upload button for this specific activity only
        menu.findItem(R.id.search_menu_item).setVisible(false);//Disabling search button for this specific activity only

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