package com.example.siddh.talktry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 202 ;
    private FirebaseAuth mAuth;
    private FirebaseDatabase fbdb;
    private DatabaseReference userdb;
    private ProgressBar pb;
    private CallbackManager mCallbackManager;
    private Button fbLogIn;
    private GoogleApiClient mGoogleApiClient;
    String codeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        fbdb = FirebaseDatabase.getInstance();

        TextView forgot = (TextView) findViewById(R.id.forgotPass);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this,forgotPasword.class));

            }
        });


        /*EditText usname = (EditText) findViewById(R.id.fbmail);
        final EditText pswrd = (EditText) findViewById(R.id.fbpass);

        usname.requestFocus();
        pswrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pswrd.requestFocus();
            }
        });*/

        RadioGroup emailPhone = (RadioGroup) findViewById(R.id.emailphone);

        emailPhone.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if(checkedId == R.id.emailRadio)
                {
                    EditText usname = (EditText) findViewById(R.id.username);
                    EditText pswrd = (EditText) findViewById(R.id.userpassword);
                    pswrd.setVisibility(View.VISIBLE);

                    usname.setHint("email");
                    pswrd.setHint("password");
                    usname.setText("");
                    usname.getText().clear();
                    pswrd.setText("");

                    usname.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    pswrd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    Button verifyButton = (Button) findViewById(R.id.verifyotp);
                    Button submitButton = (Button) findViewById(R.id.submit);
                    verifyButton.setVisibility(View.GONE);
                    submitButton.setVisibility(View.VISIBLE);

                }

                else if(checkedId == R.id.phoneRadio) {

                    EditText usname = (EditText) findViewById(R.id.username);
                    EditText pswrd = (EditText) findViewById(R.id.userpassword);
                    pswrd.setVisibility(View.GONE);

                    usname.setHint("Phone no. with country code");
                    pswrd.setHint("OTP");

                    usname.setInputType(InputType.TYPE_CLASS_PHONE);
                    pswrd.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);

                    usname.setText("");
                    pswrd.getText().clear();

                    Button verifyButton = (Button) findViewById(R.id.verifyotp);
                    Button submitButton = (Button) findViewById(R.id.submit);
                    verifyButton.setVisibility(View.GONE);
                    submitButton.setVisibility(View.VISIBLE);
                }
            }
        });


        mCallbackManager = CallbackManager.Factory.create();

        fbLogIn = (Button) findViewById(R.id.fbButton);
        pb = (ProgressBar) findViewById(R.id.loginPB);

        fbLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //fbLogIn.setEnabled(false);
                pb.setVisibility(View.VISIBLE);
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email","public_profile","user_friends"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //Toast.makeText(LoginActivity.this,"Login through FaceBook successfull",Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        pb.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this,"Login through FaceBook cancelled",Toast.LENGTH_SHORT).show();
                        //fbLogIn.setEnabled(true);

                    }

                    @Override
                    public void onError(FacebookException error) {
                        pb.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this,error.getMessage().toString(),Toast.LENGTH_SHORT).show();
                       // fbLogIn.setEnabled(true);

                    }
                });
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(LoginActivity.this,"Error while connecting to google",Toast.LENGTH_SHORT).show();

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();


    }
    public void googlelogin(View view)
    {
        signIn();
    }

    private void signIn() {
        pb.setVisibility(View.VISIBLE);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    private void handleFacebookAccessToken(AccessToken token) {
        //Log.d(TAG, "handleFacebookAccessToken:" + token);

        pb = (ProgressBar) findViewById(R.id.loginPB);
        pb.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                           Toast.makeText(LoginActivity.this, "signInWithCredential:success",Toast.LENGTH_SHORT).show();
                           // fbLogIn.setEnabled(true);
                            pb.setVisibility(View.GONE);
                            FirebaseUser user = mAuth.getCurrentUser();

                            userdb = fbdb.getReference().child("users").child(formatEmail(user.getEmail()));
                            userdb.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.exists())
                                    {
                                        userDetail user1 = new userDetail(mAuth.getCurrentUser().getDisplayName(),mAuth.getCurrentUser().getEmail(),"Facebook",mAuth.getCurrentUser().getUid(),mAuth.getCurrentUser().getPhotoUrl().toString());
                                        userdb.push().setValue(user1);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            activityRedirect();
                        } else {
                            pb.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                           // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //fbLogIn.setEnabled(true);
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }

        // Pass the activity result back to the Facebook SDK

        //TODO SEE HOW THIS LINE RELATED TO FB GOT IN GOOGLE
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pb.setVisibility(View.GONE);
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, "signInWithCredential:success",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser() ;

                            userdb = fbdb.getReference().child("users").child(formatEmail(user.getEmail()));
                            userdb.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.exists())
                                    {
                                        userDetail user1 = new userDetail(mAuth.getCurrentUser().getDisplayName(),mAuth.getCurrentUser().getEmail(),"Google",mAuth.getCurrentUser().getUid(),mAuth.getCurrentUser().getPhotoUrl().toString());
                                        userdb.push().setValue(user1);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            activityRedirect();

                        } else {
                            // If sign in fails, display a message to the user.
                            pb.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null)
            activityRedirect();

    }

    public void checkFunc (View view) {

        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }

        RadioGroup emailPhone = (RadioGroup) findViewById(R.id.emailphone);

        int selectedId = emailPhone.getCheckedRadioButtonId();


        if (selectedId == R.id.emailRadio) {

            EditText em = (EditText) findViewById(R.id.username);
            EditText pswrd = (EditText) findViewById(R.id.userpassword);
            pb = (ProgressBar) findViewById(R.id.loginPB);
            final String email = em.getText().toString().trim();
            final String pass = pswrd.getText().toString().trim();
            Button verifyButton = (Button) findViewById(R.id.verifyotp);
            Button submitButton = (Button) findViewById(R.id.submit);
            verifyButton.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);


            if (email.isEmpty()) {
                em.setError("email is required");
                em.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                em.setError("email is invalid");
                em.requestFocus();
                return;
            }

            if (pass.isEmpty()) {
                pswrd.setError("password is required");
                pswrd.requestFocus();
                return;
            }

            if (pass.length() < 6) {
                pswrd.setError("password is short. Atleast 6 chars required");
                pswrd.requestFocus();
                return;
            }

            pb.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    pb.setVisibility(View.GONE);

                    if (task.isSuccessful()) {

                        if (mAuth.getCurrentUser().isEmailVerified()) {

                            FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
                            DatabaseReference userdb = fbdb.getReference().child("users").child(formatEmail(email));
                            userDetail user1 = new userDetail(mAuth.getCurrentUser().getDisplayName(), email, pass, mAuth.getCurrentUser().getUid(),mAuth.getCurrentUser().getPhotoUrl().toString());
                            userdb.push().setValue(user1);
                            activityRedirect();
                        } else
                            redirectVerify(email, pass);

                    } else {
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (selectedId == R.id.phoneRadio) {

            EditText em = (EditText) findViewById(R.id.username);
            EditText pswrd = (EditText) findViewById(R.id.userpassword);
            pb = (ProgressBar) findViewById(R.id.loginPB);
            final String email = em.getText().toString().trim();
            Button verifyButton = (Button) findViewById(R.id.verifyotp);
            Button submitButton = (Button) findViewById(R.id.submit);


            if (email.isEmpty()) {
                em.setError("phone num is required");
                em.requestFocus();
                return;
            }

            if (!Patterns.PHONE.matcher(email).matches()) {
                em.setError("phone is invalid");
                em.requestFocus();
                return;
            } else {
                pswrd.setVisibility(View.VISIBLE);
                verifyButton.setVisibility(View.VISIBLE);
                submitButton.setVisibility(View.INVISIBLE);

           /* if (pass.isEmpty()) {
                pswrd.setError("otp is required");
                pswrd.requestFocus();
                return;
            }

            if (pass.length() < 6) {
                pswrd.setError("otp is short. 6 chars required");
                pswrd.requestFocus();
                return;
            }*/

            pb.setVisibility(View.VISIBLE);
            PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    Toast.makeText(LoginActivity.this, "OTP Successfully verified,", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                    //activityRedirect();
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    pb.setVisibility(View.GONE);

                    if (e instanceof FirebaseAuthInvalidCredentialsException) {

                        // Invalid request

                        // [START_EXCLUDE]

                        Toast.makeText(LoginActivity.this, "Invalid phone number found.", Toast.LENGTH_SHORT).show();

                        // [END_EXCLUDE]

                    } else if (e instanceof FirebaseTooManyRequestsException) {

                        // The SMS quota for the project has been exceeded

                        // [START_EXCLUDE]

                        Toast.makeText(LoginActivity.this, "Quota exceeded.", Toast.LENGTH_SHORT).show();

                        // [END_EXCLUDE]

                    }

                }

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    Toast.makeText(LoginActivity.this, "Code Sent.", Toast.LENGTH_SHORT).show();
                    codeSent = s;
                }
            };


            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    email,
                    120,
                    TimeUnit.SECONDS,
                    LoginActivity.this,
                    mCallbacks);
        }

    }


    }

    public void verifyOtp (View view)
    {
        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }

        EditText pswrd = (EditText) findViewById(R.id.userpassword);
        pb = (ProgressBar) findViewById(R.id.loginPB);

        String otp = pswrd.getText().toString().trim();
        if(otp.isEmpty())
        {
            pswrd.setError("OTP required");
            pswrd.requestFocus();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent,otp);
        signInWithPhoneAuthCredential(credential);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(LoginActivity.this,"Login Successfull",Toast.LENGTH_SHORT).show();
                            //activityRedirect();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(LoginActivity.this,"Invalid OTP entered",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
    public void signupRedirect (View view)
    {
        finish();
        Intent i =new Intent(LoginActivity.this,signupActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void activityRedirect ()
    {
        finish();
        Intent i =new Intent(LoginActivity.this,CombinedActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void redirectVerify (String email, String pass)
    {
        finish();
        Intent i =new Intent(LoginActivity.this,verifyingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("email",email);
        bundle.putString("password",pass);
        i.putExtras(bundle);
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
