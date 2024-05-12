package com.example.gasapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = Objects.requireNonNull(RegisterActivity.class.getPackage()).toString();
    private static final int SECRET_KEY = 99;
    EditText userNameEditText;
    EditText userEmailEditText;
    EditText passwordEditText;
    EditText passwordConfirmEditText;
    EditText addressEditText;
    EditText phoneEditText;
    Spinner phoneSpinner;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private CollectionReference mUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != 99) {
            finish();
        }

        userNameEditText = findViewById(R.id.editTextUserName);
        userEmailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        passwordConfirmEditText = findViewById(R.id.editTextPasswordAgain);
        addressEditText = findViewById(R.id.editTextAddress);
        phoneEditText = findViewById(R.id.editTextPhoneNumber);
        phoneSpinner = findViewById(R.id.phoneSpinner);

        SharedPreferences preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String email = preferences.getString("email", "");
        String password = preferences.getString("password", "");

        userEmailEditText.setText(email);
        passwordEditText.setText(password);
        passwordConfirmEditText.setText(password);

        phoneSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.phoneModes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phoneSpinner.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mUsers = mStore.collection("Users");

        Log.i(LOG_TAG, "onCreate");
    }

    public void register(View view) {
        String name = userNameEditText.getText().toString();
        String email = userEmailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String phoneType = phoneSpinner.getSelectedItem().toString();

        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()
                || address.isEmpty() || phone.isEmpty() || phoneType.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Regisztráció").setMessage("Minden mező kitöltése kötelező!")
                    .setPositiveButton("OK", null).show();
        }else if (!password.equals(passwordConfirm)) {
            new AlertDialog.Builder(this)
                    .setTitle("Regisztráció").setMessage("A megadott jelszavak nem egyeznek!")
                    .setPositiveButton("OK", null).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(LOG_TAG, "User created successfully");
                        User user = new User(name, email, address, phone, phoneType);
                        mUsers.add(user);
                        loggedIn();
                    } else {
                        Log.d(LOG_TAG, "User wasn't created successfully.");
                        Toast.makeText(RegisterActivity.this, "User wasn't created successfully." + task.getException(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    public void cancel(View view) {
        finish();
    }

    private void loggedIn(/*userdata*/) {
        Intent intent = new Intent(this, NavigationScreenActivity.class);
        // intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getItemAtPosition(position).toString();
        Log.i(LOG_TAG, selectedItem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}