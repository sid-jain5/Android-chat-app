package com.example.siddh.talktry;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPasword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pasword);
    }

    public void forgetFunc(View view)
    {
        EditText forgotMail = (EditText) findViewById(R.id.forgotEmailAddr);
        String mail = forgotMail.getText().toString().trim().toLowerCase();

        if(mail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mail).matches())
        {
            forgotMail.setError("Email id required");
            forgotMail.requestFocus();
            return;
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(mail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(forgotPasword.this,"Reset mail sent to your email address",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(forgotPasword.this,LoginActivity.class));
                        }
                        else
                            Toast.makeText(forgotPasword.this,"Error while sending mail to your email address",Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
