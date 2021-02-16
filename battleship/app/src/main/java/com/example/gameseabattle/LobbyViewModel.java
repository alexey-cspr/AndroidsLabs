package com.example.gameseabattle;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class LobbyViewModel extends ViewModel {
    public interface LobbyEventListener {
        void onToastRequired(String message);
        void onUpdateInstructionRequired(String message);
        void onUpdateAddButtonRequired(String message);
        void onStartGameRequired(String roomId);
        String requestRoomId();
    }

    private int shipNo = 0;
    private int[][] fieldArray = new int[10][10];
    private LobbyEventListener lobbyEventListener;
    private int state = 0;
    private DatabaseReference databaseReference;

    public String role;
    public ArrayList<Integer> clickedButtonsId = new ArrayList<>();

    public void setLobbyEventListener(LobbyEventListener listener) {
        lobbyEventListener = listener;
    }

    public void checkShipPlacement(View view) {
        if (shipNo == 0) {
            //4-cell ship
            if (clickedButtonsId.size() != 4) {

                if (lobbyEventListener != null) {
                    lobbyEventListener.onToastRequired("Wrong placement for 4-cell ship");
                }

                clickedButtonsId.clear();
                return;
            } else {
                int difference = clickedButtonsId.get(1) - clickedButtonsId.get(0);
                for (int i = 2; i < 4; i++) {
                    int current_diff = clickedButtonsId.get(i) - clickedButtonsId.get(i - 1);
                    if (difference != current_diff) {

                        if (lobbyEventListener != null) {
                            lobbyEventListener.onToastRequired("Wrong placement for 4-cell ship");
                        }

                        clickedButtonsId.clear();
                        return;
                    }
                }
                shipNo++;

                if (lobbyEventListener != null) {
                    lobbyEventListener.onUpdateInstructionRequired("3-cell ship");
                }
            }
        } else if (shipNo == 1 || shipNo == 2) {
            //3-cell ship
            if (clickedButtonsId.size() != 3) {
                if (lobbyEventListener != null) {
                    lobbyEventListener.onToastRequired("Wrong placement for 3-cell ship");
                }

                clickedButtonsId.clear();
                return;
            } else {
                int difference = clickedButtonsId.get(1) - clickedButtonsId.get(0);
                for (int i = 2; i < 3; i++) {
                    int current_diff = clickedButtonsId.get(i) - clickedButtonsId.get(i - 1);
                    if (difference != current_diff) {
                        if (lobbyEventListener != null) {
                            lobbyEventListener.onToastRequired("Wrong placement for 3-cell ship");
                        }

                        clickedButtonsId.clear();
                        return;
                    }
                }

                shipNo++;
                if (shipNo == 3) {
                    if (lobbyEventListener != null) {
                        lobbyEventListener.onUpdateInstructionRequired("2-cell ship");
                    }
                }
            }

        } else if (shipNo == 3 || shipNo == 4 || shipNo == 5) {
            if (clickedButtonsId.size() != 2) {
                if (lobbyEventListener != null) {
                    lobbyEventListener.onToastRequired("Wrong placement for 2-cell ship");
                }

                clickedButtonsId.clear();
                return;
            } else {
                int difference = clickedButtonsId.get(1) - clickedButtonsId.get(0);
                if (difference != 1 && difference != 10) {
                    if (lobbyEventListener != null) {
                        lobbyEventListener.onToastRequired("Wrong placement for 2-cell ship");
                    }

                    clickedButtonsId.clear();
                    return;
                }

                shipNo++;
                if (shipNo == 6) {
                    if (lobbyEventListener != null) {
                        lobbyEventListener.onUpdateInstructionRequired("1-cell ship");
                    }
                }
            }

        } else if (shipNo == 6 || shipNo == 7 || shipNo == 8 || shipNo == 9) {
            //1-cell ship
            if (clickedButtonsId.size() != 1) {
                if (lobbyEventListener != null) {
                    lobbyEventListener.onToastRequired("Wrong placement for 1-cell ship");
                }

                clickedButtonsId.clear();
                return;
            } else {

                int id = clickedButtonsId.get(0);
                fieldArray[id / 10][id % 10] = 1;

                shipNo++;
                if (shipNo > 9) {
                    if (lobbyEventListener != null) {
                        lobbyEventListener.onUpdateInstructionRequired("All ships are in place");
                        lobbyEventListener.onUpdateAddButtonRequired("Start game!");
                    }
                }
            }
        } else {
            createRoom();
        }
        clickedButtonsId.clear();
    }

    private void createRoom() {
        if (lobbyEventListener != null) {
            String roomId = lobbyEventListener.requestRoomId();
            databaseReference = FirebaseDatabase.getInstance().getReference("Rooms").child(roomId);
        }

        if (role.equals("host")) {
            HashMap<String, Object> field = new HashMap<>();
            if (lobbyEventListener != null) {
                field.put("id", lobbyEventListener.requestRoomId());
            }
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
                    if (lobbyEventListener != null) {
                        String roomId = lobbyEventListener.requestRoomId();
                        lobbyEventListener.onStartGameRequired(roomId);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
