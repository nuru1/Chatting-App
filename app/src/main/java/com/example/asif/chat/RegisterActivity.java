package com.example.asif.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.drm.ProcessedData;
import android.net.Uri;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.internal.kx;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText Uname,Email,Password;
    private Button Register;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private DatabaseReference db;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        Uname=(TextInputEditText)findViewById(R.id.uname);
        Email=(TextInputEditText)findViewById(R.id.email);
        Password=(TextInputEditText)findViewById(R.id.pass);
        Register=(Button)findViewById(R.id.submit);
        progressDialog = new ProgressDialog(this);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        toolbar = (Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = Uname.getText().toString();
                String mail = Email.getText().toString();
                String pass = Password.getText().toString();

                if(!TextUtils.isEmpty(name) ||!TextUtils.isEmpty(mail) ||!TextUtils.isEmpty(pass)) {
                    progressDialog.setTitle("Creating new account");
                    progressDialog.setMessage("Please Wail wait");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    register_user(name, mail, pass);
                }
            }
        });
    }

    private void register_user(final String name, String mail, String pass) {
        mAuth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    final String uid=current_user.getUid();
                    final String deviceToken= FirebaseInstanceId.getInstance().getToken();

                    db= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String,String> userMap = new HashMap<>();
                    userMap.put("name",name);
                    userMap.put("status","Hi There!!");
                    userMap.put("image","Default");
                    userMap.put("thumbnail", "Default");
                    userMap.put("Device_Token",deviceToken);
                    db.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                        progressDialog.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                        }
                    });


                }
                else {
                    progressDialog.hide();
                    Toast.makeText(getApplication(),"Authentication Failed!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
