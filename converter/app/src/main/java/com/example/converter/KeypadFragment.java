package com.example.converter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class KeypadFragment extends Fragment  implements View.OnClickListener{
    ConverterViewModel converterViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        converterViewModel = ViewModelProviders.of(getActivity()).get(ConverterViewModel.class);
        View view =  inflater.inflate(R.layout.fragment_keypad, container, false);

        Button btn0 = (Button) view.findViewById(R.id.keypad0);
        Button btn1 = (Button) view.findViewById(R.id.keypad1);
        Button btn2 = (Button) view.findViewById(R.id.keypad2);
        Button btn3 = (Button) view.findViewById(R.id.keypad3);
        Button btn4 = (Button) view.findViewById(R.id.keypad4);
        Button btn5 = (Button) view.findViewById(R.id.keypad5);
        Button btn6 = (Button) view.findViewById(R.id.keypad6);
        Button btn7 = (Button) view.findViewById(R.id.keypad7);
        Button btn8 = (Button) view.findViewById(R.id.keypad8);
        Button btn9 = (Button) view.findViewById(R.id.keypad9);
        Button btnDel = (Button) view.findViewById(R.id.keypadDelete);
        Button btnDot = (Button) view.findViewById(R.id.keypadDot);

        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        btnDot.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        Button b = (Button) view;
        if (converterViewModel.GetInputData().getValue() == null){
            converterViewModel.setInputData("");
        }
        converterViewModel.setInputData(converterViewModel.GetInputData().getValue() + b.getText());
    }
}