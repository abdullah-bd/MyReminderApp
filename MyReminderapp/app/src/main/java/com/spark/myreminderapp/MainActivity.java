package com.spark.myreminderapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    SharedPreferences mPrefs;
    Gson gson;
    String alarm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        gson = new Gson();
        lvItems = findViewById(R.id.lvItems);
        items = new ArrayList<>();
        readData();

        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();
        try{
            Intent intent = getIntent();
            String str = intent.getStringExtra("alarmList");
            addTodoItem(str);
        }catch (Exception e){
            System.out.println("lol Exception1");
        }


    }


    public void goToRem(View v) {
        Intent k = new Intent(MainActivity.this, MainSetReminder.class);
        startActivity(k);
    }


    public void addTodoItem(String str) {


        if (str.trim().length() > 0)
            items.add(str);

        itemsAdapter.notifyDataSetChanged();
        updateData();

    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long rowId) {

                Intent intent = new Intent(MainActivity.this, MainSetReminder.class);
                intent.putExtra("index", items.get(position).toString());
                System.out.println("lol"+items.get(position).toString()+"454545");
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                updateData();
                startActivity(intent);

                return true;
            }
        });
    }

    private void updateData() {
        SharedPreferences.Editor mEditor = mPrefs.edit();
        String json = gson.toJson(items);
        mEditor.putString("com.spark.reminderaapp.save", json).commit();
    }

    private void readData() {
        mPrefs = getSharedPreferences("com.spark.reminderaapp.save", 0);
        String arrayString = mPrefs.getString("Data", "");
        if (!arrayString.isEmpty())
            items = gson.fromJson(arrayString, ArrayList.class);
    }
}
