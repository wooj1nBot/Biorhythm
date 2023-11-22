package com.example.biorhythm;

import static com.example.biorhythm.BiorhythmActivity.saveDay;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    List<User> users = new ArrayList<>();
    RecyclerView rc;
    UserAdopter userAdopter;
    Button btn_ref;
    private Parcelable recyclerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        rc = findViewById(R.id.rc);
        Button btn_add = findViewById(R.id.btn_add);
        btn_ref = findViewById(R.id.btn_date);
        ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // Add same code that you want to add in onActivityResult method
                        if(result.getResultCode() == 93){
                            getUser(true);
                        }else {
                            getUser(false);
                        }
                    }
                });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserListActivity.this, UserActivity.class);
                startActivityIntent.launch(intent);
            }
        });

        Calendar calendar = BiorhythmActivity.getDay(UserListActivity.this, false);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 (E)");
        btn_ref.setText(dateFormat.format(calendar.getTime()));
        btn_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(UserListActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        try {
                            saveDay(UserListActivity.this, year, monthOfYear, dayOfMonth, false);
                            Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                            btn_ref.setText(dateFormat.format(calendar.getTime()));
                            getUser(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


                datePickerDialog.getDatePicker().setCalendarViewShown(false);
                datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                datePickerDialog.show();
            }
        });

        rc.setLayoutManager(new LinearLayoutManager(this));
        rc.addItemDecoration(new DividerItemDecoration(UserListActivity.this, 1));
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                 int pos = viewHolder.getAdapterPosition();
                 if(direction == ItemTouchHelper.LEFT){
                     User user = users.get(pos);
                     users.remove(pos);
                     removeUser(pos);
                     userAdopter.notifyItemRemoved(pos);
                     Snackbar.make(rc, user.name, Snackbar.LENGTH_LONG).setAction("복구", new View.OnClickListener() {
                         @Override
                         public void onClick(View view) {
                             users.add(pos, user);
                             saveUser(user, pos);
                             userAdopter.notifyItemInserted(pos);
                         }
                     }).show();
                 }
            }
        }).attachToRecyclerView(rc);


    }

    @Override
    protected void onResume() {
        super.onResume();
        getUser(false);

    }

    public void removeUser(int pos){
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

        try {
            jsonArray.remove(pos);
            jsonObject.put("arr", jsonArray);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("data",jsonObject.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getUser(boolean isadd){
        users.clear();
        recyclerViewState = rc.getLayoutManager().onSaveInstanceState();
        userAdopter = new UserAdopter(users);

        SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
        String p = sp.getString("data", "");
        JSONObject jsonObject;
        JSONArray jsonArray;
        Gson gson = new GsonBuilder().create();
        try {
            jsonObject = new JSONObject(p);
            jsonArray = jsonObject.getJSONArray("arr");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                User user = gson.fromJson(object.toString(), User.class);
                users.add(user);
            }
            rc.setAdapter(userAdopter);
            if(isadd){
                rc.scrollToPosition(users.size()-1);
            }else {
                rc.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            }
        } catch (JSONException e) {

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}