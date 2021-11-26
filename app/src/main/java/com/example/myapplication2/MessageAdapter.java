package com.example.myapplication2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private DatabaseReference  mCurrentUserDatabase;

    private FirebaseAuth mAuth;
    private String iimmaaggee;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mRootRef;

    public String CurrentUserName;


    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;


    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item_reciever_layout ,parent, false);


        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircularImageView profileImage;
        public TextView displayName;

        public TextView messageTime;

        public TextView messageText1;
        public CircularImageView profileImage1;
        public ImageView messageImage;
        public ImageView messageImage1;
        public TextView messageTime1;
        public TextView displayName1;

        private  RelativeLayout mLeftArrow;
        private  RelativeLayout mRightArrow;
        public Context context;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircularImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);

            messageText1 = (TextView) view.findViewById(R.id.message_text_layout1);
            profileImage1 = (CircularImageView) view.findViewById(R.id.message_profile_layout1);
            displayName1 = (TextView) view.findViewById(R.id.name_text_layout1);
            messageImage1 = (ImageView) view.findViewById(R.id.message_image_layout1);
            mLeftArrow = (RelativeLayout) itemView.findViewById(R.id.left_arrow);
            mRightArrow = (RelativeLayout) itemView.findViewById(R.id.right_arrow);
            messageTime= (TextView) view.findViewById(R.id.time_text_layout);
            messageTime1= (TextView) view.findViewById(R.id.time_text_layout1);




        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {
        final String arrow;
        mAuth=FirebaseAuth.getInstance();
        final String current_user_id=mAuth.getCurrentUser().getUid();



        final Messages c = mMessageList.get(i);

        final String from_user = c.getFrom();
        String message_type = c.getType();
        c.setSeen(true);

        if(from_user.equals((current_user_id))){


            viewHolder.mRightArrow.setVisibility(View.VISIBLE);
            viewHolder.mLeftArrow.setVisibility(View.INVISIBLE);
            arrow="right";




        }else {
            viewHolder.mRightArrow.setVisibility(View.INVISIBLE);
            viewHolder.mLeftArrow.setVisibility(View.VISIBLE);
            arrow = "left";
        }


        mCurrentUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mCurrentUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CurrentUserName = dataSnapshot.child("name").getValue().toString();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("thumb_image").getValue().toString();

                viewHolder.displayName.setText(name);

                if(arrow.equals("left")) {

                    Picasso.get().load(image).resize(1000, 1000).centerInside().networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.usersetting).into(viewHolder.profileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).resize(1000, 1000).centerInside()
                                    .placeholder(R.drawable.usersetting).into(viewHolder.profileImage);


                        }


                    });

                }else{

                    Picasso.get().load(image).resize(1000, 1000).centerInside().networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.usersetting).into(viewHolder.profileImage1, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).resize(1000, 1000).centerInside()
                                    .placeholder(R.drawable.usersetting).into(viewHolder.profileImage1);


                        }


                    });
                }

                // }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")) {
            Date date = new Date();
            date.setTime(c.getTime());
            String formattedDate = new SimpleDateFormat("MMM,dd 'AT' HH:mm").format(date);




            if(arrow.equals("left")) {


                viewHolder.messageText.setText(c.getMessage());

                viewHolder.messageTime.setText(formattedDate);
                viewHolder.messageImage.setVisibility(View.GONE);

                viewHolder.messageText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast.makeText(viewHolder.messageText.getContext(),"delete test left",Toast.LENGTH_LONG).show();

                        delete_message(viewHolder.messageText,c);
                        return false;
                    }
                });



            }else{


                   viewHolder.messageText1.setText(c.getMessage());

                viewHolder.messageTime1.setText(formattedDate);
                viewHolder.messageImage1.setVisibility(View.GONE);
                viewHolder.messageText1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        delete_message(viewHolder.messageText1,c);
                        Toast.makeText(viewHolder.messageText.getContext(),"delete test right",Toast.LENGTH_LONG).show();

                        return false;

                    }
                });


                // }



            }

        } else if(message_type.equals("image")){

            {
                if (arrow.equals("left")) {

                    viewHolder.messageText.setVisibility(View.GONE);
                    viewHolder.messageImage.setVisibility(View.VISIBLE);
                    Picasso.get().load(c.getMessage()).resize(1000, 1000).centerInside().networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.infi).into(viewHolder.messageImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(c.getMessage()).resize(1000, 1000).centerInside().placeholder(R.drawable.infi).into(viewHolder.messageImage);


                        }


                    });
                    viewHolder.messageImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent fullScreenIntent;
                            fullScreenIntent = new Intent(viewHolder.messageImage.getContext(), FullScreenImage.class);
                            fullScreenIntent.putExtra("image",c.getMessage());
                            viewHolder.messageImage.getContext().startActivity(fullScreenIntent);

                        }
                    });


                } else if (arrow.equals("right")) {
                    viewHolder.messageText1.setVisibility(View.GONE);
                    viewHolder.messageImage1.setVisibility(View.VISIBLE);
                    Picasso.get().load(c.getMessage()).resize(1000, 1000).centerInside().networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.infi).into(viewHolder.messageImage1, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(c.getMessage()).resize(1000, 1000).centerInside().placeholder(R.drawable.infi).into(viewHolder.messageImage1);


                        }


                    });
                    viewHolder.messageImage1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent fullScreenIntent;
                            fullScreenIntent = new Intent(viewHolder.messageImage1.getContext(), FullScreenImage.class);
                            fullScreenIntent.putExtra("image",c.getMessage());
                            viewHolder.messageImage1.getContext().startActivity(fullScreenIntent);

                        }
                    });



                }


                }

            }







        }


    @Override
    public int getItemCount() {
        return mMessageList.size();
    }





    public void delete_message(final TextView context, final Messages c){

        CharSequence options[] = new CharSequence[]{"Delete Message", "Share Message"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(context.getContext());

        builder.setTitle("Select Options");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i == 0){
                    final DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
                    final String old_message=c.getMessage();
                    rootRef.child("messages")
                            .child(c.getFrom())
                            .child(c.getTo())
                            .child(c.getMessageId())
                            .child("deleted message")
                            .setValue(old_message);
                    rootRef.child("messages")
                            .child(c.getFrom())
                            .child(c.getTo())
                            .child(c.getMessageId())
                            .child("message")
                            .setValue(CurrentUserName+" has deleted this message").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                rootRef.child("messages")
                                        .child(c.getTo())
                                        .child(c.getFrom())
                                        .child(c.getMessageId())
                                        .child("message")
                                        .setValue(CurrentUserName+" has deleted this message");
                                rootRef.child("messages")
                                        .child(c.getTo())
                                        .child(c.getFrom())
                                        .child(c.getMessageId())
                                        .child("deleted message")
                                        .setValue(old_message);
                            }
                        }
                    });
                    context.setText(CurrentUserName+" has deleted this message");

                    Toast.makeText(context.getContext(),"YAHOO !!",Toast.LENGTH_LONG).show();



                }

                if(i == 1){

                    ////////////////////////////////////////////////////////////////////////////////////////////

                    Intent profileIntent = new Intent(context.getContext(), share.class);
                    profileIntent.putExtra("messageshare", c.getMessage());
                    context.getContext().startActivity(profileIntent);




                    /////////////////////////////////////////////////////////////////////////////////////////////////
                    Toast.makeText(context.getContext(),"share",Toast.LENGTH_LONG).show();


                }

            }
        });

        builder.show();

    }

}

