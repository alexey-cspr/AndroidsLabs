package com.example.gameseabattle;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

public class CreateGameActivity extends LobbyActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        super.roomId = findViewById(R.id.roomId);
        super.field = findViewById(R.id.field);
        super.instruction = findViewById(R.id.instructionShipId);
        super.add = findViewById(R.id.btnAdd);
        super.roomId.setText(String.valueOf((new Random()).nextInt(50)));
        super.instruction.setText("4-cell ship");
        super.role = getIntent().getStringExtra("role");

        findViewById(R.id.btnCopyId).setOnClickListener(this::copyId);
        findViewById(R.id.btnAdd).setOnClickListener(super::checkShipPlacement);
        createField();
    }


    public void copyId(View view) {
        String label = "Copied";
        ClipboardManager clipboard = (ClipboardManager) getBaseContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, roomId.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied!", Toast.LENGTH_LONG).show();
    }
}