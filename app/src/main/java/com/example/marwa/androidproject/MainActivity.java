package com.example.marwa.androidproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    Intent intent;
    Intent n;
    Intent n1;
    Intent n2;
    TextView name,type;
    ArrayList<Trip> TripList = new ArrayList<>();
    ArrayList<String> DatesList = new ArrayList<>();
    private static final String PREFS_NAME = "trip";
    String user;
    TripAdapter adapter;
    DatabaseReference ref;
    RecyclerView  recyclerView;
    RecyclerView.LayoutManager manger;
    FirebaseDatabase database;
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        n=new Intent(this,comming.class);
        n1=new Intent(this,last.class);
        n2=new Intent(this,MainActivity.class);
        //**


//**
       // pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);


        if(ref == null){
            database = FirebaseDatabase.getInstance();//.setPersistenceEnabled(true);
         //n   database.setPersistenceEnabled(true);
            ref = database.getReference();
        }

        user= FirebaseAuth.getInstance().getCurrentUser().getUid();

        name=findViewById(R.id.tripname);
            // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new TripAdapter(MainActivity.this,TripList);
        manger = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false);
        ref.child("trips/"+user+"").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.i(TAG, dataSnapshot.getValue().toString());
             TripList.clear();

                //set alarm mnager take more than one alarm

                //**************


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.equals(null)) {
                        Toast.makeText(MainActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                    } else {

                        Trip trip = snapshot.getValue(Trip.class);
                        TripList.add(trip);
                        adapter.notifyDataSetChanged();
                        //set date for alarm
                        String[] separated = trip.getDate().split("-");
                        String trip_date = separated[0];
                        String trip_time = separated[1];
                        String[] time_seperated = trip_time.split(":");
                        String[] date_seperated = trip_date.split("/");
                        String trip_day = date_seperated[0];
                        String trip_month = date_seperated[1];
                        String trip_year = date_seperated[2];

                        String trip_hour = time_seperated[0];
                        String trip_min = time_seperated[1];
                        //

                        try {
                            if (new SimpleDateFormat("dd/M/yyyy-h:m").parse(trip.getDate()).after(new Date())) {
                                set_alarm( Integer.parseInt(trip_month), Integer.parseInt(trip_day),
                                        Integer.parseInt(trip_hour), Integer.parseInt(trip_min), trip.getTripName(), trip.getNotes(),trip.getStartPointLat().doubleValue(),trip.getEndPointLang().doubleValue());

                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }



                    }
                }
                recyclerView.setLayoutManager(manger);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);

                 Collections.reverse(TripList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: cancel");

            }
        });
        intent=new Intent(this,Main2Activity.class);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                 startActivity(intent);
            }
        });




    }


    public void set_alarm(int month, int day,int hour,int min, String title,
                          String note,double endLat,double endLong) {

        //**
        Calendar calendar2 = Calendar.getInstance();

        calendar2.set(Calendar.MONTH, month-1);
        calendar2.set(Calendar.DAY_OF_MONTH, day);
        calendar2.set(Calendar.HOUR_OF_DAY, hour);
        calendar2.set(Calendar.MINUTE, min);
        calendar2.set(Calendar.SECOND, 0);
        //**


        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("Trip_name", title);
        editor.putString("Trip_context", note);
        editor.putString("Trip_long", String.valueOf(endLong));
        editor.putString("Trip_lat", String.valueOf(endLat));

        editor.commit();

        Intent intent = new Intent(getApplicationContext(),AlramReciver.class );
        PendingIntent pintent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        AlarmManager alarm= (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP,
                calendar2.getTimeInMillis(), pintent);




    }




    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return  true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.comTrip:
                startActivity(n);
                break;
            case R.id.lastTrip:
                startActivity(n1);

                break;
            case R.id.history:
                startActivity(n2);

                break;
        }
        return true;
    }

}
