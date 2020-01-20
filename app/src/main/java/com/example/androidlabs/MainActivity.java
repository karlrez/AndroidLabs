package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_relative);

        //bunch of variables
        final CheckBox checkbox = (CheckBox) findViewById(R.id.checkbox); //variable for widget
        String onMessage = getResources().getString(R.string.on_msg);
        String offMessage = getResources().getString(R.string.off_msg);
        String undoMessage = getResources().getString(R.string.snackbar_message_undo);
        String toast_msg = getResources().getString(R.string.Toast_Message);
        String checkboxValue = getResources().getString(R.string.checkboxValue);

        // toast message when checkbox is clicked
        checkbox.setOnClickListener( v -> Toast.makeText(this, toast_msg , Toast.LENGTH_LONG).show());

        // Snackbar message when checkbox is clicked
        // Creating variable for snackbar but changing it based on boolean value given
        checkbox.setOnCheckedChangeListener((CompoundButton cb, boolean b) -> {
            Snackbar checkboxSnackbar = Snackbar.make(checkbox, checkboxValue + onMessage, Snackbar.LENGTH_LONG); // had to initialize it to make error go away
            if(b)
                checkboxSnackbar = Snackbar.make(checkbox, checkboxValue + onMessage, Snackbar.LENGTH_LONG);
            else
                checkboxSnackbar = Snackbar.make(checkbox, checkboxValue + offMessage, Snackbar.LENGTH_LONG);
            checkboxSnackbar.setAction(undoMessage, click -> cb.setChecked(!b)).show();
        });


        // variables needed for Switch Snackbar
        final Switch onOffSwitch = findViewById(R.id.switch1); //variable for switch widget
        String snackbar_msg = getResources().getString(R.string.snackbar_msg);

        // creating listener for Switch
        onOffSwitch.setOnCheckedChangeListener((CompoundButton cb, boolean b) -> {
            Snackbar mySnackbar = Snackbar.make(onOffSwitch, snackbar_msg + onMessage, Snackbar.LENGTH_LONG);
            if(b)
                mySnackbar = Snackbar.make(onOffSwitch, snackbar_msg + onMessage, Snackbar.LENGTH_LONG);
            else
                mySnackbar = Snackbar.make(onOffSwitch, snackbar_msg + offMessage, Snackbar.LENGTH_LONG);
            mySnackbar.setAction(undoMessage, click -> cb.setChecked(!b)).show();
        });
    }
}


