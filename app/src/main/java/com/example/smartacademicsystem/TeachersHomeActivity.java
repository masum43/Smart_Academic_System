package com.example.smartacademicsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.smartacademicsystem.spinner.adapter.StringListAdapter;
import com.example.smartacademicsystem.spinner.model.Person;
import com.github.bkhezry.searchablespinner.SearchableSpinner;
import com.github.bkhezry.searchablespinner.interfaces.IStatusListener;
import com.github.bkhezry.searchablespinner.interfaces.OnItemSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TeachersHomeActivity extends AppCompatActivity {

    EditText etDate;
    Spinner classSpinner,groupSpinner,shiftSpinner,sectionSpinner;
    private String[] classes_name,groups_name,shift_name,section_spinner;
    private TextView mTv_date;

    //spinner
    Toolbar toolbar;
    @BindView(R.id.searchable_spinner)
    SearchableSpinner searchableSpinner;
    SearchableSpinner searchableSpinner1;
    @BindView(R.id.searchable_spinner2)
    SearchableSpinner searchableSpinner2;
    private StringListAdapter mStringListAdapter;
    private ArrayList<String> mStrings = new ArrayList<>();
    private ArrayList<Person> mPersons = new ArrayList<>();
    private ArrayList<Person> mPersons1 = new ArrayList<>();
    private boolean isSpinnerOpen = false;
    private String[] names = {
            "One",
            "Two",
            "Three",
            "Four",
            "Five",
            "Six",
            "Seven",
            "Eight",
            "Nine",
            "Ten",

    };

    private String[] shift = {
            "Day",
            "Night"

    };





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
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        initStringListValues();
        mStringListAdapter = new StringListAdapter(this, mStrings, "DroidNaskh-Regular.ttf");

        searchableSpinner.setFontName("DroidNaskh-Regular.ttf");
        searchableSpinner.setAdapter(mStringListAdapter);
        searchableSpinner.setSpinnerBorderColor(Color.BLACK);
        searchableSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position, long id) {
                Toast.makeText(TeachersHomeActivity.this,  mStringListAdapter.getItem(position) + " Selected", Toast.LENGTH_LONG).show();
                //spinnerOption = mStringListAdapter.getItem(position);

            }

            @Override
            public void onNothingSelected() {

            }
        });
        searchableSpinner.setStatusListener(new IStatusListener() {
            @Override
            public void spinnerIsOpening() {
                isSpinnerOpen = true;

            }

            @Override
            public void spinnerIsClosing() {
                isSpinnerOpen = false;
            }
        });
        initPersonListValues();



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

    //spinner
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!searchableSpinner.isInsideSearchEditText(event)) {
            searchableSpinner.hideEdit();
        }
        return super.onTouchEvent(event);
    }

    private void initStringListValues() {
        Collections.addAll(mStrings, names);
    }

    private void initPersonListValues() {
        for (int i = 0; i < names.length; i++) {
            Person person = new Person();
            person.setId((long) i);
            person.setName(names[i]);
            mPersons.add(person);
            mPersons1.add(person);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_about) {
            //startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        if (isSpinnerOpen) {
            searchableSpinner.hideEdit();
            searchableSpinner1.hideEdit();
        } else {
            super.onBackPressed();
        }
    }
}

