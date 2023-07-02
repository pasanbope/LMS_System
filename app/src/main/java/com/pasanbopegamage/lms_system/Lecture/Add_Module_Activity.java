package com.pasanbopegamage.lms_system.Lecture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pasanbopegamage.lms_system.databinding.ActivityAddModuleBinding;

import java.util.HashMap;

public class Add_Module_Activity extends AppCompatActivity {

    ActivityAddModuleBinding binding;
    private String module="";

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddModuleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Modules");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateModule();
            }
        });

    }

    private void validateModule() {
        module = binding.moduleEt.getText().toString().trim();

        if (TextUtils.isEmpty(module)){
            binding.moduleEt.setError("Please enter category....");
        }
        else {
            saveModule();
        }
    }

    private void saveModule() {

        progressDialog.setMessage("Adding Module....");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",""+timestamp);
        hashMap.put("uid",""+mAuth.getUid());
        hashMap.put("module",module);
        hashMap.put("timestamp",timestamp);

        databaseReference.child(""+timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                binding.moduleEt.setText("");
                Toast.makeText(Add_Module_Activity.this, "Module added successfully....", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Add_Module_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}