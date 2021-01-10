package com.example.converter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


public class PaidConverterFragment extends Fragment {
    ConverterViewModel converterViewModel;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        converterViewModel = new ViewModelProvider(requireActivity()).get(ConverterViewModel.class);
        view = inflater.inflate(R.layout.fragment_converter, container, false);

        EditText editIn = view.findViewById(R.id.editText1);
        EditText editOut = view.findViewById(R.id.editText2);

        SetSpinners(view, converterViewModel);
        SetButtons(view, converterViewModel);
        converterViewModel.GetInputData().observe(getViewLifecycleOwner(), value -> {editIn.setText(value);});
        converterViewModel.GetOutputData().observe(getViewLifecycleOwner(), value -> {editOut.setText(value);});

        return view;
    }

    public void SetButtons(View view, ConverterViewModel converterViewModel){
        Button copy_one = view.findViewById(R.id.copyBtn1);
        Button copy_two = view.findViewById(R.id.copyBtn2);
        Button change = view.findViewById(R.id.replace);

        change.setOnClickListener(view1 -> {converterViewModel.setInputData(converterViewModel.GetOutputData().getValue());});

        copy_one.setOnClickListener(view1 -> {
            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", converterViewModel.GetInputData().getValue());
            clipboard.setPrimaryClip(clip);
            Toast toast = Toast.makeText(requireContext().getApplicationContext(), "Field1 Copied", Toast.LENGTH_LONG);
            toast.show();
        });

        copy_two.setOnClickListener(view1 -> {
            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", converterViewModel.GetOutputData().getValue());
            clipboard.setPrimaryClip(clip);
            Toast toast = Toast.makeText(requireContext().getApplicationContext(), "Field2 Copied", Toast.LENGTH_LONG);
            toast.show();
        });
    }

    public void SetSpinners(View view, ConverterViewModel converterViewModel){
        Spinner spinnerIn = view.findViewById(R.id.spinner1);
        Spinner spinnerTo = view.findViewById(R.id.spinner2);

        converterViewModel.GetCurCategory().observe(getViewLifecycleOwner(), value->{
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, converterViewModel.GetCategoriesNames(value));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerIn.setAdapter(adapter);
            spinnerTo.setAdapter(adapter);
        });

        spinnerIn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();
                converterViewModel.SetCurFromCategory(name);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();
                converterViewModel.SetCurToCategory(name);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        converterViewModel.GetCurrentFromCategory().observe(getViewLifecycleOwner(), value -> {spinnerIn.setSelection(converterViewModel.GetCategoriesNames(converterViewModel.GetCurCategory().getValue()).indexOf(value));});
        converterViewModel.GetCurrentToCategory().observe(getViewLifecycleOwner(), value -> {spinnerTo.setSelection(converterViewModel.GetCategoriesNames(converterViewModel.GetCurCategory().getValue()).indexOf(value));});
    }
}
