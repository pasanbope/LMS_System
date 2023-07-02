package com.pasanbopegamage.lms_system.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pasanbopegamage.lms_system.Adapters.ModuleAdapter;
import com.pasanbopegamage.lms_system.Models.ModuleModel;
import com.pasanbopegamage.lms_system.R;
import com.pasanbopegamage.lms_system.Splash_Activity;
import com.pasanbopegamage.lms_system.databinding.ActivityStudentDashboardBinding;

import java.util.ArrayList;

public class Student_Dashboard_Activity extends AppCompatActivity {

    ActivityStudentDashboardBinding binding;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference, moduleRef;

    ArrayList<ModuleModel> moduleModelArrayList;
    ModuleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");
        moduleRef = database.getReference("Modules");

        loadStudentDetails();

        loadModules("student");

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        binding.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapter.getFilter().filter(charSequence);
                } catch (Exception e) {
                    loadModules("student");
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    adapter.getFilter().filter(editable);
                } catch (Exception e) {
                    loadModules("student");
                }
            }
        });
    }

    private void showLogoutDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Student_Dashboard_Activity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(Student_Dashboard_Activity.this).inflate(R.layout.layout_warning_dialog, (ConstraintLayout) findViewById(R.id.layoutDialogContainer)

        );

        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitile)).
                setText(getResources().getString(R.string.warning_title));
        ((TextView) view.findViewById(R.id.textMessage)).
                setText(getResources().getString(R.string.dummy_text));
        ((TextView) view.findViewById(R.id.buttonYes)).
                setText(getResources().getString(R.string.yes));
        ((TextView) view.findViewById(R.id.buttonNo)).
                setText(getResources().getString(R.string.no));

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LogOut();
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void LogOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(Student_Dashboard_Activity.this, Splash_Activity.class));
        finish();
    }

    private void loadModules(String userType) {

        moduleModelArrayList = new ArrayList<>();

        moduleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                moduleModelArrayList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ModuleModel moduleModel = ds.getValue(ModuleModel.class);
                        moduleModelArrayList.add(moduleModel);
                    }
                    adapter = new ModuleAdapter(Student_Dashboard_Activity.this, moduleModelArrayList, userType);
                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    binding.moduleRv.setLayoutManager(staggeredGridLayoutManager);
                    binding.moduleRv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadStudentDetails() {
        databaseReference.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = "" + snapshot.child("studentEmail").getValue();
                binding.emailTv.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser == null) {
            //not logged in
            binding.emailTv.setText("Not Logged In");

        } else {
            checkUserStatus();

        }
    }

    private void checkUserStatus() {
        databaseReference.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String accountStatus = "" + snapshot.child("accountStatus").getValue();

                if (accountStatus.equals("pending")) {
                    LogOut();
                } else if (accountStatus.equals("suspend")) {
                    LogOut();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}