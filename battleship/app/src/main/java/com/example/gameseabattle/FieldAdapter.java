package com.example.gameseabattle;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;


class FieldAdapter extends RecyclerView.Adapter<FieldAdapter.FieldViewHolder> {
    public interface ButtonClickListener {
        void onButtonClick(int i, int j, int btnId);
    }

    public static final int MAIN_FIELD = 0;
    public static final int OTHER_FIELD = 1;

    private ButtonClickListener buttonClickListener;
    private int second;
    private int fieldType;
    private Context context;
    private HashMap<Integer, Button> buttons = new HashMap<>();

    public FieldAdapter(Context context, int second, int fieldType) {
        this.context = context;
        this.second = second;
        this.fieldType = fieldType;
    }

    public void setButtonClickListener(ButtonClickListener listener) {
        buttonClickListener = listener;
    }

    public void disableButton(int id) {
        if (id >= 0) {
            Button btn = buttons.get(id);
            btn.setEnabled(false);
        }
    }

    public void enableButton(int id) {
        if (id >= 0) {
            Button btn = buttons.get(id);
            btn.setEnabled(true);
        }
    }


    @NonNull
    @Override
    public FieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.field, parent, false);

        return new FieldViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FieldViewHolder holder, int position) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                75, 75);
        params.setMargins(2, 2, 2, 2);

        for (int i = 0; i < 10; ++i) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            ((LinearLayout) holder.itemView).addView(linearLayout);

            for (int j = 0; j < 10; j++) {
                Button btn = new Button(context);

                if (fieldType != MAIN_FIELD) {
                    btn.setEnabled(false);
                }

                int btnId = i * 10 + j + second;
                buttons.put(btnId, btn);
                btn.setLayoutParams(params);
                btn.setTextColor(Color.BLACK);
                linearLayout.addView(btn);
                int iIdx = i;
                int jIdx = j;
                btn.setOnClickListener(v -> {
                    if (buttonClickListener != null) {
                        buttonClickListener.onButtonClick(iIdx, jIdx, btnId);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class FieldViewHolder extends RecyclerView.ViewHolder {

        public FieldViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}