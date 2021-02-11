package com.example.gameseabattle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.gameseabattle.Models.Room;
import com.example.gameseabattle.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;

public class GameActivity extends AppCompatActivity {
    private LinearLayout myField, otherUserField;
    private DatabaseReference databaseReference;
    private String roomId;
    private Room currentRoom;

    public int[][] myFieldArray;
    public int[][] otherUserFieldArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        myField = findViewById(R.id.myField);
        otherUserField = findViewById(R.id.otherUserField);
        roomId = getIntent().getStringExtra("RoomId");
        databaseReference = FirebaseDatabase.getInstance().getReference("Rooms").child(roomId);
        uploadGame();
        setGameUpdate();

        createField(myField, 0);
        for (int i = 0; i < 100; ++i) {
            disableButton(i);
        }
        createField(otherUserField, 100);
    }

    private void uploadGame() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentRoom = snapshot.getValue(Room.class);
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (uid.equals(currentRoom.getHostUser())) {
                    myFieldArray = new Gson().fromJson(currentRoom.getField1(), int[][].class);
                    otherUserFieldArray = new Gson().fromJson(currentRoom.getField2(), int[][].class);
                } else {
                    myFieldArray = new Gson().fromJson(currentRoom.getField2(), int[][].class);
                    otherUserFieldArray = new Gson().fromJson(currentRoom.getField1(), int[][].class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setGameUpdate() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentRoom = snapshot.getValue(Room.class);
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (uid.equals(currentRoom.getHostUser())) {
                    myFieldArray = new Gson().fromJson(currentRoom.getField1(), int[][].class);
                    otherUserFieldArray = new Gson().fromJson(currentRoom.getField2(), int[][].class);
                } else {
                    myFieldArray = new Gson().fromJson(currentRoom.getField2(), int[][].class);
                    otherUserFieldArray = new Gson().fromJson(currentRoom.getField1(), int[][].class);
                }
                if (currentRoom.getGameState() == GameState.GAME_OVER) {
                    for (int i = 100; i < 200; ++i) {
                        disableButton(i);
                    }
                } else {
                    updateAllButtons();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void disableButton(int id) {
        if (id >= 0) {
            Button btn = findViewById(id);
            btn.setEnabled(false);
        }
    }

    private void enableButton(int id) {
        if (id >= 0) {
            Button btn = findViewById(id);
            btn.setEnabled(true);
        }
    }

    private void createField(LinearLayout view, int second) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                75, 75);
        params.setMargins(2, 2, 2, 2);
        for (int i = 0; i < 10; ++i) {
            LinearLayout linearLayout = new LinearLayout(getApplicationContext());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            view.addView(linearLayout);
            for (int j = 0; j < 10; j++) {
                Button btn = new Button(getApplicationContext());
                btn.setId(i * 10 + j + second);
                btn.setLayoutParams(params);
                btn.setTextColor(Color.BLACK);
                linearLayout.addView(btn);
                int i_idx = i;
                int j_idx = j;
                btn.setOnClickListener(v -> {
                    myFieldArray[i_idx][j_idx] = 1;
                    int id = btn.getId();
                    disableButton(btn.getId());
                    int state = otherUserFieldArray[(id - 100) / 10][(id - 100) % 10];
                    if (state == 1) {
                        btn.setText("X");
                        otherUserFieldArray[(id - 100) / 10][(id - 100) % 10] = 3;
                    } else {
                        otherUserFieldArray[(id - 100) / 10][(id - 100) % 10] = 2;
                        if (currentRoom.getHostUser().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            currentRoom.setGameState(GameState.USER_TURN);
                        } else {
                            currentRoom.setGameState(GameState.HOST_TURN);
                        }
                    }
                    updateGame();
                });
            }
        }
    }

    public void updateGame() {
        HashMap<String, Object> newField = new HashMap<>();
        if (currentRoom.getHostUser().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            newField.put("field2", new Gson().toJson(otherUserFieldArray));
        } else {
            newField.put("field1", new Gson().toJson(otherUserFieldArray));
        }
        if (isEnded()) {
            currentRoom.setGameState(GameState.GAME_OVER);
        }
        if (currentRoom.getGameState().equals(GameState.GAME_OVER)) {
            DatabaseReference userProfile = FirebaseDatabase.getInstance().getReference("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            userProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    HashMap<String, Object> statistics = new HashMap<>();
                    assert user != null;
                    if (user.getGamesAmount() != null) {
                        statistics.put("total", user.getGamesAmount() + 1);
                    } else {
                        statistics.put("total", 1);
                    }
                    userProfile.updateChildren(statistics);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        newField.put("gameState", currentRoom.getGameState());
        databaseReference.updateChildren(newField);
    }

    private boolean isEnded() {
        int amountOfMyEndedShips = 0;
        int otherUserEndedShips = 0;
        for (int i = 0; i < 100; ++i) {
            amountOfMyEndedShips += myFieldArray[i / 10][i % 10] == 3 ? 1 : 0;
            otherUserEndedShips += otherUserFieldArray[i / 10][i % 10] == 3 ? 1 : 0;
        }
        if (amountOfMyEndedShips == 20) {
            Toast.makeText(getApplicationContext(), "Sorry, you loose", Toast.LENGTH_SHORT).show();
            return true;
        } else if (otherUserEndedShips == 20) {
            Toast.makeText(getApplicationContext(), "You WON!!!", Toast.LENGTH_SHORT).show();
            addAWin();
            return true;
        } else {
            return false;
        }
    }

    private void addAWin() {
        DatabaseReference userProfile = FirebaseDatabase.getInstance().getReference("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                HashMap<String, Object> stats = new HashMap<>();
                assert user != null;
                if (user.getWins() != null) {
                    stats.put("wins", user.getWins() + 1);
                } else {
                    stats.put("wins", 1);
                }
                userProfile.updateChildren(stats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateAllButtons() {
        for (int i = 0; i < 200; ++i) {
            Button btn = findViewById(i);
            int cur = i < 100 ? myFieldArray[i / 10][i % 10] : otherUserFieldArray[(i - 100) / 10][(i - 100) % 10];
            if (i >= 100) {
                if ((FirebaseAuth.getInstance().getCurrentUser().getUid().equals(currentRoom.getHostUser()) &&
                        currentRoom.getGameState().equals(GameState.HOST_TURN)) || (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(currentRoom.getUser()) &&
                        currentRoom.getGameState().equals(GameState.USER_TURN))) {
                    enableButton(i);
                } else {
                    disableButton(i);
                }
            }
            switch (cur) {
                case 2:
                    btn.setText("*");
                    disableButton(i);
                    break;
                case 3:
                    btn.setText("X");
                    disableButton(i);
                    break;
            }
        }
    }
}