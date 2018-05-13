package com.example.siddh.talktry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class settingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView reset = (TextView) findViewById(R.id.resetPass);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(settingsActivity.this);
                View promptView = li.inflate(R.layout.add_user_prompt, null);

                AlertDialog.Builder alertBuild = new AlertDialog.Builder(settingsActivity.this);
                alertBuild.setView(promptView);

                final EditText newUser = (EditText) promptView.findViewById(R.id.newUser);
                newUser.setHint("Enter new password");

                alertBuild.setCancelable(false)
                        .setPositiveButton("Change",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        String pass = newUser.getText().toString().trim();

                                        if(pass.isEmpty() || pass.length()<6)
                                        {
                                            newUser.setError("Password length less than six characters");
                                            newUser.requestFocus();
                                            return;
                                        }

                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        user.updatePassword(pass)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            Toast.makeText(settingsActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

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

        final Switch finger = (Switch) findViewById(R.id.fingerSwitch);

        SharedPreferences sp1 = getSharedPreferences("authenticate",MODE_PRIVATE);

        Boolean status = sp1.getBoolean("auth",true);

        if(status)
            finger.setChecked(true);
        else
            finger.setChecked(false);

        finger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sp1 = getSharedPreferences("authenticate",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp1.edit();

                if(b)
                {

                    editor.putBoolean("auth", true);
                    editor.apply();
                }
                else
                {

                    editor.putBoolean("auth", false);
                    editor.apply();
                }
            }
        });
    }
}
