package com.example.siddh.talktry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        startAnimation();

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                //start your activity here
                //startActivity(new Intent(MainActivity.this,Dashboard.class));


                SharedPreferences sp = getSharedPreferences("skip", MODE_PRIVATE);

                Boolean stat = sp.getBoolean("status", false);
                if (!stat) {

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("status", true);
                    editor.apply();
                    Intent i = new Intent(MainActivity.this, WelcomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                    //Toast.makeText(MainActivity.this,"First time",Toast.LENGTH_SHORT).show();
                }
                else {
                    if (mAuth.getCurrentUser() != null) {
                        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {

                            if (user.getProviderId().equals("facebook.com") || user.getProviderId().equals("google.com")) {
                                //For linked facebook account
                                Intent i = new Intent(MainActivity.this, CombinedActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(i);

                            }

                        }
                        if (mAuth.getCurrentUser().isEmailVerified()) {
                            Intent i = new Intent(MainActivity.this, CombinedActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(i);
                        } else if (!mAuth.getCurrentUser().isEmailVerified()) {
                            Intent i = new Intent(MainActivity.this, verifyingActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(i);
                        }

                    } else if (mAuth.getCurrentUser() == null) {
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(i);
                    }
                }

                //Toast.makeText(MainActivity.this,"Not First time",Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(MainActivity.this, DashBoard.class));
            }

        }, 2500L);


    }


    private void startAnimation() {

        Animation anim = AnimationUtils.loadAnimation(this,R.anim.alpha);
        anim.reset();
        RelativeLayout rel = (RelativeLayout) findViewById(R.id.lay1);
        rel.clearAnimation();
        rel.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this,R.anim.translate);
        anim.reset();
        ImageView img = (ImageView) findViewById(R.id.logo1);
        img.clearAnimation();
        img.startAnimation(anim);
    }
}
