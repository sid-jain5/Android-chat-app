package com.example.siddh.talktry;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class pGChatFrag extends Fragment {

    FloatingActionButton fb;
    EditText msgToSend;
    ListView msgList;
    messageAdapter msgadapter;
    String groupId;


    private FirebaseDatabase fbdb;
    private DatabaseReference msgdbrf1,msgdbrf2;
    private ChildEventListener cel;

    private FirebaseAuth fbAuth;
    private FirebaseAuth.AuthStateListener fbAuthStLstnr;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_layout,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //attachDbReadListener();
        //Toast.makeText(getActivity(),"Chat Activity opened",Toast.LENGTH_SHORT);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            groupId = bundle.getString("email", "random");
        }

        if(groupId == "random")
        {
            Toast.makeText(getActivity(),"Friend not found",Toast.LENGTH_SHORT).show();
            Fragment fragment = new groupChatFrag();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.nav_screen_area, fragment);
            fragmentTransaction.commit();
        }

        fbdb = FirebaseDatabase.getInstance();
        fbAuth = FirebaseAuth.getInstance();
        msgdbrf1 = fbdb.getReference().child("groups").child(groupId).child("group_message");

        fb = (FloatingActionButton) view.findViewById(R.id.sendButton);
        msgToSend = (EditText) view.findViewById(R.id.inputMsg);
        msgList = (ListView) view.findViewById(R.id.msgListView);



        List<chatMessage> friendlyMessages = new ArrayList<>();
        msgadapter = new messageAdapter(getActivity(), R.layout.list_items, friendlyMessages);
        msgList.setAdapter(msgadapter);
        attachDbReadListener();
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toSend = msgToSend.getText().toString();
                if (toSend.length() > 0) {
                    chatMessage chatMsg = new chatMessage(toSend,fbAuth.getCurrentUser().getDisplayName(),formatEmail(fbAuth.getCurrentUser().getEmail().toString()));
                    msgdbrf1.push().setValue(chatMsg);
                    //msgdbrf2.push().setValue(chatMsg);
                    msgToSend.setText("");

                } else {
                    Toast.makeText(getActivity(),"Empty Message",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void attachDbReadListener(){
        if(cel == null) {
            cel = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    for(DataSnapshot sigleSnap: dataSnapshot.getChildren())
                    {
                        chatMessage friendlyMessage = dataSnapshot.getValue(chatMessage.class);
                        msgadapter.add(friendlyMessage);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            msgdbrf1.addChildEventListener(cel);
        }
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
