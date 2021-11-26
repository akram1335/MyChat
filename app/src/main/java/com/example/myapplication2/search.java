package com.example.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class search extends AppCompatActivity {

    private ImageButton searchButton;
    private EditText searchText;
    private RecyclerView searchResultList;
    private DatabaseReference allUsersDatabaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        allUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        //allUsersDatabaseRef.keepSynced(true);

        searchResultList=(RecyclerView)findViewById(R.id.search_result);
        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));

        searchButton=(ImageButton)findViewById(R.id.search_btn);
        searchText=(EditText)findViewById(R.id.search_name);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchInpute=searchText.getText().toString();
                SearchPeapleAndFriends(searchInpute);
            }
        });
    }

    /////////////find user
    private void SearchPeapleAndFriends(String searchInpute) {


        Toast.makeText(this,"searching...",Toast.LENGTH_LONG).show();
        Query SearchPeapleAndFriendsqQuery= allUsersDatabaseRef.orderByChild("name")
                .startAt(searchInpute).endAt(searchInpute+"\uf8ff");
        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, UsersViewHolder>(


                Users.class,
                R.layout.user_item_layout,
                search.UsersViewHolder.class,
                SearchPeapleAndFriendsqQuery
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users users, int position) {


                final String user_id = getRef(position).getKey();
                UsersViewHolder.setDisplayName(users.getName());
                UsersViewHolder.setUserStatus(users.getStatus());
                UsersViewHolder.setUserImage(users.getThumb_image(), getApplicationContext());

                UsersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileIntent = new Intent(search.this, ProfileActivity.class);// lzm nbadelhaaaa /////////////
                        profileIntent.putExtra("user_id", user_id);
                        startActivity(profileIntent);

                    }
                });



            }
        };


        searchResultList.setAdapter(firebaseRecyclerAdapter);
    }

//////////////// all users
    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                Users.class,
                R.layout.user_item_layout,
                UsersViewHolder.class,
                allUsersDatabaseRef

        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, Users users, int position) {



                usersViewHolder.setDisplayName(users.getName());
                usersViewHolder.setUserStatus(users.getStatus());
                usersViewHolder.setUserImage(users.getThumb_image(), getApplicationContext());

                final String user_id = getRef(position).getKey();

                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileIntent = new Intent(search.this, ProfileActivity.class);// lzm nbadelhaaaa /////////////
                        profileIntent.putExtra("user_id", user_id);
                        startActivity(profileIntent);

                    }
                });


            }
        };


        searchResultList.setAdapter(firebaseRecyclerAdapter);

    }

    /////////// get infos
    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        static View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public static void setDisplayName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public static void setUserStatus(String status){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);


        }

        public static void setUserImage(final String thumb_image, Context ctx){

            final CircularImageView userImageView = (CircularImageView) mView.findViewById(R.id.user_single_image);

            //Picasso.get().load(thumb_image).placeholder(R.drawable.usersetting).into(userImageView);
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
    public void onBackPressed() {
        Intent i = new Intent(this, chatpage.class);
        startActivity(i);
        finish();


    }

}
