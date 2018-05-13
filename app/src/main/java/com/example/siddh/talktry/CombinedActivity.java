package com.example.siddh.talktry;

import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class CombinedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int CHOOSE_IMAGE = 101 ;
    private static final int LOCK_REQUEST_CODE = 221 ;
    private static final int SECURITY_SETTING_REQUEST_CODE = 300;

    private StorageReference mStorageRef;
    private FirebaseDatabase firebaseDatabase;

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
        setContentView(R.layout.activity_combined);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Talk");
        getSupportActionBar().setIcon(R.drawable.camera);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fat = FirebaseAuth.getInstance();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //authenticating app

        SharedPreferences sp1 = getSharedPreferences("authenticate",MODE_PRIVATE);

        Boolean status = sp1.getBoolean("auth",true);
        if(status)
            authenticateApp();
        /////
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.navUsername);
        navUsername.setText(fat.getCurrentUser().getDisplayName().toString());

        TextView navEmail = (TextView) headerView.findViewById(R.id.navEmailid);
        navEmail.setText(fat.getCurrentUser().getEmail().toString());

       ImageView dpPic = (ImageView) headerView.findViewById(R.id.navimageView);

        String photoUrl = fat.getCurrentUser().getPhotoUrl().toString();
        if(photoUrl != null)
        {
            Glide.with(CombinedActivity.this)
                    .load(photoUrl)
                    .into(dpPic);
        }

        Fragment fragment = new pUserList();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.nav_screen_area, fragment);
        fragmentTransaction.commit();
    }

    private void authenticateApp() {

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
                    Toast.makeText(CombinedActivity.this,"Unable to put screen lock", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    protected void onStart() {
        super.onStart();
        fat = FirebaseAuth.getInstance();
        if (fat.getCurrentUser() == null){
            finish();
            startActivity(new Intent(CombinedActivity.this,LoginActivity.class));
        }
    }

    Boolean exit = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fragmentManager =getSupportFragmentManager();
            Fragment myFragment = fragmentManager.findFragmentByTag("chat_frag");
            if (myFragment != null && myFragment.isVisible()) {
                // add your code here
                Fragment fragment = new pUserList();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.nav_screen_area, fragment);
                fragmentTransaction.commit();
            }
            else{
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
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == LOCK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //If screen lock authentication is success update text


                android.support.v7.widget.Toolbar toolbar = findViewById(R.id.logoutTool);
                setSupportActionBar(toolbar);

            } else {
                //If screen lock authentication is failed update text
                authenticateApp();
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
                Toast.makeText(CombinedActivity.this,"Unable to put screen lock", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.deleteButton).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.menuLogout:

                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                finish();
                startActivity(new Intent(CombinedActivity.this,LoginActivity.class));

                break;

        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_chat) {
            // Handle the camera action
            fragment = new pUserList();
        } else if (id == R.id.nav_group) {

            fragment = new groupChatFrag();

        } /*else if (id == R.id.nav_media) {

        } */else if (id == R.id.nav_profile) {

            fragment = new profileFrag();

        } else if (id == R.id.nav_settings) {

            startActivity(new Intent(CombinedActivity.this,settingsActivity.class));


        } else if (id == R.id.nav_logOut) {

            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            finish();
            startActivity(new Intent(CombinedActivity.this,LoginActivity.class));

        } else if (id == R.id.nav_about) {

            startActivity(new Intent(CombinedActivity.this,aboutUs.class));


        } else if(id == R.id.nav_nearby) {

            startActivity(new Intent(CombinedActivity.this,MapsActivity.class));
        }

        if(fragment!=null){

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.nav_screen_area, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
