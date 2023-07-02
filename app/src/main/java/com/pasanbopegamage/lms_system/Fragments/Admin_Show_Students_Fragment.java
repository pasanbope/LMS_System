package com.pasanbopegamage.lms_system.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pasanbopegamage.lms_system.Adapters.StudentsAdapter;
import com.pasanbopegamage.lms_system.Admin.Add_Lectures_Activity;
import com.pasanbopegamage.lms_system.Models.StudentsModel;
import com.pasanbopegamage.lms_system.R;
import com.pasanbopegamage.lms_system.databinding.FragmentAdminShowStudentsBinding;

import java.util.ArrayList;


public class Admin_Show_Students_Fragment extends Fragment {

    FragmentAdminShowStudentsBinding binding;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    private StudentsAdapter studentsAdapter;
    private ArrayList<StudentsModel> studentsModelArrayList, studentsList;

    public Admin_Show_Students_Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminShowStudentsBinding.inflate(inflater, container, false);

        loadStudentRequests();

        loadStudents();

        binding.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    studentsAdapter.getFilter().filter(charSequence);
                } catch (Exception e) {
                    loadStudents();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    studentsAdapter.getFilter().filter(editable);
                } catch (Exception e) {
                    loadStudents();
                }
            }
        });

        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Add_Lectures_Activity.class));
            }
        });


        return binding.getRoot();
    }

    private void loadStudents() {

        studentsList = new ArrayList<>();

        databaseReference.orderByChild("userType").equalTo("student").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentsList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        String accountStatus = "" + ds.child("accountStatus").getValue();

                        if (!accountStatus.equals("pending")) {
                            binding.studentsTv.setVisibility(View.VISIBLE);
                            StudentsModel studentsModel = ds.getValue(StudentsModel.class);
                            studentsList.add(studentsModel);

                        } else if (studentsList.size() == 0) {
                            binding.studentsTv.setVisibility(View.GONE);
                        }

                        studentsAdapter = new StudentsAdapter(getContext(), studentsList);
                        binding.studentsRecycleView.setAdapter(studentsAdapter);
                        studentsAdapter.notifyDataSetChanged();
                    }

                } else {
                    binding.studentsTv.setText("no data found!!!");
                    binding.studentsRecycleView.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStudentRequests() {

        studentsModelArrayList = new ArrayList<>();

        databaseReference.orderByChild("userType").equalTo("student").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentsModelArrayList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        String accountStatus = "" + ds.child("accountStatus").getValue();

                        if (accountStatus.equals("pending")) {
                            binding.btitle.setVisibility(View.VISIBLE);
                            StudentsModel studentsModel = ds.getValue(StudentsModel.class);
                            studentsModelArrayList.add(studentsModel);
                        } else if (studentsModelArrayList.size() == 0) {
                            binding.btitle.setVisibility(View.GONE);
                        }
                        studentsAdapter = new StudentsAdapter(getContext(), studentsModelArrayList);
                        binding.pendingStudentsRecycleView.setAdapter(studentsAdapter);
                        studentsAdapter.notifyDataSetChanged();
                    }

                } else {
                    binding.btitle.setVisibility(View.GONE);
                    binding.pendingStudentsRecycleView.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}