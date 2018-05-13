package com.example.siddh.talktry;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class userListAdapter extends ArrayAdapter <friendDetail> {


    public userListAdapter(@NonNull Context context, int resource, List<friendDetail> objects) {
        super(context, resource,objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.list_users, parent, false);
        }

        TextView messageTextView = (TextView) convertView.findViewById(R.id.user_name);
        ImageView userdp = (ImageView) convertView.findViewById(R.id.userdp);

        friendDetail friend = getItem(position);

        messageTextView.setText(friend.getName());

        Glide.with(userdp.getContext())
                .load(friend.getProfilePicURi())
                .apply(new RequestOptions().placeholder(R.drawable.send_button).error(R.drawable.camera))
                .into(userdp);

        return convertView;
    }

    @Nullable
    @Override
    public friendDetail getItem(int position) {
        return super.getItem(position);
    }
}
