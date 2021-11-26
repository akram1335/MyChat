package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class resetpass extends AppCompatActivity {
    private EditText send_mail;
    private Button btn_reset;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpass);
        send_mail=(EditText)findViewById(R.id.enter_reset_mail);
        btn_reset=(Button)findViewById(R.id.reset_mail_button);
        firebaseAuth=FirebaseAuth.getInstance();
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = send_mail.getText().toString();
                if(email.isEmpty()){
                    Toast.makeText(resetpass.this,"the filed is empty",Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(resetpass.this,"we sended you an email ,please check your email",Toast.LENGTH_SHORT).show();

                            }else{
                                String error= task.getException().getMessage();
                                Toast.makeText(resetpass.this,error,Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });
    }
    public void onBackPressed() {
        final String previous_activity = getIntent().getStringExtra("previous_activity");
        if(previous_activity.equals("chatpage")){

            Intent callIntent = new Intent(this,chatpage.class);
            startActivity(callIntent);
        }else{
            Intent callIntent = new Intent(this,login.class);
            startActivity(callIntent);
        }



    }
}
