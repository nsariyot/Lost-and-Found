package com.example.ssj_recognized.lostandfound;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.icu.text.LocaleDisplayNames;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;


public class UpdateUserActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    ImageView uploadbtn, circular;
    String download;
    Button logout;
    ProgressBar uploadProgress;
    TextView name, email, seeposts;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference myDatabase;
    UUID uuid = UUID.randomUUID();

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        uploadbtn = findViewById(R.id.uploadbtn);
        uploadProgress = findViewById(R.id.progressBarupload);
        circular = findViewById(R.id.circular);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/montesart.ttf");
        name.setTypeface(typeface);
        email.setTypeface(typeface);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(),SplashScreen.class);
                startActivity(intent);
                finish();
            }
        });

        seeposts = findViewById(R.id.seeposts);


        seeposts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserPosts.class);
                startActivity(intent);
            }
        });




        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        myDatabase = FirebaseDatabase.getInstance().getReference();

        myDatabase.child("details").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        name.setText(dataSnapshot.child("name").getValue().toString());
                        email.setText(dataSnapshot.child("email").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                uploadProgress.setVisibility(View.VISIBLE);
                uploadImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if (filePath != null) {


            StorageReference ref = storageReference.child("dp/" + uuid.toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                                circular.setImageBitmap(bitmap);
                                uploadProgress.setVisibility(View.GONE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            download = taskSnapshot.getDownloadUrl().toString();

                            myDatabase.child("details").child(
                                    FirebaseAuth.getInstance()
                                            .getCurrentUser().getUid().toString()).child("dp").setValue(download).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), "Profile Picture Changed Successfully", Toast.LENGTH_LONG).show();
                                }
                            });



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(UpdateUserActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());

                        }
                    });
        }
    }
}
