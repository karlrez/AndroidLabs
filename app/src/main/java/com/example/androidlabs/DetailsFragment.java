package com.example.androidlabs;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    private AppCompatActivity parentActivity;
    private Bundle dataFromActivity;
    private TextView message, id;
    private CheckBox isSend;
    private Button hideBtn;
    private float long_id;
    private String string_message;
    private boolean boolean_isSend = false;


    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflating the fragment_details view but storing result as a variable
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        // Getting data from Bundle created in ChatRoomActivity
        dataFromActivity = getArguments();
        long_id = dataFromActivity.getLong(ChatRoomActivity.ITEM_ID);
        string_message = dataFromActivity.getString(ChatRoomActivity.ITEM_SELECTED);
        boolean_isSend = dataFromActivity.getBoolean(ChatRoomActivity.ITEM_ISSEND);

        // variables for widgets from fragment_details view
        message = view.findViewById(R.id.message_here);
        id = view.findViewById(R.id.id_equals);
        isSend = view.findViewById(R.id.is_send_msg);
        hideBtn = view.findViewById(R.id.hide_btn);

        //setting new values for view
        message.setText(string_message);
        id.setText(String.format("ID= %.0f", long_id)); //removes the decimal from float
        isSend.setChecked(boolean_isSend);

        hideBtn.setOnClickListener( clk -> {
            //Tell the parent activity to remove
            parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
        });

        return view;
    }

    @Override //need this method for hideBtn to work
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }
}
