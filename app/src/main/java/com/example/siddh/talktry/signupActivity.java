package com.example.siddh.talktry;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signupActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseDatabase fbdb;
    private DatabaseReference userdb;

    private ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        fbdb = FirebaseDatabase.getInstance();
       // userdb = fbdb.getReference().child("users");
    }

    /*
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        startActivity(new Intent(signupActivity.this,DashBoard.class));
    }*/

    public void checkFunc (View view)
    {
        EditText em = (EditText) findViewById(R.id.username1);
        EditText pswrd = (EditText) findViewById(R.id.userpassword1);
        pb = (ProgressBar) findViewById(R.id.signupPB);
        final String email = em.getText().toString().trim();
        final String pass = pswrd.getText().toString().trim();

        if (email.isEmpty())
        {
            em.setError("email is required");
            em.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            em.setError("email is invalid");
            em.requestFocus();
            return;
        }

        if (pass.isEmpty())
        {
            pswrd.setError("password is required");
            pswrd.requestFocus();
            return;
        }

        if (pass.length() < 6)
        {
            pswrd.setError("password is short. Atleast 6 chars required");
            pswrd.requestFocus();
            return;
        }

        pb.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                pb.setVisibility(View.GONE);

                if(task.isSuccessful())
                {
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    finish();
                    Toast.makeText(signupActivity.this,"Succesfully registered",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(signupActivity.this, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                else
                {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                        Toast.makeText(signupActivity.this,"You are already registered",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(signupActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void loginRedirect (View view)
    {
        finish();
        Intent i =new Intent(this,LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
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
