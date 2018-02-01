package com.example.asif.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseUser current_User;
    private DatabaseReference databaseReference;
    private CircleImageView mdisplayImage;
    private TextView mDisplayName, mDisplayStatus;
    private Button mchange_photo, mchange_status;
    private static final int GALLERY_PICK = 1;
    private ProgressDialog progressDialog;
    StorageReference storageReference;
    private Bitmap thumb_bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mdisplayImage = (CircleImageView) findViewById(R.id.circleImageView);
        mDisplayName = (TextView) findViewById(R.id.settings_name);
        mDisplayStatus = (TextView) findViewById(R.id.settings_status);
        mchange_photo = (Button) findViewById(R.id.change_photo);
        mchange_status = (Button) findViewById(R.id.change_status);

        current_User = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_User.getUid();
        //Toast.makeText(SettingsActivity.this,uid,Toast.LENGTH_SHORT).show();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        storageReference = FirebaseStorage.getInstance().getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String thumb = dataSnapshot.child("thumbnail").getValue().toString();

                mDisplayName.setText(name);
                mDisplayStatus.setText(status);
                if(!image.equals("Default"))
                Picasso.with(SettingsActivity.this).load(thumb).placeholder(R.drawable.picture).into(mdisplayImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mchange_status.setOnClickListener(this);
        mchange_photo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.change_status) {
            String val = mDisplayStatus.getText().toString();

            Intent status = new Intent(SettingsActivity.this, StatusChangeActivity.class);
            status.putExtra("stat", val);

            Toast.makeText(getApplicationContext(), "++  " + val, Toast.LENGTH_SHORT).show();

            startActivity(status);
        }
        if (view.getId() == R.id.change_photo) {

            Intent img = new Intent();
            img.setType("image/*");
            img.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(img, "Select Image"), GALLERY_PICK);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK || requestCode == RESULT_OK) {
            try {
                Uri imageuri = data.getData();
                CropImage.activity(imageuri)
                        .setAspectRatio(1, 1)
                        .setMinCropWindowSize(500, 500)
                        .start(this);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            progressDialog= new ProgressDialog(this);
            progressDialog.setTitle("Uploading Image");
            progressDialog.setMessage("please wait");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                File thumb_filepath=new File(resultUri.getPath());
                try {
                     thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filepath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_data = baos.toByteArray();

                String uid = current_User.getUid();
                StorageReference filepath= storageReference.child("profile images").child(uid+".jpg");

                final StorageReference thumbnail_filepath=storageReference.child("profile images").child("thumbs").child(uid+".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            final String downloadUri = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumbnail_filepath.putBytes(thumb_data);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    if(thumb_task.isSuccessful()){
                                        String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                        Map imageMap = new HashMap<>();
                                        imageMap.put("image",downloadUri);
                                        imageMap.put("thumbnail",thumb_downloadUrl);

                                        databaseReference.updateChildren(imageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(),"Upload Sucess!!",Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Thumb Upload Failed!!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                     }
                                }
                            });



                            //Toast.makeText(getApplicationContext(), "Uploaded!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Upload Failed!!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}