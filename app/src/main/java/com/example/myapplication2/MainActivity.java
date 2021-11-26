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
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText mTheName;
    private EditText mEmail;
    private EditText mPassword;
    private Button mCreatregButton;
    private ImageButton gtlin;
    private ProgressDialog mlogindialog;
    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();



         gtlin =(ImageButton)findViewById(R.id.gotologin) ;
        mTheName=(EditText)findViewById(R.id.reg_name);
        mEmail=(EditText)findViewById(R.id.reg_mail);
        mPassword=(EditText)findViewById(R.id.reg_pass);
        mCreatregButton=(Button)findViewById(R.id.reg_button);
        mlogindialog=(new ProgressDialog(this));

        gtlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openloginactivity();
            }
        });
        mCreatregButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String theName=mTheName.getText().toString();
                String Email=mEmail.getText().toString();
                String Password=mPassword.getText().toString();
                if(!TextUtils.isEmpty(theName) || !TextUtils.isEmpty(Email) || !TextUtils.isEmpty(Password)) {
                    if(theName.equals("Deleted User")){
                        Toast.makeText(MainActivity.this,"username not appropriate",Toast.LENGTH_LONG).show();

                    }else {
                    mlogindialog.setTitle("Sign up");
                    mlogindialog.setMessage("please wait until finishing the process  ");
                    mlogindialog.setCanceledOnTouchOutside(false);
                    mlogindialog.show();
                    register_user(theName, Email, Password);
                }}
            }
        });

    }

    public void openloginactivity() {
        Intent i=new Intent(this,login.class);
        startActivity(i);
        finish();
    }
    public void onBackPressed() {

        openloginactivity();


    }

    private void register_user(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){


                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                           String device_token = instanceIdResult.getToken();
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", display_name);
                            userMap.put("status", "Mr.B User.");
                            userMap.put("image", "default");
                            userMap.put("thumb_image", "default");
                            userMap.put("device_token", device_token);

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        mlogindialog.dismiss();

                                        Intent mainIntent = new Intent(MainActivity.this, chatpage.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();

                                    }

                                }
                            });

                        }
                    });





                } else {

                    mlogindialog.hide();

                    String error = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        error = "Weak Password!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        error = "Invalid Email";
                    } catch (FirebaseAuthUserCollisionException e) {
                        error = "Existing account!";
                    } catch (Exception e) {
                        error = "Cannot Sign in. Please check the internet connection and try again";
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();

                }

            }
        });

    }
}





