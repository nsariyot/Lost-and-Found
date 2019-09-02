package com.example.ssj_recognized.lostandfound;

/**
 * Created by ssj-recognized on 29/4/19.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.graphics.Typeface.BOLD;

/**
 * Created by ssj-recognized on 19/2/19.
 */

public class RecyclerViewAdapter1 extends  RecyclerView.Adapter<RecyclerViewAdapter1.ViewHolder>{


    ArrayList<String> claimedname, claimedemail, objectname, claimedlink, objectlocation, claimedid;
    HashMap<String, String> parents;
    String state, city;

    DatabaseReference mydatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uuid = auth.getCurrentUser().getUid();


    private Context mContext;

    public RecyclerViewAdapter1(ArrayList<String> claimedname, ArrayList<String> claimedemail, ArrayList<String> objectname, ArrayList<String> claimlink, String state, String city, ArrayList<String>objectlocation, ArrayList<String>claimedid, Context context){
        this.claimedname = claimedname;
        this.claimedemail = claimedemail;
        this.claimedid = claimedid;
        this.objectlocation = objectlocation;
        this.objectname = objectname;
        this.claimedlink = claimlink;
        this.state = state;
        this.city = city;
        mContext = context;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclertwolayout, parent, false);
        ViewHolder holder = new ViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        new DownloadImageTask(holder.dp).execute(claimedlink.get(position));
        holder.username.setText(claimedname.get(position) + "\n("+claimedemail.get(position)+")");
        holder.details.setText("claimed your post "+objectname.get(position)+"\nfound at "+objectlocation.get(position));



        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, UserProfile.class);
                intent.putExtra("id", claimedid.get(position));
                mContext.startActivity(intent);
            }
        });








    }




    @Override
    public int getItemCount() {
        return claimedlink.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        CircleImageView dp;

        TextView username, details;
        RelativeLayout parent;



        public ViewHolder(View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.dp);
            username = itemView.findViewById(R.id.username);
            details = itemView.findViewById(R.id.details);

            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/montesart.ttf");
            username.setTypeface(typeface, BOLD);
            details.setTypeface(typeface);
            parent = itemView.findViewById(R.id.notificationlayout);





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
