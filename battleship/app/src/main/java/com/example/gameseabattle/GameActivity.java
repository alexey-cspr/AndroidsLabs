package com.example.gameseabattle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {
    private RecyclerView myField, otherUserField;
    GameViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        viewModel = new ViewModelProvider(this).get(GameViewModel.class);

        myField = findViewById(R.id.myField);
        otherUserField = findViewById(R.id.otherUserField);

        myField.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        otherUserField.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        FieldAdapter myFieldAdapter = new FieldAdapter(this, 0, FieldAdapter.MAIN_FIELD);
        FieldAdapter otherUserFieldAdapter = new FieldAdapter(this, 100, FieldAdapter.OTHER_FIELD);

        myField.setAdapter(myFieldAdapter);
        otherUserField.setAdapter(otherUserFieldAdapter);

        String roomId = getIntent().getStringExtra("RoomId");

        new Handler().postDelayed((Runnable) () -> {
            viewModel.uploadGame(roomId);
            viewModel.setGameUpdate();
            viewModel.setGameEventListener(new GameViewModel.GameEventListener() {
                @Override
                public void onGameOver(int i) {
                    if (i < 100) {
                        myFieldAdapter.disableButton(i);
                    } else {
                        otherUserFieldAdapter.disableButton(i);
                    }
                }

                @Override
                public void onToastRequired(String message) {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onEnableButtonRequired(int btnId) {
                    if (btnId < 100) {
                        myFieldAdapter.enableButton(btnId);
                    } else {
                        otherUserFieldAdapter.enableButton(btnId);
                    }
                }

                @Override
                public void onDisableButtonRequired(int btnId) {
                    if (btnId < 100) {
                        myFieldAdapter.disableButton(btnId);
                    } else {
                        otherUserFieldAdapter.disableButton(btnId);
                    }
                }

                @Override
                public void onUpdateButtonTextRequired(int btnId, String newText) {
                    ((Button) findViewById(btnId)).setText(newText);
                }

                @Override
                public void onButtonClick(int btnId) {
                    otherUserFieldAdapter.disableButton(btnId);
                }
            });

            otherUserFieldAdapter.setButtonClickListener((i, j, btnId) -> viewModel.handleButtonClick(i, j, btnId));
        }, 1000);
    }
}