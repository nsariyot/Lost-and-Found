package com.example.ssj_recognized.lostandfound;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class upload extends AppCompatActivity {

    ImageView circular, upload;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseStorage storage;
    StorageReference storageReference;
    ProgressBar uploadProgress;

    String download, state2, city2;

    Button uploadbtn;

    EditText description, state, city, location;

    DatabaseReference myDatabase;

    UUID uuid = UUID.randomUUID();

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);


        upload = (ImageView) findViewById(R.id.uploadbtn);
        circular = (ImageView) findViewById(R.id.circular);
        upload = (ImageView) findViewById(R.id.uploadbtn);
        circular = (ImageView) findViewById(R.id.circular);

        description = findViewById(R.id.description);

        state = findViewById(R.id.state);
        city = findViewById(R.id.city);


        location = findViewById(R.id.location);
        uploadbtn = findViewById(R.id.upload);
        uploadProgress = findViewById(R.id.progressBarupload);

        Bundle b = getIntent().getExtras();
        state2 = b.getString("state");
        city2 = b.getString("city");

        state.setText(state2);
        state.setEnabled(false);
        city.setText(city2);
        city.setEnabled(false);


        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String state1 = state.getText().toString();
                String city1 = city.getText().toString();

                String location1 = location.getText().toString();
                String desc = description.getText().toString();
                HashMap<String, String> temp = new HashMap<>();

                temp.put("name", desc);
                temp.put("location", location1);

                temp.put("link", download);
                temp.put("state", state2);
                temp.put("city", city2);
                myDatabase.child("found").child(firebaseAuth.getCurrentUser().getUid().toString())
                        .child(uuid.toString()).setValue(temp).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Image Uploaded.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        myDatabase = FirebaseDatabase.getInstance().getReference();


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
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


            StorageReference ref = storageReference.child("found/" + uuid.toString());
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



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(upload.this, "Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
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
