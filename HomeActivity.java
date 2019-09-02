package com.example.ssj_recognized.lostandfound;

import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.support.v7.widget.Toolbar;
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
import java.util.List;
import java.util.Locale;



public class HomeActivity extends AppCompatActivity implements LocationListener{

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    String state, city, name, email, dp;
    double currentLat, currentLong;
    LocationManager locationManager;
    RelativeLayout loading;
    ArrayList<String> objectname, link, locationlist, userlist, dplink, objectid, alreadyclaimed;
    double count1, count2;
    Toolbar title;
    HashMap<String, String > parentTrack;
    TextView title1;
    ImageView user, camera, notification, search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        loading = findViewById(R.id.loading);
        objectname = new ArrayList<>();
        link = new ArrayList<>();
        locationlist = new ArrayList<>();
        userlist = new ArrayList<>();
        dplink = new ArrayList<>();
        user = findViewById(R.id.user);
        camera = findViewById(R.id.camera);
        title1 = findViewById(R.id.titletext);
        objectid = new ArrayList<>();
        parentTrack = new HashMap<>();
        search = findViewById(R.id.search);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),SearchObject.class);
                intent.putExtra("state", state);
                intent.putExtra("city", city);
                intent.putExtra("email", email);
                intent.putExtra("dp", dp);
                intent.putExtra("name1", name);
                startActivity(intent);

            }
        });

        notification = findViewById(R.id.notification);

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Notifications.class);
                intent.putExtra("state", state);
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/montesart.ttf");
        title1.setTypeface(typeface);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), upload.class);
                intent.putExtra("state", state);
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

        databaseReference.child("details").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                email = dataSnapshot.child("email").getValue().toString();
                dp = dataSnapshot.child("dp").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UpdateUserActivity.class);
                startActivity(intent);
            }
        });





        try {

            locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(final Location location) {

        currentLat = location.getLatitude();
        currentLong = location.getLongitude();



        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            state = addresses.get(0).getAdminArea();
            city = addresses.get(0).getSubAdminArea();
            Log.d("#######", state+" "+city);

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


                                        if(dataSnapshot2.child("state").getValue().toString()
                                                .equals(state)
                                                &&
                                                dataSnapshot2.child("city").getValue().toString()
                                                .equals(city)){
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
            loading.setVisibility(View.GONE);





        }catch(Exception e)
        {



        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    public void setRecycler(){
        RelativeLayout notfound = findViewById(R.id.notfound);
        notfound.setVisibility(View.GONE);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler1);

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(userlist, link, locationlist, objectname, dplink, objectid, state, city, parentTrack, alreadyclaimed, name, email,dp,0, getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));

    }
}
