package com.example.siddh.talktry;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class aboutUs extends AppCompatActivity {

    private static final int REQUEST_PHONE_CALL = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                else
                {

                }
                return;
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        ImageView call = (ImageView) findViewById(R.id.callLogo);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+919953869239"));
                if (ActivityCompat.checkSelfPermission(aboutUs.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(aboutUs.this, new String[]{android.Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                    return;
                }
                startActivity(intent);
            }
        });



        ImageView mail = (ImageView) findViewById(R.id.mailLogo);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Intent.ACTION_SEND_MULTIPLE);
                it.putExtra(Intent.EXTRA_EMAIL,new String[] {"sidjainsid05@gmail.com"});
                it.putExtra(Intent.EXTRA_SUBJECT, "Feedback TALK chat app");
                it.putExtra(Intent.EXTRA_TEXT, "From! " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                //it.setType("text/plain");
                it.setType("message/rfc822");
                startActivity(it);
            }
        });
    }
}
