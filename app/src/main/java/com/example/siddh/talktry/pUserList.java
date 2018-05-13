package com.example.siddh.talktry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siddh on 28-Mar-18.
 */

public class pUserList extends Fragment {

    private StorageReference mStorageRef;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userdb,frienddb,frienddb1;
    private FirebaseAuth fat;

    FloatingActionButton fb;
    ListView userList;
    TextView nouser;
    userListAdapter msgadapter;
    ProgressBar fupb;
    userListAdapter friendAdapter;
    private ChildEventListener cel,child;
    private Boolean itemSelected = false;
    private int selectedPosition = 0;
    List<friendDetail> friendsList = new ArrayList<>();
    ArrayList<String> listKeys = new ArrayList<String>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_list,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fat = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        fupb = (ProgressBar)  view.findViewById(R.id.finduserPB);

        loadFriends();
        fb = (FloatingActionButton) view.findViewById(R.id.addUserButton);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(getContext());
                View promptView = li.inflate(R.layout.add_user_prompt,null);

                AlertDialog.Builder alertBuild = new AlertDialog.Builder(getContext());
                alertBuild.setView(promptView);

                final EditText newUser = (EditText) promptView.findViewById(R.id.newUser);

                alertBuild.setCancelable(false)
                        .setPositiveButton("ADD",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        fupb.setVisibility(View.VISIBLE);
                                        final String nus = formatEmail(newUser.getText().toString().trim().toLowerCase());
                                        final String email = newUser.getText().toString().trim().toLowerCase();
                                        //Toast.makeText(getContext(),nus,Toast.LENGTH_SHORT).show();

                                        //Toast.makeText(getContext(),"Email entered",Toast.LENGTH_SHORT).show();
                                        userdb = firebaseDatabase.getReference().child("users").child(nus);
                                        userdb.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                //Toast.makeText(getContext(),nus,Toast.LENGTH_SHORT).show();
                                                if(dataSnapshot.exists())
                                                {
                                                    addFriend(nus);
                                                }
                                                else {
                                                    fupb.setVisibility(View.GONE);
                                                    //Toast.makeText(getContext(), "Friend not found, want to invite him/her?", Toast.LENGTH_SHORT).show();
                                                    inviteFriend(email);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                           // Toast.makeText(getActivity(),"User Does not exists",Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertBuild.create();

                // show it
                alertDialog.show();

            }
        });



    }

    public void inviteFriend(final String email)
    {
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptView = li.inflate(R.layout.invite_new_user,null);

        AlertDialog.Builder alertBuild = new AlertDialog.Builder(getContext());
        alertBuild.setView(promptView);

        alertBuild.setCancelable(false)
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent it = new Intent(Intent.ACTION_SEND_MULTIPLE);
                                it.putExtra(Intent.EXTRA_EMAIL,new String[] {email});
                                it.putExtra(Intent.EXTRA_SUBJECT, "Invitation to join TALK chat app");
                                it.putExtra(Intent.EXTRA_TEXT, "Hi! " + fat.getCurrentUser().getDisplayName() + " invited you to join Talk app");
                                //it.setType("text/plain");
                                it.setType("message/rfc822");
                                startActivity(it);
                            }
                        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = alertBuild.create();

        // show it
        alertDialog.show();

    }

    public void loadFriends()
    {
        userList = getView().findViewById(R.id.usersList);
        nouser = getView().findViewById(R.id.noUsersText);

        friendAdapter = new userListAdapter(getActivity(),R.layout.list_users, friendsList);
        userList.setAdapter(friendAdapter);
        attachReadListener();

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                selectedPosition = i;
                itemSelected = true;

                startChat(listKeys.get(selectedPosition));
            }
        });

        userList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                CharSequence options[] = new CharSequence[] {"View Profile","Delete Friend"};
                final int j =i;

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Friend");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        switch (which)
                        {
                            case 0:
                                Fragment fragment = new friendsProfile();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                Bundle bundle = new Bundle();

                                bundle.putString("email",listKeys.get(j));
                                fragment.setArguments(bundle);

                                fragmentTransaction.replace(R.id.nav_screen_area, fragment, "chat_frag").addToBackStack("tag");
                                fragmentTransaction.commit();
                                break;

                            case 1:
                                //Toast.makeText(getContext(),"Delete friend feature",Toast.LENGTH_SHORT).show();
                                frienddb = firebaseDatabase.getReference().child("friends").child(formatEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())+"-friends");
                                frienddb1 = firebaseDatabase.getReference().child("friends").child(listKeys.get(j) +"-friends");
                                frienddb.child(listKeys.get(j)).removeValue();
                                frienddb1.child(formatEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())).removeValue();
                                break;

                        }

                    }
                });
                builder.show();

                return true;
            }
        });

    }
    public void attachReadListener()
    {
        if(cel == null) {
            cel = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    friendDetail friendly;
                    for(DataSnapshot singlesnap: dataSnapshot.getChildren())
                    {
                        friendly = singlesnap.getValue(friendDetail.class);
                        friendAdapter.add(friendly);
                    }

                    Log.d("key",dataSnapshot.getKey());
                    listKeys.add(dataSnapshot.getKey());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    String key = dataSnapshot.getKey();
                    int index = listKeys.indexOf(key);

                    if (index != -1) {
                        listKeys.remove(index);
                        friendsList.remove(index);
                        friendAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            frienddb = firebaseDatabase.getReference().child("friends").child(formatEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())+"-friends");
            frienddb.addChildEventListener(cel);
        }
    }
    public void addFriend(String user1)
    {
        final String user2 = user1;
        userdb = FirebaseDatabase.getInstance().getReference().child("users").child(user1);

        userdb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userDetail friend1=null;
                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                     friend1 = singleSnapshot.getValue(userDetail.class);
                }

                final userDetail friend2 = friend1;
                //Toast.makeText(getContext(),"friend is "+friend1.getName(),Toast.LENGTH_SHORT).show();
                frienddb = firebaseDatabase.getReference().child("friends").child(formatEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())+"-friends").child(user2);
                frienddb1 = firebaseDatabase.getReference().child("friends").child(user2 +"-friends").child(formatEmail(fat.getCurrentUser().getEmail()));
                frienddb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                            Toast.makeText(getContext(),"Friend already added, Scroll the list",Toast.LENGTH_SHORT).show();
                        else
                        {
                            friendDetail newFriend = new friendDetail(friend2.getName(),friend2.getDpPicurl(),friend2.getEmail());
                            frienddb.push().setValue(newFriend);
                            frienddb1.push().setValue(new friendDetail(fat.getCurrentUser().getDisplayName(),fat.getCurrentUser().getPhotoUrl().toString(), fat.getCurrentUser().getEmail()));
                            Toast.makeText(getActivity(),"Chatting with " + friend2.getName(), Toast.LENGTH_SHORT).show();
                            startChat(user2);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });

    }

    public  void startChat(String nus)
    {
        fupb.setVisibility(View.GONE);
        //Toast.makeText(getContext(),"Email exists in database",Toast.LENGTH_SHORT).show();

        Fragment fragment = new pChatFrag();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();

        bundle.putString("email",nus);
        fragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.nav_screen_area, fragment, "chat_frag").addToBackStack("tag");
        fragmentTransaction.commit();
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
