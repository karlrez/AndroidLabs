package com.example.androidlabs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatRoomActivity extends AppCompatActivity {
    private ArrayList<Message> elements = new ArrayList<>();
    private MyListAdapter myAdapter;
    private Button sendBtn, recieveBtn;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        ListView myList = (ListView) findViewById(R.id.theListView);
        myList.setAdapter( myAdapter = new MyListAdapter());

        sendBtn = findViewById(R.id.sendBtn);
        recieveBtn = findViewById(R.id.recieveBtn);
        editText = findViewById(R.id.chatMessage);

        //send button click handler
        sendBtn.setOnClickListener((click) -> {
            String message = editText.getText().toString();
            if (!message.equals("")) {
                Message addMessage = new Message(message, true);
                elements.add(addMessage);
                myAdapter.notifyDataSetChanged();
                editText.setText("");
            }
        } );

        //recieve button click handler
        recieveBtn.setOnClickListener((click) -> {
            String message = editText.getText().toString();
            if (!message.equals("")) {
                Message addMessage = new Message(message, false);
                elements.add(addMessage);
                myAdapter.notifyDataSetChanged();
                editText.setText("");
            }
        } );

        //action when holding down on a message object
        myList.setOnItemLongClickListener( (p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Do you want to delete this")

                    //What is the message:
                    .setMessage("The selected row is: " + pos +
                            "\nThe database id: " + id)

                    //what the Yes button does:
                    .setPositiveButton("Yes", (click, arg) -> {
                        elements.remove(pos);
                        myAdapter.notifyDataSetChanged();
                    })
                    //What the No button does:
                    .setNegativeButton("No", (click, arg) -> { })

                    //An optional third button:
                    //.setNeutralButton("Maybe", (click, arg) -> {  })

                    //You can add extra layout elements:
                    //.setView(getLayoutInflater().inflate(R.layout.row_layout, null) )

                    //Show the dialog
                    .create().show();
            return true;
        });

    }




    private class MyListAdapter extends BaseAdapter{

        public int getCount() { return elements.size(); }

        public Object getItem(int position) { return elements.get(position); }

        public long getItemId(int position) { return (long) position; }

        public View getView(int position, View old, ViewGroup parent)
        {
            View newView = old;
            LayoutInflater inflater = getLayoutInflater();

            //make a new row:
            if(newView == null) {
                if (elements.get(position).isSend())
                    newView = inflater.inflate(R.layout.send_layout, parent, false);
                else
                    newView = inflater.inflate(R.layout.recieve_layout, parent, false);
            }
            //set what the text should be for this row:
            TextView tView = newView.findViewById(R.id.textGoesHere);
            tView.setText( elements.get(position).getMessage() );

            //return it to be put in the table
            return newView;
        }
    }

    private class Message {
        private String message;
        private boolean send;

        Message(String message, boolean send) {
            this.message = message;
            this.send = send;
        }

        public String getMessage () { return message; }

        public boolean isSend () { return send; }
    }
}
