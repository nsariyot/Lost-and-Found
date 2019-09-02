package com.example.ssj_recognized.lostandfound;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class Notifications extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uid = auth.getCurrentUser().getUid();
    ArrayList<String> objectlocation, finalclaimid, claimedid, claimemail, claimname, claimlink, objectname;
    String state, city;
    int i2;
    int count = 0;
    HashMap<String, String> trackername, trackeremail;

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        objectlocation = new ArrayList<>();
        claimedid = new ArrayList<>();
        finalclaimid = new ArrayList<>();
        claimemail = new ArrayList<>();
        claimlink = new ArrayList<>();
        objectname = new ArrayList<>();
        claimname = new ArrayList<>();
        trackername = new HashMap<>();
        trackeremail = new HashMap<>();

        title = findViewById(R.id.titletext);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/montesart.ttf");
        title.setTypeface(typeface);

        Bundle b = getIntent().getExtras();

        state = b.getString("state");
        city  = b.getString("city");

        databaseReference.child("found").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            try {
                                String[] claimed = dataSnapshot1.child("claimed").getValue().toString().split(",");
                                String[] claimedname = dataSnapshot1.child("claimedname").getValue().toString().split(",");
                                String[] claimedemail = dataSnapshot1.child("claimedemail").getValue().toString().split(",");
                                String[] claimeddp = dataSnapshot1.child("claimeddp").getValue().toString().split(",");

                                for(int i=0; i<claimed.length;i++){
                                    claimedid.add(claimed[i]);
                                    claimname.add(claimedname[i]);
                                    claimemail.add(claimedemail[i]);
                                    claimlink.add(claimeddp[i]);
                                    objectlocation.add(dataSnapshot1.child("location").getValue().toString());
                                    objectname.add(dataSnapshot1.child("name").getValue().toString());
                                }

                                Log.d("####CLAIMED IDS ", claimedid.toString());

                                Log.d("######LOCATION ", objectlocation.toString());

                            }catch (Exception e){

                            }

                        }

                        setRecycler();



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



    public void setRecycler(){





        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.notifications);

        RecyclerViewAdapter1 adapter = new RecyclerViewAdapter1(claimname, claimemail, objectname, claimlink, state, city, objectlocation, claimedid, getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(Notifications.this));

    }
}
