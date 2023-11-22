package com.example.biorhythm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    private User user;
    private int REQUEST_CODE = 97;
    private ImageView iv_profile;
    TextInputEditText name_edit;
    DatePicker datePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        iv_profile = findViewById(R.id.profile);
        name_edit = findViewById(R.id.name_edit);
        datePicker = findViewById(R.id.datePicker2);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        int index = intent.getIntExtra("index", -1);
        if(user == null){
            user = new User();
            user.setColor(Color.parseColor("#1E88E5"));
            iv_profile.setImageResource(R.drawable.profile);
            iv_profile.setColorFilter(user.getColor());
        }else {
            if(user.getImage() != null){
                iv_profile.setImageURI(user.getImage());
                iv_profile.setColorFilter(null);
            }else {
                iv_profile.setImageResource(R.drawable.profile);
                iv_profile.setColorFilter(user.getColor());
            }
            name_edit.setText(user.name);
            datePicker.updateDate(user.getBirthday().get(Calendar.YEAR), user.getBirthday().get(Calendar.MONTH), user.getBirthday().get(Calendar.DAY_OF_MONTH));
        }

        RelativeLayout btn_profile = findViewById(R.id.btn_profile);
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.getImage() != null) showDialog();
                else showProfileDialog();
            }
        });
        Button apply = findViewById(R.id.btn_apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index == -1){
                    saveUser(user, index);
                }else {
                    saveUser(user, index);
                }

            }
        });
    }

    public void showProfileDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        View view = LayoutInflater.from(UserActivity.this).inflate(R.layout.choose_profile_dialog, (LinearLayout) findViewById(R.id.dialog));
        builder.setView(view);
        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();

        Button blue = view.findViewById(R.id.blue);
        Button red = view.findViewById(R.id.red);
        Button green = view.findViewById(R.id.green);
        Button orange = view.findViewById(R.id.orange);
        Button profile = view.findViewById(R.id.change);

        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setColor(Color.parseColor("#1E88E5"));
                if(user.getImage() == null) iv_profile.setColorFilter(user.getColor());
                alertDialog.dismiss();
            }
        });
        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setColor(Color.parseColor("#D81B60"));
                if(user.getImage() == null) iv_profile.setColorFilter(user.getColor());
                alertDialog.dismiss();
            }
        });
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setColor(Color.parseColor("#43A047"));
                if(user.getImage() == null) iv_profile.setColorFilter(user.getColor());
                alertDialog.dismiss();
            }
        });
        orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setColor(Color.parseColor("#FB8C00"));
                if(user.getImage() == null) iv_profile.setColorFilter(user.getColor());
                alertDialog.dismiss();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_CODE);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        View view = LayoutInflater.from(UserActivity.this).inflate(R.layout.profile_dialog, (LinearLayout) findViewById(R.id.dialog));
        builder.setView(view);
        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();

        Button profile = view.findViewById(R.id.change);
        Button remove = view.findViewById(R.id.remove);


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_CODE);
                alertDialog.dismiss();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 user.setImage(null);
                 iv_profile.setImageResource(R.drawable.profile);
                 iv_profile.setColorFilter(user.getColor());
                 alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            user.setImage(selectedImageUri.toString());
            iv_profile.setColorFilter(null);
            iv_profile.setImageURI(selectedImageUri);
        }
    }



    public void saveUser(User user, int index){
        SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
        String p = sp.getString("data", "");
        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            jsonObject = new JSONObject(p);
            jsonArray = jsonObject.getJSONArray("arr");
        } catch (JSONException e) {
            jsonObject = new JSONObject();
            jsonArray = new JSONArray();
        }

        user.setName(name_edit.getText().toString());
        user.setBirthday(new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth()));
        Gson gson = new GsonBuilder().create();
        String data = gson.toJson(user, User.class);
        try {
            if(index != -1){
                jsonArray.put(index, new JSONObject(data));
            }else {
                jsonArray.put(new JSONObject(data));
            }
            jsonObject.put("arr", jsonArray);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("data",jsonObject.toString());
            editor.apply();
            setResult(93);
            finish();
        } catch (JSONException e) {
            Toast.makeText(UserActivity.this, "데이터 저장 실패", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}