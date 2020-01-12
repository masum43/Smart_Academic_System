package com.example.smartacademicsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StudentLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);


    }

    public void studentLoginBtn(View view) {

        startActivity(new Intent(StudentLoginActivity.this,StudentHomeActivity.class));
    }
}
