package com.example.gameseabattle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LobbyActivity extends AppCompatActivity {
    protected LinearLayout field;
    protected TextView roomId, instruction;
    protected Button add;

    protected LobbyViewModel viewModel;

    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(LobbyViewModel.class);

        viewModel.setLobbyEventListener(new LobbyViewModel.LobbyEventListener() {
            @Override
            public void onToastRequired(String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUpdateInstructionRequired(String message) {
                instruction.setText(message);
            }

            @Override
            public void onUpdateAddButtonRequired(String message) {
                add.setText(message);
            }

            @Override
            public void onStartGameRequired(String roomId) {
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                intent.putExtra("RoomId", roomId);
                startActivity(intent);
                finish();
            }

            @Override
            public String requestRoomId() {
                return roomId.getText().toString();
            }
        });
    }


    protected void createField() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                100, 100);
        params.setMargins(3, 3, 3, 3);
        for (int i = 0; i < 10; ++i) {
            LinearLayout linearLayout = new LinearLayout(getApplicationContext());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            field.addView(linearLayout);
            for (int j = 0; j < 10; j++) {
                Button btn = new Button(getApplicationContext());
                btn.setId(i * 10 + j);
                btn.setLayoutParams(params);
                btn.setOnClickListener(v -> {
                    cellClicked(btn, params);
                });
                linearLayout.addView(btn);
            }
        }
    }

    public void cellClicked(Button button, LinearLayout.LayoutParams params) {
        if (button.getText().equals("0")) {
            button.setLayoutParams(params);
            button.setText("");
            viewModel.clickedButtonsId.remove(button.getId());
        } else {
            button.setText("0");
            button.setTextColor(Color.BLACK);
            button.setEnabled(false);
            viewModel.clickedButtonsId.add(button.getId());
        }
    }
}