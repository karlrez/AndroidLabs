package com.example.androidlabs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatRoomActivity extends AppCompatActivity {
    private ArrayList<Message> elements = new ArrayList<>();
    private MyListAdapter myAdapter;
    private Button sendBtn, recieveBtn;
    private EditText editText;
    SQLiteDatabase db;
    public static final String ITEM_SELECTED = "ITEM_SELECTED";
    public static final String ITEM_ISSEND = "ITEM_ISSEND";
    public static final String ITEM_ID = "ITEM_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        //creating an adapter object and sending to listView
        ListView myList = findViewById(R.id.theListView);
        myList.setAdapter(myAdapter = new MyListAdapter());

        //variables for widgets in the view
        sendBtn = findViewById(R.id.sendBtn);
        recieveBtn = findViewById(R.id.recieveBtn);
        editText = findViewById(R.id.chatMessage);

        //load saved messages from db
        loadDataFromDatabase();

        //send button click handler
        sendBtn.setOnClickListener((click) -> {
            String message = editText.getText().toString();
            if (!message.equals("")) {

                //add a new row to the db
                ContentValues newRowValues = new ContentValues();

                //provide a value for the db columns
                newRowValues.put(MyOpener.COL_ISSEND, 1); // 1 for true 0 for false
                newRowValues.put(MyOpener.COL_MESSAGE, message);

                //insert into db (insert method returns a db id)
                long newId = db.insert(MyOpener.TABLE_NAME, null, newRowValues);

                // new creating Message object and adding to our ArrayList
                Message addMessage = new Message(message, true, newId);
                elements.add(addMessage);
                myAdapter.notifyDataSetChanged();
                editText.setText("");
            }
        } );

        //recieve button click handler
        recieveBtn.setOnClickListener((click) -> {
            String message = editText.getText().toString();
            if (!message.equals("")) {

                //add a new row to the db
                ContentValues newRowValues = new ContentValues();

                //provide a value for the db columns
                newRowValues.put(MyOpener.COL_ISSEND, 0);
                newRowValues.put(MyOpener.COL_MESSAGE, message);

                //insert into db which returns id
                long newId = db.insert(MyOpener.TABLE_NAME, null, newRowValues);

                // new creating Message object and adding to our ArrayList
                Message addMessage = new Message(message, false, newId);
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
                        deleteMessage(elements.get(pos));
                        elements.remove(pos);
                        myAdapter.notifyDataSetChanged();
                    })
                    //What the No button does:
                    .setNegativeButton("No", (click, arg) -> { })

                    //Show the dialog
                    .create().show();
            return true;
        });

        //fragment for frame layout
        //FrameLayout frameLayout = findViewById(R.id.fragmentLocation);
        boolean isTablet = findViewById(R.id.fragmentLocation) != null; // !null if using tablet device

        myList.setOnItemClickListener((list, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_SELECTED, elements.get(position).getMessage() ); //getting message from elements
            dataToPass.putBoolean(ITEM_ISSEND, elements.get(position).getIsSend());
            dataToPass.putLong(ITEM_ID, id);

            if(isTablet) {
                DetailsFragment dFragment = new DetailsFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment.
            }
            else { // using Phone device
                Intent nextActivity = new Intent(this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });

        //Whenever you swipe down on the list, do something:
        SwipeRefreshLayout refresher = findViewById(R.id.refresher);
        refresher.setOnRefreshListener( () -> refresher.setRefreshing(false)  );

    }




    private class MyListAdapter extends BaseAdapter{

        public int getCount() { return elements.size(); }

        public Object getItem(int position) { return elements.get(position); }

        public long getItemId(int position) { return elements.get(position).getId(); } //modified to return db id

        public View getView(int position, View old, ViewGroup parent)
        {
            View newView = old;
            LayoutInflater inflater = getLayoutInflater();

            //make a new row:
            if(newView == null) {
                if (elements.get(position).getIsSend())
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

    private void loadDataFromDatabase() {

        //get db connection:
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase();

        //creating array for the db column names
        String [] columns = {MyOpener.COL_ID, MyOpener.COL_MESSAGE, MyOpener.COL_ISSEND};
        //query all the results from the db
        Cursor results = db.query(false,MyOpener.TABLE_NAME,columns,null,null,null,null,null,null);

        //Now the results object has rows of results that match the query.
        //find the column indices:
        int messageColumnIndex = results.getColumnIndex(MyOpener.COL_MESSAGE);
        int isSendColIndex = results.getColumnIndex(MyOpener.COL_ISSEND);
        int idColIndex = results.getColumnIndex(MyOpener.COL_ID);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext()) {
            String message = results.getString(messageColumnIndex);
            boolean isSend = results.getInt(isSendColIndex) ==1 ? true : false;
            long id = results.getLong(idColIndex);

            //add the new Contact to the array list:
            elements.add(new Message(message, isSend, id));
        }
        //calling printCursor to print info to log
        printCursor(results, db.getVersion());
    }

    protected void deleteMessage(Message message) {
        db.delete(MyOpener.TABLE_NAME, MyOpener.COL_ID + "= ?", new String[] {Long.toString(message.getId())});
    }

    protected void printCursor(Cursor c, int version) {
        Log.d("Version ", Integer.toString(version));
        Log.d("Number of Columns ", Integer.toString(c.getColumnCount()));
        Log.d("Column Names: ", Arrays.toString(c.getColumnNames()));
        Log.d("Number of Results: ", Integer.toString(c.getCount()));

        //printing all rows
        c.moveToFirst();
        for (int i=0; i<c.getCount(); i++) {
            Log.d("Results: ", c.getString(0) + " | " +
                    c.getString(1) + " | " +
                    c.getString(2));
            c.moveToNext();
        }
    }

    private class Message {
        private String message;
        private boolean isSend;
        private long Id;

        Message(String message, boolean isSend, long Id) {
            this.message = message;
            this.isSend = isSend;
            this.Id = Id;
        }

        public String getMessage () { return message; }

        public boolean getIsSend () { return isSend; }

        public long getId () { return Id; }
    }
}
