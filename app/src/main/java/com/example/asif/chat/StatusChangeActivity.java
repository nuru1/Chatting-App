package com.example.asif.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusChangeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText _status;
    private Button save;
    private DatabaseReference db;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_change);

        Intent status = new Intent();
        //String status_val= getIntent().getExtras().getString("new status");
        String status_val=status.getStringExtra("stat");
        if(status.hasExtra("stat"))
        Toast.makeText(getApplication(),"++++   "+status_val,Toast.LENGTH_SHORT).show();
        toolbar=(Toolbar)findViewById(R.id.status_tabBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        _status=(TextInputEditText)findViewById(R.id.newStatus);
        save=(Button)findViewById(R.id.save_changes);
        _status.setText(status_val);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String uid=firebaseUser.getUid();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mStatus=_status.getText().toString();
                if(!TextUtils.isEmpty(mStatus) ){
                progressDialog.setTitle("Saving Changes");
                progressDialog.setMessage("Please wait");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                db= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                db.child("status").setValue(mStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Intent intent = new Intent(StatusChangeActivity.this,SettingsActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            progressDialog.hide();
                            Toast.makeText(getApplicationContext(),"something is wrong",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                }
            }
        });
    }
}
