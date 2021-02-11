package com.example.gameseabattle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    public void onClickMainPage(View view) {
        switch (view.getId()) {
            case R.id.btnCreateGame: {
                Intent intent = new Intent(getApplicationContext(), CreateGameActivity.class);
                intent.putExtra("role", "host");
                startActivity(intent);
            }
            break;
            case R.id.btnJoinGame: {
                Intent intent = new Intent(getApplicationContext(), EnterRoomActivity.class);
                intent.putExtra("role", "guest");
                startActivity(intent);
            }
            break;
            case R.id.btnProfile: {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
            break;
            case R.id.btnExit: {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
            break;
        }
    }
}