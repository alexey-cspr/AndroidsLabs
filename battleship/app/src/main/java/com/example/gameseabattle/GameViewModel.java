package com.example.gameseabattle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

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

public class GameViewModel extends ViewModel {
    public interface GameEventListener {
        void onGameOver(int i); // btn index
        void onToastRequired(String message);
        void onEnableButtonRequired(int btnId);
        void onDisableButtonRequired(int btnId);
        void onUpdateButtonTextRequired(int btnId, String newText);
        void onButtonClick(int btnId);
    }


    private int[][] myFieldArray;
    private int[][] otherUserFieldArray;

    private DatabaseReference databaseReference;
    private String roomId;
    private Room currentRoom;
    private GameEventListener gameEventListener;

    public void setGameEventListener(GameEventListener listener) {
        gameEventListener = listener;
    }


    public void uploadGame(String roomId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Rooms").child(roomId);
        this.roomId = roomId;
        FirebaseDatabase.getInstance().getReference("Rooms").child(this.roomId);
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

    public void setGameUpdate() {
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
                        if (gameEventListener != null) {
                            gameEventListener.onGameOver(i);
                        }
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

    public void handleButtonClick(int i, int j, int btnId) {
        myFieldArray[i][j] = 1;

        if (gameEventListener != null) {
            gameEventListener.onButtonClick(btnId);
        }

        int state = otherUserFieldArray[(btnId - 100) / 10][(btnId - 100) % 10];
        if (state == 1) {
            if (gameEventListener != null) {
                gameEventListener.onUpdateButtonTextRequired(btnId, "X");
            }

            otherUserFieldArray[(btnId - 100) / 10][(btnId - 100) % 10] = 3;
        } else {
            otherUserFieldArray[(btnId - 100) / 10][(btnId - 100) % 10] = 2;
            if (currentRoom.getHostUser().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                currentRoom.setGameState(GameState.USER_TURN);
            } else {
                currentRoom.setGameState(GameState.HOST_TURN);
            }
        }

        updateGame();
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
            if (gameEventListener != null) {
                gameEventListener.onToastRequired("Sorry, you loose");
            }

            return true;
        } else if (otherUserEndedShips == 20) {
            if (gameEventListener != null) {
                gameEventListener.onToastRequired("You WON!!!");
            }

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
            int cur = i < 100 ? myFieldArray[i / 10][i % 10] : otherUserFieldArray[(i - 100) / 10][(i - 100) % 10];
            if (i >= 100) {
                if ((FirebaseAuth.getInstance().getCurrentUser().getUid().equals(currentRoom.getHostUser()) &&
                        currentRoom.getGameState().equals(GameState.HOST_TURN)) || (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(currentRoom.getUser()) &&
                        currentRoom.getGameState().equals(GameState.USER_TURN))) {
                    if (gameEventListener != null) {
                        gameEventListener.onEnableButtonRequired(i);
                    }

                } else {
                    if (gameEventListener != null) {
                        gameEventListener.onDisableButtonRequired(i);
                    }
                }
            }
            switch (cur) {
                case 2:
                    if (gameEventListener != null) {
                        gameEventListener.onUpdateButtonTextRequired(i, "*");
                        gameEventListener.onDisableButtonRequired(i);
                    }

                    break;
                case 3:
                    if (gameEventListener != null) {
                        gameEventListener.onUpdateButtonTextRequired(i, "X");
                        gameEventListener.onDisableButtonRequired(i);
                    }

                    break;
            }
        }
    }
}
