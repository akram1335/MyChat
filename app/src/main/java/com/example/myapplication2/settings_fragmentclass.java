package com.example.myapplication2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class settings_fragmentclass extends Fragment {


    private View mMainView;
    private TextView logout;


    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mRootRef;


    //Android Layout
    private CircularImageView mDisplayImage;
    private TextView mName;
    public TextView mStatus;
    private TextView mStatusBtn;
    private TextView mImageBtn;
    private TextView change_pass;
    public  TextView mDelete;
    public  TextView mLogout;
    private static final int GALLERY_PICK = 1;
    // Storage Firebase
    private StorageReference mImageStorage;
    private ProgressDialog mProgressDialog;
    private Bitmap thumb_bitmap=null;
    private String image;
    public  String status;





    public settings_fragmentclass() {
        // Required empty public constructor


    }







    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // return inflater.inflate(R.layout.fragment_friends, container, false);


        mMainView = inflater.inflate(R.layout.activity_settinglayout, container, false);



        mProgressDialog=(new ProgressDialog(getContext()));
        logout=(TextView)mMainView.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();


                Intent i =new Intent(getContext(),login.class);
                startActivity(i);

            }
        });
        ////////////////////////


        change_pass=(TextView)mMainView.findViewById(R.id.change_motpass);
        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i =new Intent(getContext(),resetpass.class);
                i.putExtra("previous_activity","chatpage");
                startActivity(i);

            }
        });



        mName = (TextView) mMainView.findViewById(R.id.usernameTextView);
        mStatus = (TextView)  mMainView.findViewById(R.id.statusTextView);
        mStatusBtn = (TextView)  mMainView.findViewById(R.id.change_status);
        mImageBtn = (TextView)  mMainView.findViewById(R.id.change_photo);
        mDisplayImage = (CircularImageView)  mMainView.findViewById(R.id.profileCircleImageView);

        mDelete = (TextView)  mMainView.findViewById(R.id.delete_account);

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
                dialog.setTitle("Are you sure");
                dialog.setMessage("by clicking OK, you won't be able to access to your account, your data will be completely deleted.");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mProgressDialog.setTitle("deleting account");
                        mProgressDialog.setMessage("please wait until finishing the process ");
                        mProgressDialog.setCanceledOnTouchOutside(false);
                        mProgressDialog.show();


                        mRootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
                        mRootRef.child("name").setValue("Deleted User").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                } else {

                                }

                            }
                        });
                        mRootRef.child("online").setValue(ServerValue.TIMESTAMP);
                        mCurrentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    Toast.makeText(getContext(),"Account deleted",Toast.LENGTH_LONG).show();
                                    Intent startIntent =new Intent(getContext(),login.class);
                                    startActivity(startIntent);
                                }else{
                                    Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });

                dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });


        mLogout = (TextView)  mMainView.findViewById(R.id.logout);

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

         mRootRef = FirebaseDatabase.getInstance().getReference();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);


        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                image = dataSnapshot.child("image").getValue().toString();
                status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);

                if(!image.equals("default")) {
                    // Picasso.get().load(image).placeholder(R.drawable.usersetting).into(mDisplayImage);
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.usersetting).into(mDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).placeholder(R.drawable.usersetting).into(mDisplayImage);


                        }


                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mDisplayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fullScreenIntent = new Intent(getContext(), FullScreenImage.class);
                fullScreenIntent.putExtra("image",image);
                startActivity(fullScreenIntent);
            }
        });
        /////////////////////////// dialogue change status
        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExampleDialog exampleDialog = new ExampleDialog();
                exampleDialog.getname(status);
                exampleDialog.show(getFragmentManager(), "example dialog");
            }
        });
        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), test.class);
                startActivity(i);



            }
        });



        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid())
                        .child("online").setValue(ServerValue.TIMESTAMP);
                FirebaseAuth.getInstance().signOut();

                Intent startIntent =new Intent(getContext(),login.class);
                startActivity(startIntent);

            }
        });


        return mMainView;
    }








}