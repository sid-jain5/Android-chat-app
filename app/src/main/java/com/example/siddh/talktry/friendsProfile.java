package com.example.siddh.talktry;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class friendsProfile extends Fragment {

    String friend;
    TextView displayName,vuser;
    ImageView profilepic;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends_profile,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            friend = bundle.getString("email", "random");
        }

        profilepic = (ImageView) view.findViewById(R.id.picupload);
        displayName = (TextView) view.findViewById(R.id.profilename);
        vuser = (TextView) view.findViewById(R.id.profileEmail);

        DatabaseReference userdb = FirebaseDatabase.getInstance().getReference().child("users").child(friend);

        userdb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userDetail friend1=null;
                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                    friend1 = singleSnapshot.getValue(userDetail.class);
                }

                loadUserInfo(friend1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    protected void loadUserInfo(userDetail friend3) {


        /*if(user!=null && user.getPhotoUrl() != null && user.getDisplayName() != null){
            Toast.makeText(DashBoard.this,"all good",Toast.LENGTH_SHORT).show();
        }*/

                String photoUrl = friend3.getDpPicurl();
                Glide.with(getActivity())
                        .load(photoUrl)
                        .into(profilepic);
                //Toast.makeText(DashBoard.this,"image found",Toast.LENGTH_SHORT).show();

                displayName.setText(friend3.getName());

                vuser.setText(friend3.getEmail());

    }
}
