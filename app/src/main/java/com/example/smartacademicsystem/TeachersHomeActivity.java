package com.example.smartacademicsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TeachersHomeActivity extends AppCompatActivity {

    EditText etDate;
    Spinner classSpinner,groupSpinner,shiftSpinner,sectionSpinner;
    private String[] classes_name,groups_name,shift_name,section_spinner;
    private TextView mTv_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_home);

        //etDate = findViewById(R.id.et_date);

        Calendar calendar =Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        //toggle btn initialize
        final ToggleButton tb = findViewById(R.id.toggleButton);

        mTv_date = findViewById(R.id.tv_date);


        //all spinner
        //adding class spinner
        classSpinner = findViewById(R.id.class_spinner);
        classes_name = getResources().getStringArray(R.array.classes_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.single_spinner_view_teachers_home,R.id.singleSpinnerTvId,classes_name);
        classSpinner.setAdapter(adapter);



        /*etDate =findViewById(R.id.et_date);

        if(etDate != null)
        {
            etDate.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");



                }
            });
        } */

        //setting current date to the textview
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        mTv_date.setText(formattedDate);



        //toogle btn
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled

                } else {
                    // The toggle is disabled

                }
            }
        });




    }
}

