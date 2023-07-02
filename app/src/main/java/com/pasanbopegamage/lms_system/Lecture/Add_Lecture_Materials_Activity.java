package com.pasanbopegamage.lms_system.Lecture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pasanbopegamage.lms_system.Internet_Check;
import com.pasanbopegamage.lms_system.databinding.ActivityAddLectureMaterialsBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Add_Lecture_Materials_Activity extends AppCompatActivity {

    ActivityAddLectureMaterialsBinding binding;

    private static final int PDF_PICk_CODE = 1000;
    private Uri pdfUri = null,pdfUrl = null;
    private ProgressDialog progressDialog;
    ArrayList<Uri> pdfList = new ArrayList<Uri>();
    private ArrayList<String> moduleNameArrayList,moduleIdArrayList;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    FirebaseStorage storage;
    DatabaseReference databaseReference,materialsRef;
    private DatePickerDialog picker;
    private int upload_count = 0;
    private long timestamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddLectureMaterialsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Modules");
        materialsRef = database.getReference().child("ModuleMaterials");
        storage = FirebaseStorage.getInstance();

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

        binding.lectureDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //date picker dialog
                picker = new DatePickerDialog(Add_Lecture_Materials_Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        binding.lectureDateTv.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        binding.uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkInternet Connection
                if (!Internet_Check.isConnected(Add_Lecture_Materials_Activity.this)){

                    Internet_Check.showCustomDialog(Add_Lecture_Materials_Activity.this);
                }else {
                    validateData();
                }

            }
        });
    }

    private String lectureTitle="",moduleDescription="",lectureDate="";
    private void validateData() {


        lectureTitle = binding.moduleEt.getText().toString().trim();
        moduleDescription = binding.moduleDescriptionEt.getText().toString().trim();
        lectureDate = binding.lectureDateTv.getText().toString().trim();

        if (TextUtils.isEmpty(lectureTitle)){
            binding.moduleEt.setError("Enter title");
        }
        else if (TextUtils.isEmpty(moduleDescription)){
            binding.moduleDescriptionEt.setError("Enter description");
        }
        else if (TextUtils.isEmpty(selectedModuleTitle)){
            Toast.makeText(this, "Select module name...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(lectureDate)){
            Toast.makeText(this, "Select lecture date", Toast.LENGTH_SHORT).show();
        }
        else if (pdfUri==null && pdfUrl == null){
            Toast.makeText(this, "Select PDF...", Toast.LENGTH_SHORT).show();
        }
        else {
            savingLectureMaterials();
        }
    }


    private void savingLectureMaterials() {
        progressDialog.setMessage("Saving lecture materials....");
        String uid = mAuth.getUid();

        timestamp = System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("id",""+timestamp);
        hashMap.put("lectureTitle",""+lectureTitle);
        hashMap.put("lectureDescription",""+moduleDescription);
        hashMap.put("lectureDate",""+lectureDate);
        hashMap.put("moduleId",""+selectedModuleId);
        hashMap.put("moduleName",""+selectedModuleTitle);
        hashMap.put("timestamp",timestamp);

            materialsRef.child(""+timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    uploadPdfToStorage(""+timestamp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Add_Lecture_Materials_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

    }

    private void uploadPdfToStorage(String timestamp) {

        progressDialog.setMessage("Uploading Pdf....");
        progressDialog.show();


        StorageReference pdfFolder = FirebaseStorage.getInstance().getReference().child("Module_Materials");

        for (upload_count = 0; upload_count < pdfList.size(); upload_count++) {

            Uri IndividualPdf = pdfList.get(upload_count);
            StorageReference pdfName = pdfFolder.child("PDF" + IndividualPdf.getLastPathSegment());

            pdfName.putFile(IndividualPdf).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pdfName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = String.valueOf(uri);
                            StorePdf(url,timestamp);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(Add_Lecture_Materials_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void StorePdf(String url, String timestamp) {

        progressDialog.setMessage("Saving lecture materials....");
        String uid = mAuth.getUid();

        long newTimestamp = System.currentTimeMillis();

        HashMap<String, Object> pdfMap = new HashMap<>();
        pdfMap.put("pdfUrl", url);

        materialsRef = database.getReference().child("ModuleMaterials");
        materialsRef.child(timestamp).child("PDFLinks").child(""+newTimestamp).setValue(pdfMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                clearData();
                Toast.makeText(Add_Lecture_Materials_Activity.this, "Lecture Material Added Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Add_Lecture_Materials_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearData() {
        binding.moduleEt.setText("");
        binding.moduleDescriptionEt.setText("");
        binding.moduleNameTv.setText("");
        binding.lectureDateTv.setText("");
        pdfUri = null;
        pdfUrl = null;
        binding.text.setText("Enter module details to continue");
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
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PDF_PICk_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data.getClipData() != null) {

                int countClipData = data.getClipData().getItemCount();
                int currentPdfSelect = 0;
                while (currentPdfSelect < countClipData) {

                    pdfUri = data.getClipData().getItemAt(currentPdfSelect).getUri();
                    pdfList.add(pdfUri);
                    currentPdfSelect = currentPdfSelect + 1;

                }
                binding.text.setText("You have Selected " + pdfList.size() + " PDf files");
            }
            //if select one file
            else if (data.getData() != null) {
                pdfUrl = data.getData();
                pdfList.add(pdfUrl);
                binding.text.setText("You No Selected " + pdfList.size() + " PDf files");
            }

        }
    }
}