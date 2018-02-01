package com.example.asif.chat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView mReqList;

    private DatabaseReference mReqDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);

        mReqList = (RecyclerView) mMainView.findViewById(R.id.conv_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Request").child(mCurrent_user_id);

        mReqDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mReqList.setHasFixedSize(true);
        mReqList.setLayoutManager(linearLayoutManager);


        // Inflate the layout for this fragment
        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Req, RequestsFragment.ReqViewHolder> firebaseReqAdapter = new FirebaseRecyclerAdapter<Req, ReqViewHolder>(
                Req.class,
                R.layout.user_single,
                RequestsFragment.ReqViewHolder.class,
                mReqDatabase
        ) {
            @Override
            protected void populateViewHolder(final ReqViewHolder viewHolder, Req model, int position) {

                final String user_id = getRef(position).getKey();

                mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumbnail").getValue().toString();

                        viewHolder.setDisplayName(userName);
                        viewHolder.setUserImage(userThumb, getContext());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                profileIntent.putExtra("user_id", user_id);
                                startActivity(profileIntent);

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mReqList.setAdapter(firebaseReqAdapter);
    }


    public static class ReqViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ReqViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }
        public void setDisplayName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.User_name);
            userNameView.setText(name);
            TextView userStatusView = (TextView) mView.findViewById(R.id.User_status);
            userStatusView.setVisibility(View.INVISIBLE);
        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.User_image);

            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.picture).into(userImageView);
        }

    }

}
