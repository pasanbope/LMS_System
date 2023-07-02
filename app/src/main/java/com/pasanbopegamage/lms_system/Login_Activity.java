package com.pasanbopegamage.lms_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pasanbopegamage.lms_system.Admin.Admin_Dashboard_Activity;
import com.pasanbopegamage.lms_system.Lecture.Lecture_Dashboard_Activity;
import com.pasanbopegamage.lms_system.Student.Student_Dashboard_Activity;
import com.pasanbopegamage.lms_system.databinding.ActivityLoginBinding;

public class Login_Activity extends AppCompatActivity {

    ActivityLoginBinding binding;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    private String emailEt = "", passwordEt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        binding.signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        binding.forgotPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_Activity.this, Forgot_Password_Activity.class));
            }
        });

        binding.signupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_Activity.this, Students_Register_Activity.class));
            }
        });

    }

    private void validateData() {

        if (!validateEmail() | !validatePassword()) {
            return;
        }
        loginUser();
    }

    private void loginUser() {

        progressDialog.setMessage("Logging In....");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(emailEt, passwordEt).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                checkUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Login_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUser() {

        progressDialog.setMessage("Checking User.....");


        databaseReference.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userType = "" + snapshot.child("userType").getValue();
                String accountStatus = "" + snapshot.child("accountStatus").getValue();

                switch (userType) {
                    case "student":
                        checkAccountStatus(accountStatus, userType);
                        break;
                    case "lecture":
                        progressDialog.dismiss();
                        startActivity(new Intent(Login_Activity.this, Lecture_Dashboard_Activity.class));
                        finish();
                        break;
                    case "admin":
                        progressDialog.dismiss();
                        startActivity(new Intent(Login_Activity.this, Admin_Dashboard_Activity.class));
                        finish();
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkAccountStatus(String accountStatus, String userType) {

        switch (accountStatus) {
            case "pending":
                progressDialog.dismiss();
                Toast.makeText(this, "Your account need to approve.Please be patient", Toast.LENGTH_SHORT).show();
                break;
            case "suspend":
                progressDialog.dismiss();
                Toast.makeText(this, "Your account is suspended.Please contact IT department", Toast.LENGTH_SHORT).show();
                break;
            case "approve":
                checkUserType(userType);
                break;
        }
    }

    private void checkUserType(String userType) {

        if (userType.equals("student")) {
            progressDialog.dismiss();
            startActivity(new Intent(Login_Activity.this, Student_Dashboard_Activity.class));
            finish();
        } else if (userType.equals("teacher")) {
            progressDialog.dismiss();
            startActivity(new Intent(Login_Activity.this, Lecture_Dashboard_Activity.class));
            finish();
        }

    }

    private Boolean validateEmail() {
        emailEt = binding.emailEt.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (emailEt.isEmpty()) {
            binding.emailTv.setError("Field cannot be empty");
            return false;
        } else if (!emailEt.matches(emailPattern)) {
            binding.emailTv.setError("Invalid email address");
            return false;
        } else {
            binding.emailTv.setError(null);
            binding.emailTv.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        passwordEt = binding.passwordEt.getText().toString().trim();

        if (passwordEt.isEmpty()) {
            binding.passwordTv.setError("Field cannot be empty");
            return false;
        } else {
            binding.passwordTv.setError(null);
            binding.passwordTv.setErrorEnabled(false);
            return true;
        }

    }
}