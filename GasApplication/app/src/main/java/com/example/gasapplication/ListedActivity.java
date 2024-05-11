package com.example.gasapplication;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class ListedActivity extends AppCompatActivity {
    private static final String LOG_TAG = ListedActivity.class.getName();
    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private ArrayList<RecordItem> recordItemsList;
    private RecordItemAdapter mAdapter;
    private FirebaseFirestore mStore;
    private CollectionReference mRecords;
    private NotificationHandler mNotificationHandler;
    private int gridNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listed);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        recordItemsList = new ArrayList<>();

        mAdapter = new RecordItemAdapter(this, recordItemsList);
        mRecyclerView.setAdapter(mAdapter);

        mStore = FirebaseFirestore.getInstance();
        mRecords = mStore.collection("Records");

        mNotificationHandler = new NotificationHandler(this);

        queryData();
    }

    private void queryData() {
        recordItemsList.clear();

        mRecords.orderBy("dateAndTime", Query.Direction.DESCENDING).limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Map<String, String> user1 = (Map<String, String>) document.get("user");
                String email = user1.get("email");
                if (Objects.equals(email, user.getEmail())) {
                    RecordItem record = document.toObject(RecordItem.class);
                    record.setId(document.getId());
                    recordItemsList.add(record);
                }
            }

            if (recordItemsList.size() == 0) {
                noContent();
            }

            mAdapter.notifyDataSetChanged();
        });
    }

    public void deleteAppointment(RecordItem appointment) {
        DocumentReference ref = mRecords.document(appointment._getId());
        ref.delete().addOnSuccessListener(success -> {
            Log.d(LOG_TAG, "Record is deleted");
        }).addOnFailureListener(failure -> {
            Log.d(LOG_TAG, "Record deletion failed");
        });

        mNotificationHandler.send("Mérőállás törölve!");
        queryData();
    }

    public void updateAppointment(RecordItem record) {
        if (record.isWrong()) {
            new AlertDialog.Builder(this)
                    .setTitle("Diktálások").setMessage("Már küldött hibabejelentést erről a diktálásról!")
                    .setPositiveButton("OK", null).show();
        }
        else {
            mRecords.document(record._getId()).update("wrong", true);
            mNotificationHandler.send("Hiba bejelentve! Kollégáink értesítve lettek, kérjük türelemmel várja felkeresésüket!");
            queryData();
        }
    }

    public void noContent() {
        Intent intent = new Intent(this, NavigationScreenActivity.class);
        new AlertDialog.Builder(this)
                .setTitle("Diktálások").setMessage("Önnek nincs bediktált mérőállása!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(intent);
                        Log.d(LOG_TAG, "no records? ',:]");
                    }
                }).show();
    }

    public void cancel(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, NavigationScreenActivity.class);
        startActivity(intent);
    }
}