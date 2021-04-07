package com.example.administrator.opencvdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

public class ImageFetchActivity extends AppCompatActivity {
    private ImageButton URL;
    private ImageButton Camera;
    private ImageButton QRcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fetch);
        URL = findViewById(R.id.URL);
        Camera = findViewById(R.id.Camera);
        QRcode = findViewById(R.id.qrcode);
        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ImageFetchActivity.this,CameraActivity.class);
                startActivity(intent1);
            }
        });
        URL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(ImageFetchActivity.this,URLActivity.class);
                startActivity(intent2);
            }
        });
        QRcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(ImageFetchActivity.this,QRCodeActivity.class);
                startActivity(intent3);
            }
        });
    }
}