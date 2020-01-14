package com.example.smartacademicsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

public class TeachersHomeActivity extends AppCompatActivity {

    EditText etDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_home);

        etDate = findViewById(R.id.et_date);

        Calendar calendar =Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        etDate =findViewById(R.id.et_date);

        if(etDate != null)
        {
            etDate.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");



                }
            });
        }



    }
}

