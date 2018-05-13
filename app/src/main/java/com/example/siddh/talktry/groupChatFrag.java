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
import android.support.v4.content.res.TypedArrayUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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
 * Created by siddh on 20-Mar-18.
 */

public class groupChatFrag extends Fragment {

    private StorageReference mStorageRef;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userdb,groupdb;
    private FirebaseAuth fat;
    private ChildEventListener cel;

    FloatingActionButton fb;
    ListView groupList;
    TextView nouser;
    groupAdapter grpAdapter;
    ProgressBar fupb;
    private Boolean itemSelected = false;
    private int selectedPosition = 0;
    List<groupDetail> groups = new ArrayList<>();
    ArrayList<String> listKeys = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_list,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Toast.makeText(getActivity(),"Group Chat Activity opened",Toast.LENGTH_SHORT).show();

        fat = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        fupb = (ProgressBar)  view.findViewById(R.id.finduserPB);

        loadGroups();
        fb = (FloatingActionButton) view.findViewById(R.id.addUserButton);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(getContext());
                View promptView = li.inflate(R.layout.add_user_prompt,null);


                AlertDialog.Builder alertBuild = new AlertDialog.Builder(getContext());
                alertBuild.setView(promptView);

                final EditText newUser = (EditText) promptView.findViewById(R.id.newUser);
                newUser.setHint("Group name");

                alertBuild.setCancelable(false)
                        .setPositiveButton("Create",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        fupb.setVisibility(View.VISIBLE);
                                        final String gname = newUser.getText().toString().trim().toLowerCase();
                                        String gname2 = gname + " ";
                                        int f = fat.getCurrentUser().getDisplayName().indexOf(" ");
                                        int k = gname2.indexOf(" ");
                                        String group = fat.getCurrentUser().getDisplayName().toLowerCase().substring(0,f);
                                        gname2 = gname2.substring(0,k);
                                        addGroup(group+gname2, gname);

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

    public void addGroup(String id, String name)
    {

        final String name2 = name;
        final String id2 = id;
        groupdb = FirebaseDatabase.getInstance().getReference().child("groups").child(id2);

        //Toast.makeText(getActivity(),"add group" + name, Toast.LENGTH_SHORT).show();

        groupdb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    Toast.makeText(getActivity(),"Group already exists",Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(getActivity(),"add group inside else " + name2, Toast.LENGTH_SHORT).show();
                    String creator = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toString();
                    groupDetail gpDetail = new groupDetail(id2, name2,creator);
                    groupdb.push().setValue(gpDetail);
                    groupdb.child("group_members").push().setValue(formatEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                    FirebaseDatabase.getInstance().getReference().child("user-groups").child(formatEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())).push().setValue(gpDetail);
                    groupdb.child("group_message").push().setValue("Welcome to " + id2);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fupb.setVisibility(View.GONE);
    }

    public void addMembers(final int pos)
    {
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
                                            addFriend(nus,pos);
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
    public void addFriend(final String nus, int pos)
    {
        final String groupid = listKeys.get(pos);
        final DatabaseReference groupFind = FirebaseDatabase.getInstance().getReference().child("groups").child(groupid).child("group_members");
        Toast.makeText(getActivity(), "adding friend in " + groupid, Toast.LENGTH_SHORT).show();

        groupFind.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fupb.setVisibility(View.GONE);
                if(!dataSnapshot.exists())
                    Toast.makeText(getActivity(),"Error adding member",Toast.LENGTH_SHORT).show();
                else {
                    groupFind.push().setValue(nus);
                    FirebaseDatabase.getInstance().getReference().child("user-groups").child(nus).push().setValue(groupid);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
    public void loadGroups()
    {
        groupList = getView().findViewById(R.id.usersList);
        nouser = getView().findViewById(R.id.noUsersText);

        grpAdapter = new groupAdapter(getActivity(),R.layout.list_groups, groups);
        groupList.setAdapter(grpAdapter);
        attachReadListener();

        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedPosition = i;
                itemSelected = true;

                startChat(listKeys.get(selectedPosition));
            }
        });

        groupList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                CharSequence options[] = new CharSequence[]{"Add Member", "View members"};
                final int j = i;

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Friend");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        switch (which) {
                            case 0:
                                addMembers(j);
                                break;

                            case 1:
                                Toast.makeText(getContext(),"View member feature",Toast.LENGTH_SHORT).show();
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

                    groupDetail group1 = dataSnapshot.getValue(groupDetail.class);
                    //for(DataSnapshot singlsnap: dataSnapshot.getChildren())
                    //{
                        //group1 =singlsnap.getValue(groupDetail.class);
                        grpAdapter.add(group1);
                    //}

                    Log.d("key_group",dataSnapshot.getKey());
                    listKeys.add(group1.getGroupId());
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
                        groups.remove(index);
                        grpAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            DatabaseReference groupDB = firebaseDatabase.getReference().child("user-groups").child(formatEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
            groupDB.addChildEventListener(cel);
        }
    }

    public void startChat(String nus)
    {
        fupb.setVisibility(View.GONE);
        //Toast.makeText(getContext(),"Email exists in database",Toast.LENGTH_SHORT).show();

        Fragment fragment = new pGChatFrag();
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
