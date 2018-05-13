package com.example.siddh.talktry;

import android.app.KeyguardManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class DashBoard extends AppCompatActivity {

    

    private static final int CHOOSE_IMAGE = 101 ;
    private static final int LOCK_REQUEST_CODE = 221 ;
    private static final int SECURITY_SETTING_REQUEST_CODE = 300;
    ImageView profilepic;
    EditText displayName;

    ProgressBar pb;
    private StorageReference mStorageRef;
    private FirebaseDatabase firebaseDatabase;

    String profilepicurl;
    Uri profileImage;

    FirebaseAuth fat;

    /* TODO

    1 PROFILE
    2 PERSONAL MESSAGES
    3 GROUP MESSAGES
    4 MEDIA
    5 NOTIFICATIONS
    6 SETTINGS

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        
        //authenticateApp();


    }

   /* private void authenticateApp() {

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Create an intent to open device screen lock screen to authenticate
            //Pass the Screen Lock screen Title and Description
            Intent i = keyguardManager.createConfirmDeviceCredentialIntent("Unlock", "Confirm your screen lock PIN,Pattern or Password");
            try {
                //Start activity for result
                startActivityForResult(i, LOCK_REQUEST_CODE);
            } catch (Exception e) {

                //If some exception occurs means Screen lock is not set up please set screen lock
                //Open Security screen directly to enable patter lock
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                try {

                    //Start activity for result
                    startActivityForResult(intent, SECURITY_SETTING_REQUEST_CODE);
                } catch (Exception ex) {

                    //If app is unable to find any Security settings then user has to set screen lock manually
                    Toast.makeText(DashBoard.this,"Unable to put screen lock", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }*/



   /* @Override
    protected void onStart() {
        super.onStart();
        fat = FirebaseAuth.getInstance();
        if (fat.getCurrentUser() == null){
            finish();
            startActivity(new Intent(DashBoard.this,LoginActivity.class));
        }
    }*/

    /*protected void loadUserInfo() {

        fat = FirebaseAuth.getInstance();
        final FirebaseUser user = fat.getCurrentUser();

        profilepic = (ImageView) findViewById(R.id.picupload);
        displayName = (EditText) findViewById(R.id.profilename);
        TextView vuser = (TextView) findViewById(R.id.verifiedText);

        /*if(user!=null && user.getPhotoUrl() != null && user.getDisplayName() != null){
            Toast.makeText(DashBoard.this,"all good",Toast.LENGTH_SHORT).show();
        }*/
        /*if(user!=null){

            if(user.getPhotoUrl() != null){
                String photoUrl = user.getPhotoUrl().toString();
                Glide.with(DashBoard.this)
                        .load(photoUrl)
                        .into(profilepic);
                //Toast.makeText(DashBoard.this,"image found",Toast.LENGTH_SHORT).show();
            }
            else {
                //Toast.makeText(DashBoard.this,"image not found",Toast.LENGTH_SHORT).show();
            }
            if(user.getDisplayName() != null) {
                displayName.setText(user.getDisplayName());
            }
            else {
                //Toast.makeText(DashBoard.this,"display not found",Toast.LENGTH_SHORT).show();
            }
            if(user.isEmailVerified())
            {
                vuser.setText("Account is verified");
            }
            else
            {
                vuser.setText("Account is not verified (Click to verify or sign in again if already verified)");
                vuser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(DashBoard.this,"Verfication Email sent. Check YOur Mailbox",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        }

        else {
            //Toast.makeText(DashBoard.this,"user not found",Toast.LENGTH_SHORT).show();
        }
       /*ArrayList<String> resultIAV = getFilePaths();
        DatabaseReference msg = FirebaseDatabase.getInstance().getReference().child("Images");
        msg.push().setValue("user");*/
    //}

    /*Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }*/
   /* public void save(View view)
    {
        fat = FirebaseAuth.getInstance();

        displayName = findViewById(R.id.profilename);
        String displName = displayName.getText().toString();

        if(displName.isEmpty()){
            displayName.setError("Name Required");
            displayName.requestFocus();
            return;
        }

        FirebaseUser user = fat.getCurrentUser();
        if(user!=null && profilepicurl!=null){

            //Toast.makeText(DashBoard.this,"user and image found",Toast.LENGTH_SHORT).show();

            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displName)
                    .setPhotoUri(Uri.parse(profilepicurl))
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(DashBoard.this,"Profile Updated",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
        else{
            //Toast.makeText(DashBoard.this,"user and image not found",Toast.LENGTH_SHORT).show();
        }

        startActivity(new Intent(DashBoard.this,CombinedActivity.class));

    }*/

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData()!=null){

            profileImage = data.getData();

            try {
                Bitmap b = MediaStore.Images.Media.getBitmap(getContentResolver(),profileImage);
                profilepic.setImageBitmap(b);

                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        /*if(requestCode == LOCK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //If screen lock authentication is success update text


                android.support.v7.widget.Toolbar toolbar = findViewById(R.id.logoutTool);
                setSupportActionBar(toolbar);

                profilepic = (ImageView) findViewById(R.id.picupload);

                loadUserInfo();

                profilepic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imageChooser();
                    }
                });
            } else {
                //If screen lock authentication is failed update text
                Toast.makeText(DashBoard.this,"Invalid",Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == SECURITY_SETTING_REQUEST_CODE) {
            //When user is enabled Security settings then we don't get any kind of RESULT_OK
            //So we need to check whether device has enabled screen lock or not
            if (isDeviceSecure()) {
                //If screen lock enabled show toast and start intent to authenticate user
                authenticateApp();
            } else {
                //If screen lock is not enabled just update text
                Toast.makeText(DashBoard.this,"Unable to put screen lock", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private boolean isDeviceSecure() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        //this method only work whose api level is greater than or equal to Jelly_Bean (16)
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && keyguardManager.isKeyguardSecure();

        //You can also use keyguardManager.isDeviceSecure(); but it requires API Level 23

    }

    //On Click of button do authentication again
    public void authenticateAgain(View view) {
        authenticateApp();
    }*/


    /*private void uploadImage() {

        pb = findViewById(R.id.picuploadPB);
        mStorageRef = FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis() + ".jpg");
        if(profileImage != null)
        {
            pb.setVisibility(View.VISIBLE);
            mStorageRef.putFile(profileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pb.setVisibility(View.GONE);
                    profilepicurl = taskSnapshot.getDownloadUrl().toString();

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DashBoard.this, e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });

        }

    }

    private void imageChooser()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select Profile Image"), CHOOSE_IMAGE);

    }*/

    //@Override
   /* public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menuLogout:

                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                finish();
                startActivity(new Intent(DashBoard.this,LoginActivity.class));

                break;
        }
        return true;
    }*/

   /* public ArrayList<String> getFilePaths()
    {


        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA};
        Cursor c = null;
        SortedSet<String> dirList = new TreeSet<String>();
        ArrayList<String> resultIAV = new ArrayList<String>();

        String[] directories = null;
        if (u != null)
        {
            c = managedQuery(u, projection, null, null, null);
        }

        if ((c != null) && (c.moveToFirst()))
        {
            do
            {
                String tempDir = c.getString(0);
                tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                try{
                    dirList.add(tempDir);
                }
                catch(Exception e)
                {

                }
            }
            while (c.moveToNext());
            directories = new String[dirList.size()];
            dirList.toArray(directories);

        }

        for(int i=0;i<dirList.size();i++)
        {
            File imageDir = new File(directories[i]);
            File[] imageList = imageDir.listFiles();
            if(imageList == null)
                continue;
            for (File imagePath : imageList) {
                try {

                    if(imagePath.isDirectory())
                    {
                        imageList = imagePath.listFiles();

                    }
                    if ( imagePath.getName().contains(".jpg")|| imagePath.getName().contains(".JPG")
                            || imagePath.getName().contains(".jpeg")|| imagePath.getName().contains(".JPEG")
                            || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG")
                            || imagePath.getName().contains(".gif") || imagePath.getName().contains(".GIF")
                            || imagePath.getName().contains(".bmp") || imagePath.getName().contains(".BMP")
                            )
                    {



                        String path= imagePath.getAbsolutePath();
                        resultIAV.add(path);

                    }
                }
                //  }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return resultIAV;
    }*/
}
