package com.pasanbopegamage.lms_system.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.pasanbopegamage.lms_system.databinding.ActivityAddLecturesBinding;

import java.util.HashMap;

public class Add_Lectures_Activity extends AppCompatActivity {

    ActivityAddLecturesBinding binding;

    private String lectureEmail="",lectureName="",password="",lectureId="";
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseStorage storage;

    private String[] cameraPermissions;
    private String[] storagePermissions;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    private Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddLecturesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cameraPermissions = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        databaseReference = database.getReference("Users");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait........");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.selectPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateLecture();
            }
        });
    }

    private void validateLecture() {
        if(!validateEmail() | !validateLectureName() | !validateLectureId() | !validatePassword() | !validateLecturePic()){
            return;
        }
        createLectureAccount();
    }

    private void createLectureAccount() {
        progressDialog.setMessage("Creating Account....");
        progressDialog.show();


        mAuth.createUserWithEmailAndPassword(lectureEmail,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                updateLectureInfo();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Add_Lectures_Activity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLectureInfo() {

        progressDialog.setMessage("Lecture Data Updating with Image");
        String filePathName = "Lectures_Image/" + "" + mAuth.getUid();

        long timestamp = System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("lectureName", lectureName);
        hashMap.put("lectureEmail", lectureEmail);
        hashMap.put("lectureId", lectureId);
        hashMap.put("uid",mAuth.getUid());
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("userType", "lecture");
        hashMap.put("accountStatus", "approve");

        storage.getReference(filePathName).putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                Uri downloadImageUri = uriTask.getResult();

                if (uriTask.isSuccessful()) {

                    hashMap.put("lectureImage", downloadImageUri.toString());

                    databaseReference.child(mAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(Add_Lectures_Activity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                            clearData();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Add_Lectures_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Add_Lectures_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void clearData() {

        binding.emailEt.setText("");
        binding.nameEt.setText("");
        binding.lectureIdEt.setText("");
        binding.passwordEt.setText("");
        image_uri = null;
        binding.profileIv.setImageResource(0);
    }

    private void showImagePickerDialog() {
        String[] options = {"camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Add_Lectures_Activity.this);
        builder.setTitle("Pick Image").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (checkCameraPermissions()) {
                        pickFromCamera();
                    } else {
                        requestCameraPermission();
                    }
                } else {
                    if (checkStoragePermission()) {
                        pickFromGallery();

                    } else {
                        requestStoragePermission();
                    }

                }
            }
        });
        builder.show();
    }

    private void pickFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image_Description");

        image_uri = Add_Lectures_Activity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(Add_Lectures_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(Add_Lectures_Activity.this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions() {

        boolean result = ContextCompat.checkSelfPermission(Add_Lectures_Activity.this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(Add_Lectures_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;

    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(Add_Lectures_Activity.this, cameraPermissions, CAMERA_REQUEST_CODE);
    }


    private Boolean validatePassword() {
        password = binding.passwordEt.getText().toString().trim();

        if (password.isEmpty()) {
            binding.passwordTv.setError("Field cannot be empty");
            return false;
        } else {
            binding.passwordTv.setError(null);
            binding.passwordTv.setErrorEnabled(false);
            return true;
        }

    }

    private Boolean validateLectureId() {
        lectureId = binding.lectureIdEt.getText().toString().trim();

        if (lectureId.isEmpty()) {
            binding.studentIdTv.setError("Field cannot be empty");
            return false;

        } else {
            binding.studentIdTv.setError(null);
            binding.studentIdTv.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateLecturePic() {

        if (image_uri == null) {
            Toast.makeText(this, "please select an image", Toast.LENGTH_SHORT).show();
            return false;

        } else {
            return true;
        }
    }

    private Boolean validateLectureName() {

        lectureName = binding.nameEt.getText().toString();

        if (lectureName.isEmpty()) {
            binding.nameTv.setError("Field cannot be empty");
            return false;
        } else if (lectureName.length() >= 15) {
            binding.nameTv.setError("Username too long");
            return false;
        } else {
            binding.nameTv.setError(null);
            binding.nameTv.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail() {
        lectureEmail = binding.emailEt.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (lectureEmail.isEmpty()) {
            binding.emailTv.setError("Field cannot be empty");
            return false;
        } else if (!lectureEmail.matches(emailPattern)) {
            binding.emailTv.setError("Invalid email address");
            return false;
        } else {
            binding.emailTv.setError(null);
            binding.emailTv.setErrorEnabled(false);
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {

                image_uri = data.getData();
                binding.profileIv.setImageURI(image_uri);

            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {

                binding.profileIv.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}