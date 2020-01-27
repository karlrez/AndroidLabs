package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    SharedPreferences prefs = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_lab3);


        prefs = getSharedPreferences("FileName", Context.MODE_PRIVATE);
        String savedString = prefs.getString("ReserveName", "");
        EditText typeField = findViewById(R.id.emailInput);
        typeField.setText(savedString);

        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener( click -> {
            saveSharedPrefs( typeField.getText().toString());

            Intent goToProfile = new Intent(MainActivity.this, ProfileActivity.class);
            goToProfile.putExtra("EMAIL", typeField.getText().toString());
            // requestCode 1 is just random
            startActivity(goToProfile);
        } );


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void saveSharedPrefs(String stringToSave) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ReserveName", stringToSave);
        editor.commit();
    }

}