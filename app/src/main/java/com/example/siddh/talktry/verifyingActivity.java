package com.example.siddh.talktry;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.IOException;

public class verifyingActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101 ;
    private static final int LOCK_REQUEST_CODE = 221 ;
    private static final int SECURITY_SETTING_REQUEST_CODE = 300;

    ImageView profilepic;
    EditText displayName;
    TextView vuser;
    ProgressBar pb;
    Button logOut;

    FirebaseAuth fat;
    private StorageReference mStorageRef;
    private FirebaseDatabase fbdb;
    private DatabaseReference msgdbrf;
    String emailRec;
    String passRec;

    String profilepicurl;
    Uri profileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        pb = findViewById(R.id.picuploadPB);

        emailRec = getIntent().getExtras().getString("email");
        passRec = getIntent().getExtras().getString("password");

        //Toast.makeText(verifyingActivity.this, "email is " + emailRec + " password is " + passRec, Toast.LENGTH_SHORT).show();
        profilepic = (ImageView) findViewById(R.id.picupload);
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });
    }

    public void newuser (View view)
    {
        fat = FirebaseAuth.getInstance();


        displayName = (EditText) findViewById(R.id.profilename);
        vuser = (TextView) findViewById(R.id.verifiedText);


        // loadUserInfo();
        fbdb = FirebaseDatabase.getInstance();



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
                                Toast.makeText(verifyingActivity.this,"Profile Updated",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            loadUserInfo();

        }
        else{
            Toast.makeText(verifyingActivity.this,"user and image not found",Toast.LENGTH_SHORT).show();
        }
        //startActivity(new Intent(get,CombinedActivity.class));
    }



    protected void loadUserInfo() {

        fat = FirebaseAuth.getInstance();
        final FirebaseUser user = fat.getCurrentUser();

        /*if(user!=null && user.getPhotoUrl() != null && user.getDisplayName() != null){
            Toast.makeText(DashBoard.this,"all good",Toast.LENGTH_SHORT).show();
        }*/
        if(user!=null){

            if(user.isEmailVerified())
            {
                FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
                DatabaseReference userdb = fbdb.getReference().child("users").child(formatEmail(emailRec));
                userDetail user1 = new userDetail(fat.getCurrentUser().getDisplayName(),emailRec,passRec,fat.getCurrentUser().getUid(),fat.getCurrentUser().getPhotoUrl().toString());
                userdb.push().setValue(user1);
                finish();
                Intent i =new Intent(this,CombinedActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
            else
            {
                vuser.setVisibility(View.VISIBLE);
                vuser.setText("Account is not verified (Click to verify or sign in again if already verified)");
                vuser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(verifyingActivity.this,"Verfication Email sent. Check Your Mailbox, Log out and log in again",Toast.LENGTH_LONG).show();
                                logOut = (Button) findViewById(R.id.sgnotbtn);
                                logOut.setVisibility(View.VISIBLE);
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
    }
    /*public void save(View view)
    {


    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            profileImage = data.getData();

            try {
                Bitmap b = MediaStore.Images.Media.getBitmap(getContentResolver(), profileImage);
                profilepic.setImageBitmap(b);
                uploadImage();

            } catch (IOException e) {
                Toast.makeText(verifyingActivity.this,"Error while uploading", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }


    private void uploadImage() {

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
                            Toast.makeText(verifyingActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();

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
    }

    public void activityRedirect ()
    {
        finish();
        Intent i =new Intent(this,CombinedActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void lgout (View view)
    {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        finish();
        startActivity(new Intent(verifyingActivity.this,LoginActivity.class));
    }

    public String formatEmail(String email)
    {
        String fmail;
        fmail = email.substring(0, email.length()-4);
        fmail = fmail.replace('@','X');
        fmail = fmail.replace('.','X');
        //System.out.println(fmail);
        return fmail;
    }
}
