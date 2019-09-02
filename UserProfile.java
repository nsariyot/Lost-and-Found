package com.example.ssj_recognized.lostandfound;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.graphics.Typeface.BOLD;

public class UserProfile extends AppCompatActivity {
    String id;

    TextView name, email, seeposts;
    CircleImageView dp;
    ProgressBar progressBar;


    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        dp = findViewById(R.id.circular);
        progressBar = findViewById(R.id.progressBarupload);
        progressBar.setVisibility(View.VISIBLE);


        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/montesart.ttf");
        name.setTypeface(typeface, BOLD);
        email.setTypeface(typeface);

        Bundle b = getIntent().getExtras();

        id = b.getString("id");

        databaseReference.child("details").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                name.setText(dataSnapshot.child("name").getValue().toString());
                email.setText(dataSnapshot.child("email").getValue().toString());
                new DownloadImageTask(dp).execute(
                        dataSnapshot.child("dp").getValue().toString()
                );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
            progressBar.setVisibility(View.GONE);
        }
    }
}
