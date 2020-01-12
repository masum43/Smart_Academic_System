package com.example.smartacademicsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TeachersLoginActivity extends AppCompatActivity {

    Button teachersLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_login);

        teachersLoginBtn = findViewById(R.id.teacherLoginBtn);

        teachersLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(TeachersLoginActivity.this,TeachersHomeActivity.class));
                
            }
        });

    }
}
