package com.example.administrator.opencvdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class ImageProcessActivity extends AppCompatActivity {
    private ImageButton togray;
    private ImageButton beautyFace;
    private ImageButton filter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_process);
        togray = findViewById(R.id.Gray);
        beautyFace = findViewById(R.id.BeautyFace);
        filter = findViewById(R.id.Filter);
        togray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ImageProcessActivity.this,ToGrayActivity.class);
                startActivity(intent1);
            }
        });
        beautyFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(ImageProcessActivity.this,TestActivity.class);
                startActivity(intent2);
            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(ImageProcessActivity.this,FilterActivity.class);
                startActivity(intent3);
            }
        });
    }
}
