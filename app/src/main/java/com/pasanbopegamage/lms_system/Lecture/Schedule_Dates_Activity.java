package com.pasanbopegamage.lms_system.Lecture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pasanbopegamage.lms_system.R;
import com.pasanbopegamage.lms_system.databinding.ActivityScheduleDatesBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Schedule_Dates_Activity extends AppCompatActivity {

    ActivityScheduleDatesBinding binding;
    private DatePickerDialog picker;
    private int Hour, Minute;
    private ProgressDialog progressDialog;

    private ArrayList<String> moduleNameArrayList, moduleIdArrayList;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    DatabaseReference databaseReference,scheduleDateRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScheduleDatesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Modules");
        scheduleDateRef = database.getReference("ScheduleDatesModel");

        loadModuleNames();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.lectureDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //date picker dialog
                picker = new DatePickerDialog(Schedule_Dates_Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        binding.lectureDateTv.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        binding.lectureTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(Schedule_Dates_Activity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Hour = hourOfDay;
                        Minute = minute;

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0, 0, 0, Hour, Minute);
                        binding.lectureTimeTv.setText(DateFormat.format("hh:mm aa", calendar));
                    }
                }, 12, 0, false
                );

                timePickerDialog.updateTime(Hour, Minute);
                timePickerDialog.show();
            }
        });

        binding.moduleNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModuleNamePickDialog();
            }
        });

        binding.scheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });


    }

    private void loadModuleNames() {

        moduleNameArrayList = new ArrayList<>();
        moduleIdArrayList = new ArrayList<>();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                moduleNameArrayList.clear();
                moduleIdArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    String moduleId = "" + ds.child("id").getValue();
                    String moduleTitle = "" + ds.child("module").getValue();

                    moduleNameArrayList.add(moduleTitle);
                    moduleIdArrayList.add(moduleId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String selectedModuleId, selectedModuleTitle;

    private void ModuleNamePickDialog() {
        String[] ModuleNameArray = new String[moduleNameArrayList.size()];
        for (int i = 0; i < moduleNameArrayList.size(); i++) {
            ModuleNameArray[i] = moduleNameArrayList.get(i);

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Module").setItems(ModuleNameArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedModuleTitle = moduleNameArrayList.get(which);
                selectedModuleId = moduleIdArrayList.get(which);
                binding.moduleNameTv.setText(selectedModuleTitle);
            }
        }).show();
    }

    private String date = "", location = "", time = "";

    private void validateData() {

        date = binding.lectureDateTv.getText().toString().trim();
        location = binding.locationEt.getText().toString().trim();
        time = binding.lectureTimeTv.getText().toString().trim();

        if (TextUtils.isEmpty(date)){
            Toast.makeText(this, "select date", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(location)){
            binding.locationEt.setError("lecture hall required");
        }
        else if (TextUtils.isEmpty(selectedModuleTitle)){
            Toast.makeText(this, "Select module name...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(date)){
            Toast.makeText(this, "select time", Toast.LENGTH_SHORT).show();
        }
        else {
            scheduleDate();
        }
    }

    private void scheduleDate() {

        progressDialog.setMessage("saving date");
        progressDialog.show();

        String uid = mAuth.getUid();
        long timestamp = System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("id",""+timestamp);
        hashMap.put("date",date);
        hashMap.put("location",location);
        hashMap.put("time",time);
        hashMap.put("moduleId",""+selectedModuleId);
        hashMap.put("moduleName",""+selectedModuleTitle);
        hashMap.put("timestamp",timestamp);

        scheduleDateRef.child(""+timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                clearData();
                Toast.makeText(Schedule_Dates_Activity.this, "Scheduled date", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Schedule_Dates_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearData() {

        binding.lectureDateTv.setText("");
        binding.locationEt.setText("");
        binding.moduleNameTv.setText("");
        binding.lectureTimeTv.setText("");
    }
}