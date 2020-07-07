package com.andres.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;
    List<String> items;

    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);

        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener(){
            //we need to override the method on itemLongClick
            @Override
            public void onItemLongClicked(int position) {
                //we now have the exact posiiton of where the long pressed happened.
                //so we delete the item from the model
                items.remove(position);
                //notify adapter at which position we deleted the item
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                 //create the new activity. We'll do this with Intents. They're like requests to the Android system. In our case, the request is to open another activity defined in our app.
                //you can have many different intents like opening a URL, or open up a camera.
                //first is the content from where we are calling this intent, and the second arg is the destination, where we want to go.
                //MainActivity.this is referring to the current instance of MainActivity which is this class that we're in right now.
                //EditActivity.class is referring to the class of the EditiActivity. There is no instance yet, we just indicate that this is the class that we want to go to. The system will take care of creating the instance of EditiActivity for us.
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                //pass the data being edited
                //putExtra() takes two parameters, a key which is always a string, and a value
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                //display the activity
                //we use this because we expect a result, namely, the upodated todoItem which we expect back from EditActivity
                //first param is the intent, second is the request code. The code uniquely identifies the request for a different activity. right now, we only have one intent for the whole application, but if we had multiple intents, we would need to distinguish between different intents.
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        //we set the adpater on the recylcer view
        rvItems.setAdapter(itemsAdapter);
        //set layout manager on our rvItems. we will use the most basic layour manager which is linear layout and by default, this will put things in the UI in a vertical way.
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getText() returns a type called Editable, so we will change it to String by using toString()
                String todoItem = etItem.getText().toString();
                //add item to the model;
                items.add(todoItem);
                //notify adapter that an item is inserted
                itemsAdapter.notifyItemInserted(items.size() -1);
                //clear the text once we have submitted the todoItem
                etItem.setText("");
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    //handle the result of the EditActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            //Retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            //extract the original position of the edited item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            //update the model at the right position with new item text
            items.set(position, itemText);
            //notify the adapter
            itemsAdapter.notifyItemChanged(position);
            //persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    //This section that we are implementing is for persistence. This means that if you have 3 items and you remove 2 items and kill the app, when you open it again, it should only show one item, as opposed to the 3 that you already had because it saves the state of your app
    private File getDataFile(){
        //this method returns a file. The first paramter is the dir of this app, and second is the name of the file.
        return new File(getFilesDir(), "data.txt");
    }

    //this function will load items by reading every line of the data file
    //this will only be called once, which is when the app starts.
    private void loadItems(){
        //we are goign to use the Apache commons library
        //what this does it read the data from the file that's returned from getDataFile() and populate that into an arraylist
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }
    }

    //this function saves items by writing items into the data file
    //this will be called every time we add or remove an item from the todoList
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }
}