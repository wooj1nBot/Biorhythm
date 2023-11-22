package com.example.biorhythm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;

public class BirthdayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday);
        DatePicker datePicker = findViewById(R.id.datePicker);
        Calendar birth = BiorhythmActivity.getDay(BirthdayActivity.this, true);
        datePicker.updateDate(birth.get(Calendar.YEAR), birth.get(Calendar.MONTH), birth.get(Calendar.DAY_OF_MONTH));
        Button button = findViewById(R.id.btn_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BirthdayActivity.this, BiorhythmActivity.class);
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                BiorhythmActivity.saveDay(BirthdayActivity.this, year,month,day, true);
                intent.putExtra("year", year);
                intent.putExtra("month", month);
                intent.putExtra("day", day);
                startActivity(intent);
            }
        });
    }
}