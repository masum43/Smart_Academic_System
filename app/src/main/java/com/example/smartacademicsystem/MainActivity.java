package com.example.smartacademicsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Spinner spinner;
    private String[] services_name;
    private String spinnerOption;
    private Button okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        okBtn = findViewById(R.id.okBtnId);

        //adding spinner
        spinner = findViewById(R.id.services_spinner);
        services_name = getResources().getStringArray(R.array.services_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.single_spinner_view,R.id.singleSpinnerTvId,services_name);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Object item = parent.getItemAtPosition(position);

                if (item != null && (item.toString()).equals("Student"))
                {

                    spinnerOption = "Student";

                }

                else if(item != null && (item.toString()).equals("Teacher"))
                {
                    spinnerOption ="Teacher";
                }
                else if(item != null && (item.toString()).equals("Guardian"))
                {

                    spinnerOption ="Guardian";

                }
                else
                {
                    spinnerOption = "Select Anything";
                    Toast.makeText(MainActivity.this, "Select Anything", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(spinnerOption.equals("Student"))
                {
                    startActivity(new Intent(MainActivity.this,StudentLoginActivity.class));
                }
                else if (spinnerOption.equals("Teacher"))
                {
                    startActivity(new Intent(MainActivity.this,TeachersLoginActivity.class));
                }
                else if (spinnerOption.equals("Guardian"))
                {
                    startActivity(new Intent(MainActivity.this,GuardianLoginActivity.class));
                }
                else {
                    Toast.makeText(MainActivity.this, "Select Anything", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }


}
