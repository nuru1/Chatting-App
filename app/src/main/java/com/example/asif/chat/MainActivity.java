package com.example.asif.chat;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private ViewPager mviewPager;
    private TabsPagerAdapter tabsPagerAdapter;
    private TabLayout mtabLayout;
    private DatabaseReference userdb;

    private DatabaseReference mLocationRef;
    private GoogleApiClient googleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        mtoolbar = (Toolbar)findViewById(R.id.app_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Chat");
        if (mAuth.getCurrentUser() != null) {
            userdb = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }
        mviewPager=(ViewPager)findViewById(R.id.viewPager);
        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        mviewPager.setAdapter(tabsPagerAdapter);
        mtabLayout=(TabLayout)findViewById(R.id.tabLayout);
        mtabLayout.setupWithViewPager(mviewPager);
        userdb = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mLocationRef = FirebaseDatabase.getInstance().getReference().child("Location").child(mAuth.getCurrentUser().getUid());

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            toStart();
        }
        else {
            userdb.child("Online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {

            userdb.child("Online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void toStart() {
        Intent intent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_items,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.lout){
            FirebaseAuth.getInstance().signOut();
            toStart();
        }
        if(item.getItemId()==R.id.settings_account){
            Intent settings = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settings);
        }
        if(item.getItemId()==R.id.all_users){
            Intent all_users= new Intent(MainActivity.this,UsersActivity.class);
            startActivity(all_users);
        }
        return true;
    }

    private void buildAPiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

    }
}
