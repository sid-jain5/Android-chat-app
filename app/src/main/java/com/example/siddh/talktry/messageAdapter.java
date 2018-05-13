package com.example.siddh.talktry;

import android.app.Activity;
import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by siddh on 28-Mar-18.
 */

public class messageAdapter extends ArrayAdapter<chatMessage> {

    public messageAdapter(@NonNull Context context, int resource, List<chatMessage> objects) {
        super(context, resource, objects);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.list_items, parent, false);
        }

        final TextView messageTextView = (TextView) convertView.findViewById(R.id.message_text);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.message_user);
        //TextView timeTextView = (TextView) convertView.findViewById(R.id.message_time);
        final ImageView sender = (ImageView) convertView.findViewById(R.id.senderDp);
        //RelativeLayout msgT = (RelativeLayout) convertView.findViewById(R.id.textDetail);

        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        final chatMessage message = getItem(position);

        String mailer = message.getUserMailFormatted();
        DatabaseReference user = fbdb.getReference().child("users").child(mailer);
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                    for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                        userDetail sendUser = singleSnapshot.getValue(userDetail.class);
                        String picUrl = sendUser.getDpPicurl();

                        Glide.with(sender.getContext())
                                .load(picUrl)
                                .into(sender);
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Log.d("name",name);
       /* if(name != FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,Gravity.RIGHT);
            LinearLayout lv = (LinearLayout) convertView.findViewById(R.id.msgSab);
            lv.setLayoutParams(params);
        }*/

        messageTextView.setText(message.getMessageText());
        authorTextView.setText(message.getMessageUser());
        //timeTextView.setText(message.getMessageTime());

        return convertView;
    }
}
