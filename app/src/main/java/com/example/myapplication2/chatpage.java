package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class chatpage extends AppCompatActivity {
    //double back press
    private long backPressedTime;
    private Toast backToast;

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatpage);

        mAuth = FirebaseAuth.getInstance();
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("gentle B");


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        if (mAuth.getCurrentUser() != null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new home_fragmentclass()).commit();
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;



                    switch (menuItem.getItemId()) {
                        case R.id.nav_CHAT_home:
                            selectedFragment = new home_fragmentclass();
                            mToolbar.setTitle("Messages");
                            break;
                        case R.id.nav_friends:
                            selectedFragment = new friends_fragmentclass();
                            mToolbar.setTitle("Friends");

                            break;
                        case R.id.nav_request:
                            selectedFragment = new friends_request_fragmentclass();
                            mToolbar.setTitle("Requests");
                            break;
                        case R.id.settings_menu:
                            selectedFragment = new settings_fragmentclass();
                            mToolbar.setTitle("Settings");
                            break;
                        default:
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }};
    /////////////////////////////// Check if user is signed in (non-null) and update UI accordingly.
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser==null){
            gotoStart();
        }
    }
    private void gotoStart() {
        Intent startIntent =new Intent(chatpage.this,splash__screen.class);
        startActivity(startIntent);
        finish();
    }
    //////////////////////////////double back press
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }
////////////////////////


}
