package com.spark.myreminderapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;

import android.os.Bundle;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
//import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;


public class MainSetReminder extends AppCompatActivity {

    PlacesClient placesClient;

    DatePicker pickerDate;
    TimePicker pickerTime;
    Button buttonSetAlarm;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    public LinkedList ls;
    String alarm = "";
    String place = "";
    private final int RQS_1 = 1;
    ArrayList al;
    EditText addi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_set_reminder);
        ls = new LinkedList();
        pickerDate = findViewById(R.id.datePicker);
        pickerTime = findViewById(R.id.timePicker);

        addi = findViewById(R.id.additional);

        Calendar now = Calendar.getInstance();

        pickerDate.init(
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH),
                null);

        pickerTime.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
        pickerTime.setCurrentMinute(now.get(Calendar.MINUTE));

        buttonSetAlarm = findViewById(R.id.setBtn);
        buttonSetAlarm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Calendar current = Calendar.getInstance();

                Calendar calender = Calendar.getInstance();
                calender.set(pickerDate.getYear(),
                        pickerDate.getMonth(),
                        pickerDate.getDayOfMonth(),
                        pickerTime.getCurrentHour(),
                        pickerTime.getCurrentMinute(),
                        00);

                if (calender.compareTo(current) <= 0) {
                    //The set Date/Time already passed
                    Toast.makeText(getApplicationContext(),
                            "Invalid Date/Time",
                            Toast.LENGTH_LONG).show();
                } else {
                    setAlarm(calender);
                }

            }
        });


        try {
            Intent intent = getIntent();
            String a = intent.getStringExtra("index");
            System.out.println("lol " + a);
            if (a != null)
                cancel(a);

        } catch (Exception e) {
            System.out.println("lol Exception2");
        }


        try {
            String apiKey = "AIzaSyCKJA3q3c9dKxNkBNSlX2sMAFDrcYuFQHQ";
            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), apiKey);
            }
            placesClient = Places.createClient(this);
            final AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.editText);
            autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));

            autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    final LatLng latLng = place.getLatLng();

                    System.out.println("lol geo!" + latLng.latitude);
                }

                @Override
                public void onError(@NonNull Status status) {

                }
            });
            //String etPlace =  autocompleteSupportFragment.getString(2);
            //System.out.println("lol autocomplete name"+etPlace);

        } catch (Exception E) {
            Toast.makeText(this, "Please Put a API key!", Toast.LENGTH_SHORT).show();
        }




/*
        Button go_to_button=findViewById(R.id.pickbtn);
        go_to_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startplacPlacePickerAct();
            }
        });



*/


    }

/*
    public void startplacPlacePickerAct(){
        PlacePicker.IntentBuilder intentBuilder=new PlacePicker.IntentBuilder();
        try{
            Intent intent = intentBuilder.build(this);
            startActivityForResult(intent,RQS_1);
        }catch (Exception e){

        }
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==RQS_1 && resultCode==RESULT_OK){
            displaySelectedPlacePicker(data);
        }
    }

    private void displaySelectedPlacePicker(Intent data){
        Place placeSelected= (Place) PlacePicker.getPlace(data, this);
        String name=placeSelected.getName().toString();
        String address= placeSelected.getAddress().toString();
        TextView selectedLoc=findViewById(R.id.pickAddress);
        selectedLoc.setText(name+", "+address);
    }



*/


    private void setAlarm(Calendar targetCal) {

        Toast.makeText(this, targetCal.getTime().toString(), Toast.LENGTH_SHORT).show();
        System.out.println("lol alarm is set");
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);


        intent.putExtra("todo", addi.getText().toString());

        System.out.println("lol--> put extra " + addi.getText().toString());


        pendingIntent = PendingIntent.getBroadcast(getBaseContext(), targetCal.get(Calendar.MINUTE), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        //RQS_1++;
        alarm = targetCal.getTime().toString();


        // intent = getIntent();
        //ArrayList items = intent.getStringArrayListExtra("items");


        //items.add(targetCal.getTime().toString());

        update();
    }

    private void update() {
        Intent intent = new Intent(MainSetReminder.this, MainActivity.class);
        intent.putExtra("alarmList", alarm);
        startActivity(intent);
    }


    public void cancel(String s) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        try {
            cal.setTime(sdf.parse(s));
        } catch (Exception e) {
            System.out.println("lol cannot convert time");
        }

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getBaseContext(), cal.get(Calendar.MINUTE), intent, 0);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

        Toast.makeText(this, "Canceled.", Toast.LENGTH_SHORT).show();
    }

    public void goToParent(View v) {
        finish();
    }

}
