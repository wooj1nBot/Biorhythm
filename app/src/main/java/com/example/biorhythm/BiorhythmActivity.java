package com.example.biorhythm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Stack;

public class BiorhythmActivity extends AppCompatActivity {

    private LineChart chart;
    private int index;
    private LinearLayout mother;
    TextView tv_sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biorhythm);
        chart = findViewById(R.id.chart);
        mother = findViewById(R.id.mother);
        ImageView profile = findViewById(R.id.profile);
        TextView tv_birth = findViewById(R.id.tv_birth);
        tv_sum = findViewById(R.id.tv_sum);

        Calendar birth = getDay(BiorhythmActivity.this, true);
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");
        if(user != null){
            birth = user.getBirthday();
            if(user.getImage() != null){
                if (ContextCompat.checkSelfPermission(BiorhythmActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    profile.setImageURI(user.getImage());
                    profile.setColorFilter(null);
                    Log.d("dwdw", "Dwqwqw");
                }else {
                    profile.setImageResource(R.drawable.profile);
                    profile.setColorFilter(user.getColor());
                }
            }else {
                profile.setImageResource(R.drawable.profile);
                profile.setColorFilter(user.getColor());
            }
            tv_birth.setText(user.getName() + "\n"+ String.format("Birthday %d. %d. %d", user.birthday.get(Calendar.YEAR), user.birthday.get(Calendar.MONTH)+1, user.birthday.get(Calendar.DAY_OF_MONTH)));
        }else {
            profile.setImageResource(R.drawable.profile);
            profile.setColorFilter(Color.parseColor("#1E88E5"));
            tv_birth.setText("\n"+ String.format("Birthday %d. %d. %d", birth.get(Calendar.YEAR), birth.get(Calendar.MONTH)+1, birth.get(Calendar.DAY_OF_MONTH)));
        }

        LinearLayout btn_date = findViewById(R.id.btn_date);
        TextView tv_ref = findViewById(R.id.tv_ref);
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = getDay(BiorhythmActivity.this, false);
                Calendar birth;
                if(user != null){
                    birth = user.getBirthday();
                }else {
                    birth = getDay(BiorhythmActivity.this, true);
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(BiorhythmActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        try {
                             Calendar next = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                             next.add(Calendar.DAY_OF_MONTH, 4);
                             String d = String.format("%d년 %d월 %d일 ~ %d년 %d월 %d일", year, monthOfYear+1, dayOfMonth, next.get(Calendar.YEAR), next.get(Calendar.MONTH)+1, next.get(Calendar.DAY_OF_MONTH));
                             tv_ref.setText(d);
                             setChart(chart, birth, year, monthOfYear, dayOfMonth, true);
                             saveDay(BiorhythmActivity.this, year, monthOfYear, dayOfMonth, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));


                datePickerDialog.getDatePicker().setCalendarViewShown(false);
                datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                datePickerDialog.show();

            }
        });

        Calendar c = getDay(BiorhythmActivity.this, false);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        setChart(chart, birth, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), true);

        c.add(Calendar.DAY_OF_MONTH, 4);
        String d = String.format("%d년 %d월 %d일 ~ %d년 %d월 %d일", year, month+1, day, c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));
        tv_ref.setText(d);

    }

    public static void saveDay(Context context , int year, int month, int day, boolean isbirth){
        SharedPreferences preferences;
        if(isbirth){
            preferences = context.getSharedPreferences("birthday", Context.MODE_PRIVATE);
        }else {
            preferences = context.getSharedPreferences("day", Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("year", year);
        editor.putInt("month", month);
        editor.putInt("day", day);
        editor.apply();
    }
    public static Calendar getDay(Context context, boolean isbirth){
        SharedPreferences preferences;
        if(isbirth){
            preferences = context.getSharedPreferences("birthday", Context.MODE_PRIVATE);
        }else {
            preferences = context.getSharedPreferences("day", Context.MODE_PRIVATE);
        }
        int year = preferences.getInt("year", -1);
        int month = preferences.getInt("month", 0);
        int day = preferences.getInt("day", 1);
        if(year == -1) return Calendar.getInstance();
        return new GregorianCalendar(year, month, day);
    }

    public void setChart(LineChart chart, Calendar birth, int year, int month, int day, boolean isMain){
        ArrayList<Entry> physicalData = getPhysicalData(birth, year, month, day, isMain);
        ArrayList<Entry> emotionData = getEmotionalData(birth, year, month, day, isMain);
        ArrayList<Entry> intellectData = getIntellectualData(birth, year, month, day, isMain);

        ArrayList<String> labels = getLabelData(year, month, day, isMain);

        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setGridBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(true);
        chart.getAxisRight().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setScaleXEnabled(false);
        chart.setScaleYEnabled(false);
        chart.setHighlightPerTapEnabled(false);
        chart.setHighlightPerDragEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setTextColor(Color.parseColor("#222222"));
        leftAxis.setAxisMinimum(-1.0f); // this replaces setStartAtZero(true)
        leftAxis.setAxisMaximum(1.0f); // this replaces setStartAtZero(true)
        leftAxis.setLabelCount(11, true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(-90);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.parseColor("#585858"));
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return labels.get((int) value);
            }
        });
        xAxis.setLabelCount(13);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        LineDataSet dataSet1 = generatePhysicalLine(physicalData);
        LineDataSet dataSet2 = generateEmotionalLine(emotionData);
        LineDataSet dataSet3 = generateIntellectualLine(intellectData);
        dataSet1.setHighlightEnabled(true);
        dataSet1.setHighLightColor(Color.BLACK);
        dataSet1.setDrawHighlightIndicators(true);

        dataSets.add(dataSet1);
        dataSets.add(dataSet2);
        dataSets.add(dataSet3);
        chart.setData(new LineData(dataSets));
        if(!isMain){
            chart.zoom(270, 1, 0, 0);
            chart.moveViewToX(index-7);
        }else {
            drawInfoView(physicalData, emotionData, intellectData, labels);
        }
        chart.highlightValue(index, 0, 0);
        chart.invalidate();

    }

    public void drawInfoView(ArrayList<Entry> physicalData, ArrayList<Entry> emotionData, ArrayList<Entry> intellectData, ArrayList<String> labels){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mother.removeAllViews();

        float phySum = 0;
        float emoSum = 0;
        float intellSum = 0;


        for(int i = 0; i < labels.size(); i++){
            View view = inflater.inflate(R.layout.info_layout, null, false);
            TextView tv_date = view.findViewById(R.id.tv_date);
            TextView tv_phy = view.findViewById(R.id.tv_phy);
            TextView tv_emo = view.findViewById(R.id.tv_emo);
            TextView tv_intell = view.findViewById(R.id.tv_intell);
            tv_date.setText(labels.get(i));
            tv_phy.setText(String.format("%.2f", physicalData.get(i).getY()));
            tv_emo.setText(String.format("%.2f", emotionData.get(i).getY()));
            tv_intell.setText(String.format("%.2f", intellectData.get(i).getY()));
            if(i > 0){
            phySum +=  Math.abs(physicalData.get(i).getY() - physicalData.get(i-1).getY());
            emoSum +=  Math.abs(emotionData.get(i).getY() - emotionData.get(i-1).getY());
            intellSum +=  Math.abs(intellectData.get(i).getY() - intellectData.get(i-1).getY());
            }

            mother.addView(view);
        }
        View view = inflater.inflate(R.layout.info_layout, null, false);
        TextView tv_date = view.findViewById(R.id.tv_date);
        TextView tv_phy = view.findViewById(R.id.tv_phy);
        TextView tv_emo = view.findViewById(R.id.tv_emo);
        TextView tv_intell = view.findViewById(R.id.tv_intell);
        tv_date.setText("변동 횟수");

        tv_phy.setText(String.format("%.2f", phySum));
        tv_emo.setText(String.format("%.2f", emoSum));
        tv_intell.setText(String.format("%.2f", intellSum));
        tv_sum.setText(String.format("%.2f", phySum + emoSum + intellSum));
        mother.addView(view);
    }


    public ArrayList<Entry> getPhysicalData(Calendar birth, int year, int month, int day, boolean isMain){
        Calendar current = new GregorianCalendar(year, month, day);
        ArrayList<Entry> entries = new ArrayList<>();
        if(isMain){
            int cnt = 0;
            while (cnt <= 4) {
                entries.add(new Entry(entries.size(), (float) getPhysical(birth, current)));
                current.add(Calendar.DAY_OF_MONTH, 1);
                cnt++;
            }
        }else {
            current.add(Calendar.YEAR, -10);
            while (current.get(Calendar.YEAR) - year <= 10) {
                entries.add(new Entry(entries.size(), (float) getPhysical(birth, current)));
                current.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        return entries;
    }
    public ArrayList<Entry> getEmotionalData(Calendar birth, int year, int month, int day, boolean isMain){
        Calendar current = new GregorianCalendar(year, month, day);
        ArrayList<Entry> entries = new ArrayList<>();
        if(isMain){
            int cnt = 0;
            while (cnt <= 4) {
                entries.add(new Entry(entries.size(), (float) getEmotional(birth, current)));
                current.add(Calendar.DAY_OF_MONTH, 1);
                cnt++;
            }
        }else {
            current.add(Calendar.YEAR, -10);
            while (current.get(Calendar.YEAR) - year <= 10) {
                entries.add(new Entry(entries.size(), (float) getEmotional(birth, current)));
                current.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        return entries;
    }
    public ArrayList<Entry> getIntellectualData(Calendar birth, int year, int month, int day, boolean isMain){
        Calendar current = new GregorianCalendar(year, month, day);
        ArrayList<Entry> entries = new ArrayList<>();
        if(isMain){
            int cnt = 0;
            while (cnt <= 4) {
                entries.add(new Entry(entries.size(), (float) getIntellectual(birth, current)));
                current.add(Calendar.DAY_OF_MONTH, 1);
                cnt++;
            }
        }else {
            current.add(Calendar.YEAR, -10);
            while (current.get(Calendar.YEAR) - year <= 10) {
                entries.add(new Entry(entries.size(), (float) getIntellectual(birth, current)));
                current.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        return entries;
    }


    public ArrayList<String> getLabelData(int year, int month, int day, boolean isMain){
        Calendar current = new GregorianCalendar(year, month, day);
        ArrayList<String> labels = new ArrayList<>();
        if(isMain){
            index = 0;
            int cnt = 0;
            while (cnt <= 4) {
                labels.add((current.get(Calendar.MONTH)+1) + ". " + current.get(Calendar.DAY_OF_MONTH) + ".");
                current.add(Calendar.DAY_OF_MONTH, 1);
                cnt++;
            }
        }else {
            current.add(Calendar.YEAR, -10);
            while (current.get(Calendar.YEAR) - year <= 10) {
                if(current.get(Calendar.YEAR) == year && current.get(Calendar.MONTH) == month && current.get(Calendar.DAY_OF_MONTH) == day) index = labels.size();
                labels.add((current.get(Calendar.MONTH)+1) + ". " + current.get(Calendar.DAY_OF_MONTH) + ".");
                current.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        return labels;

    }

    private LineDataSet generatePhysicalLine(ArrayList<Entry> entries) {
        int color = Color.parseColor("#3949AB");
        LineDataSet set = new LineDataSet(entries, "Physical");
        set.setColor(color);
        set.setDrawCircles(false);
        set.setLineWidth(2f);
        set.setFillColor(color);
        set.setDrawValues(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        return set;
    }
    private LineDataSet generateEmotionalLine(ArrayList<Entry> entries) {
        int color = Color.parseColor("#E53935");
        LineDataSet set = new LineDataSet(entries, "Emotional");
        set.setColor(color);
        set.setDrawCircles(false);
        set.setLineWidth(2f);
        set.setFillColor(color);
        set.setDrawValues(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        return set;
    }
    private LineDataSet generateIntellectualLine(ArrayList<Entry> entries) {
        int color = Color.parseColor("#43A047");
        LineDataSet set = new LineDataSet(entries, "Intellectual");
        set.setColor(color);
        set.setDrawCircles(false);
        set.setLineWidth(2f);
        set.setFillColor(color);
        set.setDrawValues(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        return set;
    }


    public double getPhysical(Calendar birth, Calendar current){
        long diff = (current.getTimeInMillis() - birth.getTimeInMillis()) / 1000;
        long day = diff / (24*60*60);
        return Math.round(Math.sin((2*Math.PI*day)/23f)*100)/100.0;
    }
    public double getEmotional(Calendar birth, Calendar current){
        long diff = (current.getTimeInMillis() - birth.getTimeInMillis()) / 1000;
        long day = diff / (24*60*60);
        return Math.round(Math.sin((2*Math.PI*day)/28f)*100)/100.0;
    }
    public double getIntellectual(Calendar birth, Calendar current){
        long diff = (current.getTimeInMillis() - birth.getTimeInMillis()) / 1000;
        long day = diff / (24*60*60);
        return Math.round(Math.sin((2*Math.PI*day)/33f)*100)/100.0;
    }

}