package com.example.gasapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Objects;

public class AddRecordActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = AddRecordActivity.class.getName();
    private FirebaseUser user;

    User loggedInUser;

    EditText valueET;
    TextView dateChosenTV;
    TextView timeChosenTV;

    private FirebaseFirestore mStore;
    private CollectionReference mUsers;
    private CollectionReference mRecords;

    //private NotificationHandler mNotificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mStore = FirebaseFirestore.getInstance();
        mUsers = mStore.collection("Users");
        mRecords = mStore.collection("Records");

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user");
            mUsers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (Objects.equals(document.getString("email"), user.getEmail())) {
                                loggedInUser = new User(
                                        document.getString("name"),
                                        document.getString("email"),
                                        document.getString("address"),
                                        document.getString("phone"),
                                        document.getString("phoneType"));

                            }
                        }
                    } else {
                        Log.d(LOG_TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        } else {
            Log.d(LOG_TAG, "Unauthenticated user");
            finish();
        }

        valueET = findViewById(R.id.valueEditText);
        dateChosenTV = findViewById(R.id.dateChosenTextView);
        timeChosenTV = findViewById(R.id.timeChosenTextView);


        //mNotificationHandler = new NotificationHandler(this);
    }

    public void getAppointment(View view) {

        if (valueET.getText().toString().isEmpty() || dateChosenTV.getText().toString().isEmpty()
                || timeChosenTV.getText().toString().isEmpty()) {

            new AlertDialog.Builder(this)
                    .setTitle("Diktálás").setMessage("Mérőállás rögzítés, dátum és idő választás kötelező!")
                    .setPositiveButton("OK", null).show();
        }
        else {
            String value = valueET.getText().toString();
            String date = dateChosenTV.getText().toString();
            String time = timeChosenTV.getText().toString();
            String[] timeParts = time.split(":");
            String formattedTime = String.format("%02d:%02d", Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
            String dateAndTime = date + ", " + formattedTime;

            RecordItem record = new RecordItem(loggedInUser, value, dateAndTime);
            mRecords.add(record);
            //mNotificationHandler.send("Új időpont rögzítve!");
            toMyAppointments();
        }
    }

    public void toMyAppointments() {
        Intent intent = new Intent(this, RecordsListedActivity.class);
        startActivity(intent);
    }

    public void pickDate(View view) {
        final Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddRecordActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        dateChosenTV.setText(date);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    public void pickTime(View view) {
        final Calendar c = Calendar.getInstance();

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(AddRecordActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = hourOfDay + ":" + minute;
                        timeChosenTV.setText(time);
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

    public void cancel(View view) {
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedItem = adapterView.getItemAtPosition(i).toString();
        Log.i(LOG_TAG, selectedItem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}