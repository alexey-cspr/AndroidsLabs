package com.example.gameseabattle;

import android.os.Bundle;

public class EnterRoomActivity extends LobbyActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_room);
        super.field = findViewById(R.id.field);
        super.roomId = findViewById(R.id.roomId);
        super.instruction = findViewById(R.id.instructionShipId);
        super.add = findViewById(R.id.btnAdd);
        super.instruction.setText("4-cell ship");
        super.initViewModel();
        super.viewModel.role = getIntent().getStringExtra("role");
        findViewById(R.id.btnAdd).setOnClickListener(super.viewModel::checkShipPlacement);
        createField();
    }
}