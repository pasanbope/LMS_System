package com.pasanbopegamage.lms_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pasanbopegamage.lms_system.Admin.Admin_Dashboard_Activity;
import com.pasanbopegamage.lms_system.Lecture.Lecture_Dashboard_Activity;
import com.pasanbopegamage.lms_system.Student.Student_Dashboard_Activity;

public class Splash_Activity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");


        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    checkUser();
                }
            }
        };
        timer.start();
    }

    private void checkUser() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {

            startActivity(new Intent(Splash_Activity.this, MainActivity.class));
            finish();
        } else {

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
                            startActivity(new Intent(Splash_Activity.this, Lecture_Dashboard_Activity.class));
                            finish();
                            break;
                        case "admin":
                            startActivity(new Intent(Splash_Activity.this, Admin_Dashboard_Activity.class));
                            finish();
                            break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void checkAccountStatus(String accountStatus, String userType) {

        switch (accountStatus) {
            case "pending":
                Toast.makeText(this, "Your account need to approve.Please be patient", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Splash_Activity.this, Login_Activity.class));
                finish();
                break;
            case "suspend":
                Toast.makeText(this, "Your account is suspended.Please contact IT department", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Splash_Activity.this, Login_Activity.class));
                finish();
                break;
            case "approve":
                checkUserType(userType);
                break;
        }
    }

    private void checkUserType(String userType) {
        if (userType.equals("student")) {
            startActivity(new Intent(Splash_Activity.this, Student_Dashboard_Activity.class));
            finish();
        } else if (userType.equals("teacher")) {
            startActivity(new Intent(Splash_Activity.this, Lecture_Dashboard_Activity.class));
            finish();
        }
    }
}