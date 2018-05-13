package com.example.siddh.talktry;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class groupAdapter extends ArrayAdapter<groupDetail> {

    public groupAdapter(@NonNull Context context, int resource, @NonNull List<groupDetail> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.list_groups, parent, false);
        }

        groupDetail s = getItem(position);

        TextView messageTextView = (TextView) convertView.findViewById(R.id.groupName);

        messageTextView.setText(s.getGroupName());

        return convertView;
    }

    @Nullable
    @Override
    public groupDetail getItem(int position) {
        return super.getItem(position);
    }
}
