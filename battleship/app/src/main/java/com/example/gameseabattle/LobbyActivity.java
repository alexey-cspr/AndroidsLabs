package com.example.gameseabattle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class LobbyActivity extends AppCompatActivity {
    protected LinearLayout field;
    protected TextView roomId, instruction;
    protected Button add;

    protected int state = 0;
    protected DatabaseReference databaseReference;

    protected ArrayList<Integer> clickedButtonsId = new ArrayList<>();
    protected int shipNo = 0;
    protected int[][] fieldArray = new int[10][10];
    protected String role;


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
            clickedButtonsId.remove(button.getId());
        } else {
            button.setText("0");
            button.setTextColor(Color.BLACK);
            button.setEnabled(false);
            clickedButtonsId.add(button.getId());
        }
    }


    public void checkShipPlacement(View view) {
        if (shipNo == 0) {
            //4-cell ship
            if (clickedButtonsId.size() != 4) {
                Toast.makeText(getApplicationContext(), "Wrong placement for 4-cell ship", Toast.LENGTH_SHORT).show();

                clickedButtonsId.clear();
                return;
            } else {
                int difference = clickedButtonsId.get(1) - clickedButtonsId.get(0);
                for (int i = 2; i < 4; i++) {
                    int current_diff = clickedButtonsId.get(i) - clickedButtonsId.get(i - 1);
                    if (difference != current_diff) {
                        Toast.makeText(getApplicationContext(), "Wrong placement for 4-cell ship", Toast.LENGTH_SHORT).show();

                        clickedButtonsId.clear();
                        return;
                    }
                }

                shipNo++;
                instruction.setText("3-cell ship");
            }
        } else if (shipNo == 1 || shipNo == 2) {
            //3-cell ship
            if (clickedButtonsId.size() != 3) {
                Toast.makeText(getApplicationContext(), "Wrong placement for 3-cell ship", Toast.LENGTH_SHORT).show();

                clickedButtonsId.clear();
                return;
            } else {
                int difference = clickedButtonsId.get(1) - clickedButtonsId.get(0);
                for (int i = 2; i < 3; i++) {
                    int current_diff = clickedButtonsId.get(i) - clickedButtonsId.get(i - 1);
                    if (difference != current_diff) {
                        Toast.makeText(getApplicationContext(), "Wrong placement for 3-cell ship", Toast.LENGTH_SHORT).show();

                        clickedButtonsId.clear();
                        return;
                    }
                }

                shipNo++;
                if (shipNo == 3) {
                    instruction.setText("2-cell ship");
                }
            }

        } else if (shipNo == 3 || shipNo == 4 || shipNo == 5) {
            if (clickedButtonsId.size() != 2) {
                Toast.makeText(getApplicationContext(), "Wrong placement for 2-cell ship", Toast.LENGTH_SHORT).show();

                clickedButtonsId.clear();
                return;
            } else {
                int difference = clickedButtonsId.get(1) - clickedButtonsId.get(0);
                if (difference != 1 && difference != 10) {
                    Toast.makeText(getApplicationContext(), "Wrong placement for 2-cell ship", Toast.LENGTH_SHORT).show();

                    clickedButtonsId.clear();
                    return;
                }

                shipNo++;
                if (shipNo == 6) {
                    instruction.setText("1-cell ship");
                }
            }

        } else if (shipNo == 6 || shipNo == 7 || shipNo == 8 || shipNo == 9) {
            //1-cell ship
            if (clickedButtonsId.size() != 1) {
                Toast.makeText(getApplicationContext(), "Wrong placement for 1-cell ship", Toast.LENGTH_SHORT).show();
                clickedButtonsId.clear();
                return;
            } else {

                int id = clickedButtonsId.get(0);
                fieldArray[id / 10][id % 10] = 1;

                shipNo++;
                if (shipNo > 9) {
                    instruction.setText("All ships are in place");
                    add.setText("Start game!");
                }
            }
        } else {
            createRoom();
        }
        clickedButtonsId.clear();
    }

    protected void createRoom() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Rooms").child(roomId.getText().toString());

        if (role.equals("host")) {
            HashMap<String, Object> field = new HashMap<>();
            field.put("id", roomId.getText().toString());
            field.put("field1", new Gson().toJson(fieldArray));
            field.put("hostUser", FirebaseAuth.getInstance().getCurrentUser().getUid());
            databaseReference.updateChildren(field);
        } else {
            databaseReference.get().addOnSuccessListener(dataSnapshot -> {
                HashMap<String, Object> current = (HashMap<String, Object>) dataSnapshot.getValue();

                HashMap<String, Object> field = new HashMap<>();
                field.put("id", current.get("id"));
                field.put("field1", current.get("field1"));
                field.put("hostUser", current.get("hostUser"));
                field.put("field2", new Gson().toJson(fieldArray));
                field.put("gameState", GameState.HOST_TURN);
                field.put("user", FirebaseAuth.getInstance().getCurrentUser().getUid());
                databaseReference.updateChildren(field);
            });

        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                state++;
                if (state == 2) {
                    Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                    intent.putExtra("RoomId", roomId.getText().toString());
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}