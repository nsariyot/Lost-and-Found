package com.example.ssj_recognized.lostandfound;

import android.graphics.Typeface;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UserPosts extends AppCompatActivity {

    TextView title, titlepost;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    String state, city, name, email, dp;
    double currentLat, currentLong;
    LocationManager locationManager;
    RelativeLayout loading;
    ArrayList<String> objectname, link, locationlist, userlist, dplink, objectid, alreadyclaimed;
    double count1, count2;

    HashMap<String, String > parentTrack;
    TextView title1;
    ImageView user, camera, notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);
        title = findViewById(R.id.titletext);
        titlepost = findViewById(R.id.titlepost);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/montesart.ttf");
        titlepost.setTypeface(typeface);
        title.setTypeface(typeface);

        loading = findViewById(R.id.loading);
        objectname = new ArrayList<>();
        link = new ArrayList<>();
        locationlist = new ArrayList<>();
        userlist = new ArrayList<>();
        dplink = new ArrayList<>();
        user = findViewById(R.id.user);
        camera = findViewById(R.id.camera);
        title1 = findViewById(R.id.titletext);
        alreadyclaimed = new ArrayList<>();
        objectid = new ArrayList<>();
        parentTrack = new HashMap<>();


        databaseReference.child("found").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        count2 = 0;
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                            if(dataSnapshot1.getKey().equals(
                                    FirebaseAuth.getInstance().getCurrentUser().getUid()
                            )){
                                for(DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()){

                                    count1 = dataSnapshot.getChildrenCount()*dataSnapshot1.getChildrenCount();



                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                        reference.child("details").child(dataSnapshot1.getKey()).addListenerForSingleValueEvent(
                                                new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot3) {
                                                        userlist.add(dataSnapshot3.child("email").getValue().toString());
                                                        dplink.add(dataSnapshot3.child("dp").getValue().toString());
                                                        Log.d("#####USER LIST ", userlist.toString());
                                                        count2++;

                                                        Log.d("##### KEEP TRACK ", count1+" "+ count2);
                                                        if(count2==link.size()){
                                                            setRecycler();
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                }
                                        );
                                        objectid.add(dataSnapshot2.getKey());
                                        parentTrack.put(dataSnapshot2.getKey(), dataSnapshot1.getKey());

                                        link.add(dataSnapshot2.child("link").getValue().toString());
                                        objectname.add(dataSnapshot2.child("name").getValue().toString());
                                        locationlist.add(dataSnapshot2.child("location").getValue().toString());



                                }
                            }
                        }


                        if(link.isEmpty()){
                            RelativeLayout notfound = findViewById(R.id.notfound);
                            notfound.setVisibility(View.VISIBLE);
                            loading.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    public void setRecycler(){
        RelativeLayout notfound = findViewById(R.id.notfound);
        notfound.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler1);

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(userlist, link, locationlist, objectname, dplink, objectid, state, city, parentTrack, alreadyclaimed, name, email,dp,1, getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(UserPosts.this));

    }
}
