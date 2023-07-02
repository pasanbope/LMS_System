package com.pasanbopegamage.lms_system.Lecture;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pasanbopegamage.lms_system.Adapters.LectureMaterialsAdapter;
import com.pasanbopegamage.lms_system.Adapters.ResultsAdapter;
import com.pasanbopegamage.lms_system.Adapters.ScheduledDateAdapter;
import com.pasanbopegamage.lms_system.Models.LectureMaterialModel;
import com.pasanbopegamage.lms_system.Models.ResultsModel;
import com.pasanbopegamage.lms_system.Models.ScheduleDatesModel;
import com.pasanbopegamage.lms_system.databinding.ActivityShowModuleDetailsBinding;

import java.util.ArrayList;


public class Show_Module_Details_Activity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1000;
    ActivityShowModuleDetailsBinding binding;
    private String moduleName, moduleId, status;
    FirebaseDatabase database;
    DatabaseReference moduleMaterialsReference, resultReference, schedulesDatesRef;

    LectureMaterialsAdapter adapter;
    ArrayList<LectureMaterialModel> lectureMaterialModelArrayList;

    ResultsAdapter resultsAdapter;
    ArrayList<ResultsModel> resultsModelArrayList;

    ScheduledDateAdapter scheduledDateAdapter;
    ArrayList<ScheduleDatesModel> scheduleDatesModelArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowModuleDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        moduleMaterialsReference = database.getReference("ModuleMaterials");
        resultReference = database.getReference("Results");
        schedulesDatesRef = database.getReference("ScheduleDates");

        moduleName = getIntent().getStringExtra("moduleName");
        moduleId = getIntent().getStringExtra("moduleId");
        status = getIntent().getStringExtra("status");



        if (getIntent().getExtras() != null) {
            binding.moduleNameTv.setText(moduleName);

            if (status.equals("Exam Results")) {
                binding.marksRv.setVisibility(View.VISIBLE);
                binding.marksRv.showShimmerAdapter();
                loadExamDetails(moduleName);
            } else if (status.equals("Lecture Dates")) {
                binding.datesRv.setVisibility(View.VISIBLE);
                binding.datesRv.showShimmerAdapter();
                loadLectureDates();
            } else if (status.equals("Lecture Materials")) {
                binding.materialsRv.setVisibility(View.VISIBLE);
                binding.materialsRv.showShimmerAdapter();
                loadLectureMaterials(moduleName);
            } else {
                binding.moduleNameTv.setText("something wrong");
            }

        } else {
            binding.moduleNameTv.setText("No data found");
        }

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadLectureDates() {

        scheduleDatesModelArrayList = new ArrayList<>();
        schedulesDatesRef.orderByChild("moduleName").equalTo(moduleName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scheduleDatesModelArrayList.clear();
                if (snapshot.exists()) {
                    binding.lectureDatesTv.setVisibility(View.VISIBLE);
                    binding.datesRv.setVisibility(View.VISIBLE);
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ScheduleDatesModel scheduleDatesModel = ds.getValue(ScheduleDatesModel.class);
                        scheduleDatesModelArrayList.add(scheduleDatesModel);
                    }
                    scheduledDateAdapter = new ScheduledDateAdapter(Show_Module_Details_Activity.this, scheduleDatesModelArrayList);
                    binding.datesRv.setAdapter(scheduledDateAdapter);
                    scheduledDateAdapter.notifyDataSetChanged();
                } else {
                    binding.datesRv.hideShimmerAdapter();
                    binding.datesRv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadExamDetails(String moduleName) {

        resultsModelArrayList = new ArrayList<>();
        resultReference.orderByChild("moduleName").equalTo(moduleName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resultsModelArrayList.clear();
                if (snapshot.exists()) {
                    binding.marksTv.setVisibility(View.VISIBLE);
                    binding.marksRv.setVisibility(View.VISIBLE);
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ResultsModel resultsModel = ds.getValue(ResultsModel.class);
                        resultsModelArrayList.add(resultsModel);
                    }
                    resultsAdapter = new ResultsAdapter(Show_Module_Details_Activity.this, resultsModelArrayList);
                    binding.marksRv.setAdapter(resultsAdapter);
                    resultsAdapter.notifyDataSetChanged();
                } else {
                    binding.marksTv.setVisibility(View.GONE);
                    binding.marksRv.hideShimmerAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadLectureMaterials(String moduleName) {


        lectureMaterialModelArrayList = new ArrayList<>();
        moduleMaterialsReference.orderByChild("moduleName").equalTo(moduleName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lectureMaterialModelArrayList.clear();
                if (snapshot.exists()) {
                    binding.materialsTv.setVisibility(View.VISIBLE);
                    binding.materialsRv.setVisibility(View.VISIBLE);
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        LectureMaterialModel model = ds.getValue(LectureMaterialModel.class);
                        lectureMaterialModelArrayList.add(model);
                    }

                    adapter = new LectureMaterialsAdapter(Show_Module_Details_Activity.this, lectureMaterialModelArrayList);
                    binding.materialsRv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    binding.materialsRv.hideShimmerAdapter();
                    binding.materialsTv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}