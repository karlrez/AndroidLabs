package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.text.CollationElementIterator;

public class ProfileActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String ACTIVITY_NAME = "PROFILE_ACTIVITY";
    ImageButton mImageButton = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //click listener for ImageButton
        mImageButton = findViewById(R.id.picButton);
        mImageButton.setOnClickListener( click -> dispatchTakePictureIntent());

        //to auto-fill the email field
        Intent fromMain = getIntent();
        EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
        emailEditText.setText(fromMain.getStringExtra("EMAIL"));

        //go to chat button listener
        Button chatBtn = findViewById(R.id.goto_chat);
        Intent goToChat = new Intent(this, ChatRoomActivity.class);
        chatBtn.setOnClickListener( click -> startActivity(goToChat));

        //go to weather button listener
        Button weatherBtn = findViewById(R.id.goto_weather);
        Intent goToWeather = new Intent(this, WeatherForecast.class);
        weatherBtn.setOnClickListener( click -> startActivity(goToWeather));

        //go to toolbar activity
        Button toolbarBtn = findViewById(R.id.goto_toolbar);
        Intent goToToolbar = new Intent(this, TestToolbar.class);
        toolbarBtn.setOnClickListener( click -> startActivityForResult(goToToolbar, 1));
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // result will == OK is checking u pressed the Accept btn or checkmark
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageButton.setImageBitmap(imageBitmap);
        }
        Log.e(ACTIVITY_NAME, "In function:" + "onActivityResult()");

        // when coming back from TestToolbar Activity
        if (resultCode == 500) {
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(ACTIVITY_NAME, "In function:" + "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(ACTIVITY_NAME, "In function:" + "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(ACTIVITY_NAME, "In function:" + "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(ACTIVITY_NAME, "In function:" + "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(ACTIVITY_NAME, "In function:" + "onDestroy()");
    }

}
