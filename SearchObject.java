package com.example.ssj_recognized.lostandfound;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SearchObject extends AppCompatActivity {

    TextView title, titlepost;
    EditText name, location;
    Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_object);


        search = findViewById(R.id.searchbtn);
        name = findViewById(R.id.name);
        location = findViewById(R.id.location);

        title = findViewById(R.id.titletext);
        titlepost = findViewById(R.id.titlepost);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/montesart.ttf");
        search.setTypeface(typeface);
        name.setTypeface(typeface);
        location.setTypeface(typeface);

        titlepost.setTypeface(typeface);
        title.setTypeface(typeface);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name1 = name.getText().toString();
                String location1 = location.getText().toString();

                if (name1.isEmpty()||location1.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please Provide All Fields", Toast.LENGTH_LONG).show();
                }else {
                    Intent intent = new Intent(getApplicationContext(), SearchResults.class);
                    intent.putExtra("state", getIntent().getExtras().getString("state"));
                    intent.putExtra("city", getIntent().getExtras().getString("city"));
                    intent.putExtra("name", name1);
                    intent.putExtra("location", location1);

                    startActivity(intent);

                }
            }
        });
    }
}
