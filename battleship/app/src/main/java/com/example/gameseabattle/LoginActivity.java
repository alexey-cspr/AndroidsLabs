package com.example.gameseabattle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void signIn(View view) {
        if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Authorization failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "All fields must be complete", Toast.LENGTH_SHORT).show();
        }
    }

    public void signUp(View view) {
        if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
            firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    assert firebaseUser != null;
                    String userId = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference("Profiles").child(userId);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", userId);
                    hashMap.put("username", "user");
                    hashMap.put("imagePath", "default");
                    hashMap.put("wins", 0);
                    hashMap.put("gamesAmount", 0);
                    databaseReference.setValue(hashMap);
                    Toast.makeText(getApplicationContext(), "Registration was successful. Please, sign in now.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "All fields must be complete", Toast.LENGTH_SHORT).show();
        }
    }
}