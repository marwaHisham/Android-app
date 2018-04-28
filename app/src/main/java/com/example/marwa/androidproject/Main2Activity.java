package com.example.marwa.androidproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Main2Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    DatabaseReference ref;
    EditText tripName;
    EditText Notes;
    EditText tripType;
    private Spinner spinner;
    String type;
    private static final String[]paths = {"one Direction", "roundTrip"};
    static EditText DateEdit;    //TextClock tc;
    Button addTrip;
    Trip tr;
    Double startPointlang;
    Double startPointlat;
    Double  endPointlang;
    Double  endPointlat;
    String user;
    private PlaceAutocompleteFragment autocompleteFragment;
    private PlaceAutocompleteFragment autocompleteFragment1;
    String start;
    String end;
    CheckBox done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
       user= FirebaseAuth.getInstance().getCurrentUser().getUid();
        //user=getIntent().getStringExtra("user");
       // Toast.makeText(this, user, Toast.LENGTH_SHORT).show();
        ref = database.getReference("trips/"+user+"");
        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Main2Activity.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        //ref.child("test").setValue(1);
        tripName=findViewById(R.id.tripName);
        DateEdit=findViewById(R.id.editText6);
        DateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTruitonTimePickerDialog(v);
                showTruitonDatePickerDialog(v);
            }
        });
        Notes=findViewById(R.id.notes);
        addTrip=findViewById(R.id.AddTrip);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.start);
        autocompleteFragment1 = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.end);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                System.out.print(place.getName().toString());
                start=place.getName().toString();
                startPointlang=place.getLatLng().longitude;
                startPointlat=place.getLatLng().latitude;
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                // Log.i(TAG, "An error occurred: " + status);
            }
        });

        Intent n=new Intent(this,MainActivity.class);



        autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                // Log.i(TAG, "Place: " + place.getName());
                end=place.getName().toString();
                endPointlang=place.getLatLng().longitude;
                endPointlat=place.getLatLng().latitude;
                // Toast.makeText(Main2Activity.this, place.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                // Log.i(TAG, "An error occurred: " + status);
            }
        });



        final Intent intent=new Intent(this,MainActivity.class);
        addTrip.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                if(tripName.getText().toString().isEmpty())
                {
                    Toast.makeText(Main2Activity.this, "empty", Toast.LENGTH_SHORT).show();
                }
                else  if(!(tripName.getText().toString().isEmpty())) {
                    tr = new Trip();
                    //  tr.setId(id++);
                    tr.setTripName(tripName.getText().toString());
                    tr.setTripType(type);
                    tr.setNotes(Notes.getText().toString());
                    tr.setStartPoint(start);
                    tr.setEndPoint(end);
                    tr.setDate(DateEdit.getText().toString());
                    tr.setFlag(false);
                    tr.setEndPointLang(endPointlang);
                    tr.setEndPointLat(endPointlat);
                    tr.setStartPointLang(startPointlang);
                    tr.setStartPointLat(startPointlat);
                    ref.child(tripName.getText().toString()).setValue(tr);
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Main2Activity.this); //Read Update
                    alertDialog.setMessage("success :)");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    startActivity(intent);
                                    dialog.cancel();
                                }}).create().show();
                }
            }


        });

    }

    public void showTruitonDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position) {
            case 0:
                type="oneDirection";
                break;
            case 1:
                type="round Trip";
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            DateEdit.setText(day + "/" + (month + 1) + "/" + year);
        }
    }

    public void showTruitonTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            DateEdit.setText(DateEdit.getText() + "-" + hourOfDay + ":"+ minute);
        }
    }

}


