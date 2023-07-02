package com.pasanbopegamage.lms_system.Lecture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pasanbopegamage.lms_system.R;
import com.pasanbopegamage.lms_system.databinding.ActivityAddMarksBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class Add_Marks_Activity extends AppCompatActivity {

    ActivityAddMarksBinding binding;
    String[] items = {"Assignment","Exams"};
    ArrayAdapter<String> adapterItems;
    private static final int PICK_EXCEL_FILE_REQUEST_CODE = 1;
    private ArrayList<String> moduleNameArrayList,moduleIdArrayList;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    FirebaseStorage storage;
    private ProgressDialog progressDialog;
    DatabaseReference databaseReference,examResultRef;
    private String examDescription="",type="";

    private Uri excelUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMarksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Modules");
        examResultRef = database.getReference().child("Results");
        storage = FirebaseStorage.getInstance();

        loadModuleNames();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        adapterItems = new ArrayAdapter<String>(Add_Marks_Activity.this, R.layout.list_item, items);
        binding.autoCT.setAdapter(adapterItems);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.autoCT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                type = parent.getItemAtPosition(position).toString();
            }
        });

        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfPickIntent();
            }
        });

        binding.moduleNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModuleNamePickDialog();
            }
        });

        binding.uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void validateData() {

        examDescription = binding.descriptionEt.getText().toString().trim();

        if (TextUtils.isEmpty(examDescription)){
            binding.descriptionEt.setError("Enter description");
        }
        else if (TextUtils.isEmpty(selectedModuleTitle)){
            Toast.makeText(this, "Select module name...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(type)){
            Toast.makeText(this, "Select Result Type...", Toast.LENGTH_SHORT).show();
        }
        else if (excelUri==null){
            Toast.makeText(this, "Select Excel File...", Toast.LENGTH_SHORT).show();
        }
        else {
            savingLectureMaterials();
        }

    }

    private void savingLectureMaterials() {

        progressDialog.setMessage("Uploading Pdf....");
        progressDialog.show();

        long timeStamp = System.currentTimeMillis();

        String filePathName = "Results/" + timeStamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
        storageReference.putFile(excelUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                while (!uriTask.isSuccessful());
                String uploadExcelUrl = ""+uriTask.getResult();

                uploadFileToStorage(uploadExcelUrl,timeStamp);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Add_Marks_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadFileToStorage(String uploadExcelUrl, long timeStamp) {


        progressDialog.setMessage("Saving Results....");
        String uid = mAuth.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("id",""+timeStamp);
        hashMap.put("resultDescription",""+examDescription);
        hashMap.put("resultType",""+type);
        hashMap.put("moduleId",""+selectedModuleId);
        hashMap.put("moduleName",""+selectedModuleTitle);
        hashMap.put("timestamp",timeStamp);
        hashMap.put("excelUrl",uploadExcelUrl);

        examResultRef.child(""+timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                clearData();
                Toast.makeText(Add_Marks_Activity.this, "Results uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Add_Marks_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void clearData() {
        binding.autoCT.setText("");
        binding.descriptionEt.setText("");
        binding.moduleNameTv.setText("");
        excelUri = null;
    }

    private void loadModuleNames() {
        moduleNameArrayList = new ArrayList<>();
        moduleIdArrayList = new ArrayList<>();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                moduleNameArrayList.clear();
                moduleIdArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){

                    String moduleId = ""+ds.child("id").getValue();
                    String moduleTitle = ""+ds.child("module").getValue();

                    moduleNameArrayList.add(moduleTitle);
                    moduleIdArrayList.add(moduleId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String selectedModuleId,selectedModuleTitle;
    private void ModuleNamePickDialog() {
        String[] ModuleNameArray = new String[moduleNameArrayList.size()];
        for (int i = 0; i< moduleNameArrayList.size(); i++){
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

    private void pdfPickIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.ms-excel"); // Filter to show only Excel files
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_EXCEL_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_EXCEL_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                // Get the URI of the selected Excel file
                excelUri = data.getData();
            }
            else {
                Toast.makeText(this, "cancelled picking pdf", Toast.LENGTH_SHORT).show();
            }
        }
    }
}