package com.example.administrator.opencvdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ImageButton image_fetch;
    private ImageButton image_process;
    private ImageButton OCR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image_fetch = findViewById(R.id.image_fetch);
        image_process = findViewById(R.id.image_process);
        OCR = findViewById(R.id.OCR);
        image_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this,ImageProcessActivity.class);
                startActivity(intent1);
            }
        });
        image_fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this,ImageFetchActivity.class);
                startActivity(intent2);
            }
        });
        OCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(MainActivity.this,OCRActivity.class);
                startActivity(intent3);
            }
        });
    }
}
