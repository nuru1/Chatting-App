package com.example.asif.chat;

import android.app.ProgressDialog;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.internal.operators.parallel.ParallelRunOn;

public class ProfileActivity extends AppCompatActivity {

    TextView mdisplayName ;
    private ImageView mProfileImage;
    private TextView  mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn, mDeclineBtn;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private static int mCurrent_state;
    private DatabaseReference mFriendRequestdb;
    private DatabaseReference mFriendsdb;
    private DatabaseReference mRootRef;
    private DatabaseReference mNotificationsdb;
    private FirebaseUser mCurrentUser;

    /* 0 = Not Friends state - Send Friend request
       1 = Request Sent - Cancel Friend request
       2 = Request recieved - Accept or Decline Friend request
       3 = Friends state - Unfriend
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id = getIntent().getStringExtra("user_id");

        mdisplayName = (TextView)findViewById(R.id.profile_displayName);
        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = (Button) findViewById(R.id.profile_send_req_btn);
        mDeclineBtn = (Button) findViewById(R.id.profile_decline_btn);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);;
        mFriendRequestdb=FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        mFriendsdb = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationsdb = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        mCurrent_state=0;

        mDeclineBtn.setVisibility(View.INVISIBLE);
        mDeclineBtn.setEnabled(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading.....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Log.d("P A"," -------------------------uid-"+user_id);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mdisplayName.setText(name);
                mProfileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.picture).into(mProfileImage);

                if(mCurrentUser.getUid().equals(user_id)){

                    mDeclineBtn.setEnabled(false);
                    mDeclineBtn.setVisibility(View.INVISIBLE);

                    mProfileSendReqBtn.setEnabled(false);
                    mProfileSendReqBtn.setVisibility(View.INVISIBLE);

                }

                mFriendRequestdb.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)) {

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            Log.d("Friends", "condition--------------------------");

                            if (req_type.equals("recieved")) {

                                Log.d("Friends", "Req rcvd--------------------------");

                                mCurrent_state = 2;
                                mProfileSendReqBtn.setText("Accept Friend Request");

                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mDeclineBtn.setEnabled(true);
                            } else if (req_type.equals("sent")) {
                                Log.d("Friends", "Req sent--------------------------");

                                mCurrent_state = 1;
                                mProfileSendReqBtn.setText("Cancel Friend Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);
                            }
                            progressDialog.dismiss();

                        }
                        else {
                            Log.d("Friends","In Else --------------------------");
                            mFriendsdb.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_id)){
                                        mCurrent_state = 3;
                                        mProfileSendReqBtn.setText("Unfriend");

                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);
                                    }
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    progressDialog.dismiss();

                                }
                            });
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();

                    }
                });

                Log.d("Friends","OUT!!!--------------------------");

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProfileSendReqBtn.setEnabled(false);

                //-----------------------------Send Friend Request ---------------------------

                if(mCurrent_state==0){

                    mFriendRequestdb.child(mCurrentUser.getUid()).child(user_id).child("request_type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mFriendRequestdb.child(user_id).child(mCurrentUser.getUid()).child("request_type").setValue("recieved")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        HashMap<String,String> NotificationData = new HashMap<>();
                                                        NotificationData.put("from",mCurrentUser.getUid());
                                                        NotificationData.put("type","request");
                                                        mNotificationsdb.child(user_id).push()
                                                                .setValue(NotificationData)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {

                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                                mCurrent_state=1;
                                                                mProfileSendReqBtn.setEnabled(true);
                                                                mProfileSendReqBtn.setText("Cancel Friend Request");
                                                                Toast.makeText(getApplicationContext(),"Friend Request Sent",Toast.LENGTH_SHORT).show();

                                                            }
                                                        });
                                                    }
                                                });
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),"Error Sending Friend Request",Toast.LENGTH_SHORT).show();
                                    }
                                }
                    }       );
                }

                //-----------------------------Cancel Friend Request ---------------------------

                if(mCurrent_state==1){

                    mFriendRequestdb.child(mCurrentUser.getUid()).child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendRequestdb.child(user_id).child(mCurrentUser.getUid()).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if(task.isSuccessful()) {
                                                        mCurrent_state = 0;
                                                        mProfileSendReqBtn.setEnabled(true);
                                                        mProfileSendReqBtn.setText("Send Friend Request");

                                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                                        mDeclineBtn.setEnabled(false);

                                                        Toast.makeText(getApplication(), "Friend Request cancelled", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else {
                                                        Toast.makeText(getApplication(), "Error cancelling Friend Request ", Toast.LENGTH_SHORT).show();
                                                    }
                                                    }

                                            });
                                }
                            });
                }

                //----------------------------------- ACCEPT fRIEND REQUEST--------------------------

                if(mCurrent_state==2){
                    final String current_date = DateFormat.getDateTimeInstance().format(new Date());

                    mFriendsdb.child(mCurrentUser.getUid()).child(user_id).child("Friends Since").setValue(current_date)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        mFriendsdb.child(user_id).child(mCurrentUser.getUid()).child("Friends Since").setValue(current_date)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        mFriendRequestdb.child(mCurrentUser.getUid()).child(user_id).removeValue()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        mFriendRequestdb.child(user_id).child(mCurrentUser.getUid()).removeValue()
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        mCurrent_state = 3;
                                                                                        mProfileSendReqBtn.setEnabled(true);
                                                                                        mProfileSendReqBtn.setText("Unfriend");
                                                                                        Toast.makeText(getApplication(), "Friend Request Accepted", Toast.LENGTH_SHORT).show();

                                                                                    }
                                                                                });

                                                                    }
                                                                });


                                                    }
                                                });

                                    }
                                    else {
                                        Toast.makeText(getApplication(), "Error Accepting Friend Request ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }



                //------------------------------------Friends state---------------------------------

                if(mCurrent_state==3){

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id, null);
                    unfriendMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid(), null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if(databaseError == null){

                                mCurrent_state = 0;
                                mProfileSendReqBtn.setText("Send Friend Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();


                            }

                            mProfileSendReqBtn.setEnabled(true);

                        }
                    });

                }


            }
        });
    }
}
