package com.example.myapplication2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class share extends AppCompatActivity {

    private RecyclerView mFriendsList;
    private EditText mEditText;


    private DatabaseReference mFriendsDatabase;/////////////
    private DatabaseReference mUsersDatabase;/////////////////////////
    private DatabaseReference mRootRef;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);


        mFriendsList = (RecyclerView) findViewById(R.id.friends_list2);
        mEditText = (EditText) findViewById(R.id.textgrp);
        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);


        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(this));
        final String messageshare = getIntent().getStringExtra("messageshare");
        mEditText.setText(messageshare);




        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, share.FriendsViewHolder>(

                Friends.class,
                R.layout.user_item_layout2,
                share.FriendsViewHolder.class,
                mFriendsDatabase


        ) {
            @Override
            protected void populateViewHolder(final share.FriendsViewHolder friendsViewHolder, Friends friends, int i) {

                final String list_user_id = getRef(i).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();



                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setUserImage(userThumb, share.this);
                        if(userName.equals("Deleted User")){
                            friendsViewHolder.checkBox2.setVisibility(View.GONE);
                        }else{

                        friendsViewHolder.checkBox2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!mEditText.getText().toString().equals("") ) {
                                    sendMessage(mCurrent_user_id, list_user_id);
                                friendsViewHolder.checkBox2.setText("SENT");
                                friendsViewHolder.checkBox2.setTextColor(getApplication().getResources().getColor(R.color.login_form_details_medium));
                                friendsViewHolder.checkBox2.setBackgroundResource(R.drawable.share2);
                                friendsViewHolder.checkBox2.setClickable(false);
                                Toast.makeText(friendsViewHolder.checkBox2.getContext(), "sended ok", Toast.LENGTH_LONG).show();
                            }
                            }
                        });}

                        friendsViewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {

                                CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};

                                final AlertDialog.Builder builder = new AlertDialog.Builder(share.this);

                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        //Click Event for each item.
                                        if(i == 0){

                                            Intent profileIntent = new Intent(share.this, ProfileActivity.class);
                                            profileIntent.putExtra("user_id", list_user_id);
                                            startActivity(profileIntent);

                                        }

                                        if(i == 1){

                                            Intent chatIntent = new Intent(share.this, the_chat_room.class);///mba3d nbdloha b chatroom
                                            chatIntent.putExtra("user_id", list_user_id);
                                            chatIntent.putExtra("user_name", userName);
                                            startActivity(chatIntent);

                                        }

                                    }
                                });

                                builder.show();

                                return false;
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mFriendsList.setAdapter(friendsRecyclerViewAdapter);


    }



    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        Button checkBox2;
        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            checkBox2=(Button)mView.findViewById(R.id.checkBox2);


        }





        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name2);
            userNameView.setText(name);


        }

        public void setUserImage(final String thumb_image, Context ctx){

            final CircularImageView userImageView = (CircularImageView) mView.findViewById(R.id.user_single_image2);
            //  Picasso.get().load(thumb_image).placeholder(R.drawable.usersetting).into(userImageView);
            Picasso.get().load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.usersetting).into(userImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(thumb_image).placeholder(R.drawable.usersetting).into(userImageView);


                }


            });

        }




    }

    private void sendMessage(String mCurrentUserId,String mChatUser) {


        String message = mEditText.getText().toString();

        if(!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);
            messageMap.put("to", mChatUser);
            messageMap.put("messageId", push_id);


            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);



            mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue(true);
            mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("seen").setValue(false);
            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }

                }
            });

        }

    }
    public void onBackPressed() {
        Intent i = new Intent(this, chatpage.class);
        startActivity(i);
        finish();


    }
}