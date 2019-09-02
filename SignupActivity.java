package com.example.ssj_recognized.lostandfound;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity implements LocationListener{
    EditText name, email, password, contact;
    Button signup, getLocation;
    double currentLat, currentLong;
    String state, city;
    ProgressBar loading;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

    LocationManager locationManager;

    TextView setLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        contact = findViewById(R.id.contact);

        signup = findViewById(R.id.signupbtn);
        getLocation = findViewById(R.id.getLocation);
        setLocation = findViewById(R.id.location);
        loading = findViewById(R.id.progressBar2);



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(setLocation.getVisibility()== View.VISIBLE){
                    final String name1 = name.getText().toString();
                    final String email1 = email.getText().toString();
                    String password1 = password.getText().toString();
                    final String contact1 = contact.getText().toString();

                    firebaseAuth.createUserWithEmailAndPassword(email1,password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            HashMap<String, String> temp = new HashMap<>();
                            temp.put("name", name1);
                            temp.put("email", email1);
                            temp.put("contact", contact1);
                            temp.put("city", city);
                            temp.put("state", state);
                            firebaseDatabase.child("details").child(firebaseAuth.getCurrentUser().getUid().toString())
                                    .setValue(temp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), "Signup Successful", Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                    });
                }
            }
        });
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);
                signup.setClickable(false);

                getLocation();

            }
        });
    }

    public void getLocation() {
        try {

            locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onLocationChanged(Location location) {
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();



        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            state = addresses.get(0).getAdminArea();
            city = addresses.get(0).getSubAdminArea();

            setLocation.setText("Location : "+city+", "+state);
            setLocation.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            getLocation.setVisibility(View.VISIBLE);
            signup.setClickable(true);

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
}
