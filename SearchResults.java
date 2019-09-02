package com.example.ssj_recognized.lostandfound;

import android.graphics.Typeface;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.Arrays;
import java.util.HashMap;

public class SearchResults extends AppCompatActivity {
    String name, location;


    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    double currentLat, currentLong;
    LocationManager locationManager;
    RelativeLayout loading;
    ArrayList<String> objectname, link, locationlist, userlist, dplink, objectid, alreadyclaimed;
    double count1, count2;

    TextView title, titlepost;
    String state, city, email, dp, name1;
    HashMap<String, String > parentTrack;
    TextView title1;
    ImageView user, camera, notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Bundle b = getIntent().getExtras();

        name = b.getString("name");
        location = b.getString("location");

        state = b.getString("state");
        city = b.getString("city");
        name1 = b.getString("name1");
        email = b.getString("email");
        dp = b.getString("dp");

        loading = findViewById(R.id.loading);
        objectname = new ArrayList<>();
        link = new ArrayList<>();
        locationlist = new ArrayList<>();
        userlist = new ArrayList<>();
        dplink = new ArrayList<>();
        user = findViewById(R.id.user);
        camera = findViewById(R.id.camera);
        title1 = findViewById(R.id.titletext);
        titlepost = findViewById(R.id.titlepost);


        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/montesart.ttf");
        title1.setTypeface(typeface);
        titlepost.setTypeface(typeface);
        alreadyclaimed = new ArrayList<>();
        objectid = new ArrayList<>();
        parentTrack = new HashMap<>();

        databaseReference.child("details").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    alreadyclaimed = new ArrayList<>(
                            Arrays.asList(dataSnapshot.child("claimed").getValue().toString().split(","))
                    );
                }catch (Exception e){
                    alreadyclaimed = new ArrayList<>();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        databaseReference.child("found").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        count2 = 0;
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                            if(!dataSnapshot1.getKey().equals(
                                    FirebaseAuth.getInstance().getCurrentUser().getUid()
                            )){
                                for(DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()){

                                    count1 = dataSnapshot.getChildrenCount()*dataSnapshot1.getChildrenCount();


                                    if(dataSnapshot2.child("name").getValue().toString()
                                            .contains(name)
                                            &&
                                            dataSnapshot2.child("location").getValue().toString()
                                                    .contains(location)){
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
                        }

                        Log.d("#####LINKS", link.toString());
                        Log.d("#####OBJECT NAME ", objectname.toString());
                        Log.d("#####Location ", locationlist.toString());
                        if(link.isEmpty()){
                            RelativeLayout notfound = findViewById(R.id.notfound);
                            notfound.setVisibility(View.VISIBLE);
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

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(userlist, link, locationlist, objectname, dplink, objectid, state, city, parentTrack, alreadyclaimed, name1, email,dp,0, getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchResults.this));

    }
}
