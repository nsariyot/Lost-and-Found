package com.example.ssj_recognized.lostandfound;

/**
 * Created by ssj-recognized on 29/4/19.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.graphics.Typeface.BOLD;

/**
 * Created by ssj-recognized on 19/2/19.
 */

public class RecyclerViewAdapter extends  RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{


    ArrayList<String> username, link, locationlist, objectname, dplink, objectid, alreadyclaimed;
    HashMap<String, String> parents;
    String state, city, name, email, dp;
    ArrayList<String> claimedusers = new ArrayList<>();
    int counter;

    DatabaseReference mydatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uuid = auth.getCurrentUser().getUid();


    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> username, ArrayList<String> link, ArrayList<String> locationlist, ArrayList<String> objectname, ArrayList<String> dplink, ArrayList<String> objectid, String state, String city, HashMap<String, String> parents,ArrayList<String> alreadyclaimed,String name, String email, String dp, int counter, Context context){
        this.username = username;
        this.counter = counter;
        this.link = link;
        this.locationlist = locationlist;
        this.alreadyclaimed = alreadyclaimed;
        this.objectname = objectname;
        this.dplink = dplink;
        this.dp = dp;
        this.objectid = objectid;
        this.name = name;
        this.email = email;
        this.state = state;
        this.city = city;
        this.parents = parents;
        mContext = context;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleronelayout, parent, false);
        ViewHolder holder = new ViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        new DownloadImageTask(holder.image).execute(link.get(position));
        new DownloadImageTask(holder.dp).execute(dplink.get(position));
        holder.usernameText.setText(username.get(position));
        holder.locationText.setText(locationlist.get(position));
        if(alreadyclaimed.contains(objectid.get(position))){
            holder.claim.setClickable(false);
            holder.claim.setText("Already Claimed");
        }else {

            if(alreadyclaimed.isEmpty()&&counter==1){
                holder.claim.setText("Delete");
                holder.claim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String objid = objectid.get(position);
                        mydatabase.child("found").child(parents.get(objid)).child(objid)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        claimedusers = new ArrayList<>(Arrays.asList(
                                                dataSnapshot.child("claimed").getValue().toString().split(",")
                                        ));
                                        for(final String i : claimedusers){
                                            mydatabase.child("details").child(i).addListenerForSingleValueEvent(
                                                    new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot1) {
                                                            String newclaimed = dataSnapshot1.child("claimed").getValue().toString();

                                                            newclaimed.replaceAll(objid,"");
                                                            mydatabase.child("details").child(i).child("claimed").setValue(newclaimed).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    mydatabase.child("found").child(parents.get(objid)).child(objid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            Toast.makeText(mContext, "Post Deleted", Toast.LENGTH_LONG).show();

                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    }
                                            );
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                });

            }else {
                holder.claim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("#######", "ONCLICK CALLED");
                        final String objid = objectid.get(position);
                        mydatabase.child("found").child(parents.get(objid)).child(objid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {

                                    Log.d("%%%%%%%%%%%%%%", dataSnapshot.toString());
                                    String claimed = dataSnapshot.child("claimed").getValue().toString();
                                    String claimedname = dataSnapshot.child("claimedname").getValue().toString();
                                    String claimedemail = dataSnapshot.child("claimedemail").getValue().toString();
                                    String claimeddp = dataSnapshot.child("claimeddp").getValue().toString();
                                    mydatabase.child("found").child(parents.get(objid)).child(objid).child("claimed").setValue(claimed + "," + uuid);
                                    mydatabase.child("found").child(parents.get(objid)).child(objid).child("claimedname").setValue(claimedname + "," + name);
                                    mydatabase.child("found").child(parents.get(objid)).child(objid).child("claimedemail").setValue(claimedemail + "," + email);
                                    mydatabase.child("found").child(parents.get(objid)).child(objid).child("claimeddp").setValue(claimeddp + "," + dp);

                                    Log.d("#CLAIM DONE WITH COMMA ", claimed);

                                    mydatabase.child("details").child(uuid).addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.child("claimed").getValue() == null) {
                                                        mydatabase.child("details").child(uuid)
                                                                .child("claimed").setValue(objectid.get(position));
                                                    } else {
                                                        String previous = dataSnapshot.child("claimed").getValue().toString();
                                                        mydatabase.child("details").child(uuid).child("claimed")
                                                                .setValue(previous + "," + objectid.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(mContext, "Object Claimed", Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            }
                                    );

                                    holder.claim.setText("Claimed");
                                    holder.claim.setClickable(false);

                                } catch (Exception e) {


                                    mydatabase.child("found").child(parents.get(objid)).child(objid).child("claimed").setValue(uuid);
                                    mydatabase.child("found").child(parents.get(objid)).child(objid).child("claimedname").setValue(name);
                                    mydatabase.child("found").child(parents.get(objid)).child(objid).child("claimedemail").setValue(email);
                                    mydatabase.child("found").child(parents.get(objid)).child(objid).child("claimeddp").setValue(dp);
                                    Log.d("#CLAIM DONE ", "WITHOUT COMMA "+e.getMessage());

                                    holder.claim.setText("Claimed");
                                    holder.claim.setClickable(false);
                                    mydatabase.child("details").child(uuid).addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.child("claimed").getValue() == null) {
                                                        mydatabase.child("details").child(uuid)
                                                                .child("claimed").setValue(objectid.get(position));
                                                    } else {
                                                        String previous = dataSnapshot.child("claimed").getValue().toString();
                                                        mydatabase.child("details").child(uuid).child("claimed")
                                                                .setValue(previous + "," + objectid.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(mContext, "Object Claimed", Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            }
                                    );

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });


            }
        }


    }




    @Override
    public int getItemCount() {
        return username.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        CircleImageView dp;
        ImageView image;
        TextView usernameText, locationText;
        Button claim;


        public ViewHolder(View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.dp);
            image = itemView.findViewById(R.id.image);
            usernameText = itemView.findViewById(R.id.username);
            locationText = itemView.findViewById(R.id.location);
            claim = itemView.findViewById(R.id.claim);

            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/montesart.ttf");
            usernameText.setTypeface(typeface, BOLD);
            locationText.setTypeface(typeface);




        }
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}
