package com.example.siddh.talktry;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import static android.app.Activity.RESULT_OK;

/**
 * Created by siddh on 23-Mar-18.
 */

public class profileFrag extends Fragment {

    private static final int CHOOSE_IMAGE = 101 ;
    private static final int LOCK_REQUEST_CODE = 221 ;
    private static final int SECURITY_SETTING_REQUEST_CODE = 300;

    ImageView profilepic;
    EditText displayName;
    TextView vuser;
    ProgressBar pb;

    FirebaseAuth fat;
    private StorageReference mStorageRef;
    private FirebaseDatabase fbdb;
    private DatabaseReference msgdbrf;

    String profilepicurl;
    Uri profileImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_verify,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getActivity(),"Group Chat Activity opened",Toast.LENGTH_SHORT);
        profilepic = (ImageView) view.findViewById(R.id.picupload);
        displayName = (EditText) view.findViewById(R.id.profilename);
        pb = view.findViewById(R.id.picuploadPB);
        vuser = (TextView) view.findViewById(R.id.profileEmail);

        loadUserInfo();
        fbdb = FirebaseDatabase.getInstance();

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        Button save = (Button) view.findViewById(R.id.savepic);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fat = FirebaseAuth.getInstance();
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
                                        Toast.makeText(getActivity(),"Profile Updated",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
                else{
                    Toast.makeText(getActivity(),"user or image not found",Toast.LENGTH_SHORT).show();
                }

                //startActivity(new Intent(get,CombinedActivity.class));
            }
        });
    }

    protected void loadUserInfo() {

        fat = FirebaseAuth.getInstance();
        final FirebaseUser user = fat.getCurrentUser();

        /*if(user!=null && user.getPhotoUrl() != null && user.getDisplayName() != null){
            Toast.makeText(DashBoard.this,"all good",Toast.LENGTH_SHORT).show();
        }*/
        if(user!=null){

            if(user.getPhotoUrl() != null){
                String photoUrl = user.getPhotoUrl().toString();
                Glide.with(getActivity())
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
            if(user.getEmail()!=null)
            {
                vuser.setText(user.getEmail());
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
                Bitmap b = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), profileImage);
                profilepic.setImageBitmap(b);

                uploadImage();
            } catch (IOException e) {
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
                            Toast.makeText(getActivity(), e.getMessage(),Toast.LENGTH_SHORT).show();

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

}
