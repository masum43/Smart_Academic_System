package com.example.smartacademicsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.smartacademicsystem.spinner.adapter.PersonListAdapter;
import com.example.smartacademicsystem.spinner.adapter.StringListAdapter;
import com.example.smartacademicsystem.spinner.model.Person;
import com.github.bkhezry.searchablespinner.SearchableSpinner;
import com.github.bkhezry.searchablespinner.interfaces.IStatusListener;
import com.github.bkhezry.searchablespinner.interfaces.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private Spinner spinner;
    private String[] services_name;
    private String spinnerOption;
    private Button okBtn;

    //spinner
    Toolbar toolbar;
    @BindView(R.id.searchable_spinner)
    SearchableSpinner searchableSpinner;
    SearchableSpinner searchableSpinner1;
    SearchableSpinner searchableSpinner2;
    private StringListAdapter mStringListAdapter;
    private ArrayList<String> mStrings = new ArrayList<>();
    private ArrayList<Person> mPersons = new ArrayList<>();
    private ArrayList<Person> mPersons1 = new ArrayList<>();
    private boolean isSpinnerOpen = false;
    private String[] names = {
            "Teacher",
            "Student"

    };
    private String[] emails = {
            "aardo@yahoo.ca",
            "mfburgo@icloud.com"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        okBtn = findViewById(R.id.okBtnId);

        //adding spinner
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
                Toast.makeText(MainActivity.this,  mStringListAdapter.getItem(position) + " Selected", Toast.LENGTH_LONG).show();
                spinnerOption = mStringListAdapter.getItem(position);

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
        //mPersonListAdapter = new PersonListAdapter(this, mPersons, "DroidNaskh-Regular.ttf");
    }





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
        for (int i = 0; i < emails.length; i++) {
            Person person = new Person();
            person.setId((long) i);
            person.setName(names[i]);
            person.setEmail(emails[i]);
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

    public void okBtn(View view) {

        if(spinnerOption.equals("Student"))
        {
            startActivity(new Intent(this,StudentHomeActivity.class));
        }
        else if(spinnerOption.equals("Teacher"))
        {
            startActivity(new Intent(this,TeachersHomeActivity.class));
        }
        else {
            Toast.makeText(this, "Please select who you are....", Toast.LENGTH_SHORT).show();
        }
    }
}