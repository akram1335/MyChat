package com.example.myapplication2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class login extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button mCreatregButton;
    private TextView gtsign;
    private TextView forget_pass;
    private ProgressDialog mlogindialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        gtsign =(TextView)findViewById(R.id.gotosignup) ;
        forget_pass =(TextView)findViewById(R.id.forgot_password) ;
        mEmail=(EditText)findViewById(R.id.sign_mail);
        mPassword=(EditText)findViewById(R.id.sign_pass);
        mCreatregButton=(Button)findViewById(R.id.sign_button);

        mlogindialog=(new ProgressDialog(this));
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        gtsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openloginactivity();
            }
        });
        forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openResetActivity();
            }
        });
        mCreatregButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Email=mEmail.getText().toString();
                String Password=mPassword.getText().toString();

                if(!TextUtils.isEmpty(Email)|| !TextUtils.isEmpty(Password)){
                    mlogindialog.setTitle("Login");
                            mlogindialog.setMessage("please wait until finishing the process ");
                    mlogindialog.setCanceledOnTouchOutside(false);
                            mlogindialog.show();
                sign_user(Email,Password);}
            }
        });

    }

    private void sign_user(String Email,String Password){
        mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
         @Override
           public void onComplete(@NonNull Task<AuthResult> task) {

             if (task.isSuccessful()) {
                 mlogindialog.dismiss();
                 final String current_user_id = mAuth.getCurrentUser().getUid();

                 FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
                     @Override
                     public void onSuccess(InstanceIdResult instanceIdResult) {
                         String deviceToken = instanceIdResult.getToken();
                         mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void aVoid) {

                                 Intent mainInent = new Intent(login.this, chatpage.class);
                                 mainInent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                 startActivity(mainInent);
                                 finish();


                             }
                         });
                     }});




             } else {
                 mlogindialog.hide();
                 Toast.makeText(login.this, "you can't login, please check your informations", Toast.LENGTH_LONG).show();
             }

             }


        });
    }

    public void openloginactivity() {
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }
    public void openResetActivity() {
        Intent i=new Intent(this,resetpass.class);
        i.putExtra("previous_activity","login");
        startActivity(i);
        finish();
    }

}

