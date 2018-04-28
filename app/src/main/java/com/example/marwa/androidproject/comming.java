package com.example.marwa.androidproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class comming extends AppCompatActivity {
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
    //FirebaseDatabase database;
    private PendingIntent pendingIntent;
Boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
           Intent alarmIntent = new Intent(comming.this, AlramReciver.class);
        n=new Intent(this,comming.class);
        n1=new Intent(this,last.class);
        n2=new Intent(this,MainActivity.class);

        pendingIntent = PendingIntent.getBroadcast(comming.this, 0, alarmIntent, 0);
        user= FirebaseAuth.getInstance().getCurrentUser().getUid();



        FirebaseDatabase   database = FirebaseDatabase.getInstance();//.setPersistenceEnabled(true);
         //n   database.setPersistenceEnabled(true);
            ref = database.getReference();

        name=findViewById(R.id.tripname);
            // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new TripAdapter(comming.this,TripList);
        manger = new LinearLayoutManager(comming.this,LinearLayoutManager.VERTICAL,false);


        final Query query = ref.child("trips/"+user+"").orderByChild("flag").equalTo(flag);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot !=null) {
                //    Toast.makeText(comming.this, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                }
                TripList.clear();
                //set alarm mnager take more than one alarm
                AlarmManager[] alarmManager=new AlarmManager[24];
                ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
                //**************
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Trip trip = snapshot.getValue(Trip.class);
                    TripList.add(trip);
                    adapter.notifyDataSetChanged();



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



    public void start() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 16000;

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
        String dtStart = "2010-10-15T09:27:37Z";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try

        {
            Date date = format.parse(dtStart);
            System.out.println(date);
            Log.i("TAG","date"+date);
        } catch(
                ParseException e)

        {
            e.printStackTrace();
        }
    }

    public void cancel() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        // Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }

    public void startAlarmAt() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 60 * 20;

        /* Set the alarm to start at 10:30 AM */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        calendar.set(Calendar.MINUTE, 55);
        Intent intent = new Intent(getApplicationContext(), AlramReciver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        /* Repeating on every 20 minutes interval */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                interval, pendingIntent);


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
