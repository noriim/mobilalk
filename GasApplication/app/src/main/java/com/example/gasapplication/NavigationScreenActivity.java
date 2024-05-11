package com.example.gasapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NavigationScreenActivity extends AppCompatActivity {
    private static final String LOG_TAG = NavigationScreenActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    final int PERMISSION_REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user");
            finish();
        }

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences pref = this.getSharedPreferences("PACKAGE.NAME",MODE_PRIVATE);
        boolean firstTime = pref.getBoolean("firstTime",true);
        if(firstTime){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!shouldShowRequestPermissionRationale("99")){
                    getNotificationPermission();
                }
            }

            pref.edit().putBoolean("firstTime",false).apply();
        }

    }

    public void getNotificationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Értesítések").setMessage("Nem fog kapni értesítéseket időpont foglalás, törlés és " +
                                    "sürgőssé megjelölés után!")
                            .setPositiveButton("OK", null).show();
                }
                return;

        }
    }


    public void toAppointmentBook(View view) {
        Intent intent = new Intent(this, AddRecordActivity.class);
        startActivity(intent);
    }

    public void toMyAppointments(View view) {
        Intent intent = new Intent(this, ListedActivity.class);
        startActivity(intent);
    }

    public void toContacts(View view) {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }

    public void signOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        new AlertDialog.Builder(this)
                .setTitle("Kijelentkezés").setMessage("Biztosan ki szeretne jelentkezni?")
                .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAuth.signOut();
                        startActivity(intent);
                        Log.d(LOG_TAG, "User logged out.");
                    }
                }).setNegativeButton("Nem", null).show();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        new AlertDialog.Builder(this)
                .setTitle("Kijelentkezés").setMessage("Biztosan ki szeretne jelentkezni?")
                .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAuth.signOut();
                        startActivity(intent);
                        Log.d(LOG_TAG, "User logged out.");
                    }
                }).setNegativeButton("Nem", null).show();
    }
}