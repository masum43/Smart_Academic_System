package com.example.smartacademicsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import butterknife.ButterKnife;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private Spinner spinner;
    private String[] services_name;
    private String spinnerOption;
    private Button okBtn;
    Toolbar toolbar;



    //spinner
    private ArrayList<String> TypeAl = new ArrayList<>();
    private RelativeLayout selectCompanyRel;
    private TextView selectedCompanyTv;
    SpinnerDialog spinnerDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        okBtn = findViewById(R.id.okBtnId);

        selectCompanyRel = findViewById(R.id.selectCompanyBtnId);
        selectedCompanyTv = findViewById(R.id.selected_company_text_id);

        //adding spinner
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        TypeAl.add("Teacher");
        TypeAl.add("Student");


        spinnerDialog = new SpinnerDialog(this, TypeAl, getString(R.string.Select_or_search_company), "Close");// With No Animation
        spinnerDialog = new SpinnerDialog(this, TypeAl, getString(R.string.Select_or_search_company), R.style.DialogAnimations_SmileWindow, "Close");// With 	Animation

        spinnerDialog.setCancellable(true); // for cancellable
        spinnerDialog.setShowKeyboard(false);// for open keyboard by default


        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                //Toast.makeText(InitialActivity.this, item + "  " + position + "", Toast.LENGTH_SHORT).show();
                spinnerOption = item;
                selectedCompanyTv.setText(item);


            }
        });

        selectCompanyRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
            }
        });


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


    public void okBtn(View view) {

        if (spinnerOption!=null)
        {
            if(spinnerOption.equals("Student"))
            {
                startActivity(new Intent(this,StudentLoginActivity.class));
            }
            else if(spinnerOption.equals("Teacher"))
            {
                startActivity(new Intent(this,TeachersLoginActivity.class));
            }
        }

        else {
            Toast.makeText(this, "Please select who you are....", Toast.LENGTH_SHORT).show();
        }
    }
}