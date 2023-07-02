package com.pasanbopegamage.lms_system.Fragments;

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
import com.pasanbopegamage.lms_system.Adapters.LecturesAdapter;
import com.pasanbopegamage.lms_system.Models.LectureModel;
import com.pasanbopegamage.lms_system.R;
import com.pasanbopegamage.lms_system.databinding.FragmentAdminShowLecturesBinding;
import com.pasanbopegamage.lms_system.databinding.FragmentAdminShowStudentsBinding;

import java.util.ArrayList;


public class Admin_Show_Lectures_Fragment extends Fragment {


    FragmentAdminShowLecturesBinding binding;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    private LecturesAdapter lecturesAdapter;
    private ArrayList<LectureModel> lectureModelArrayList;

    public Admin_Show_Lectures_Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminShowLecturesBinding.inflate(inflater, container, false);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");

        loadLectures();

        binding.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    lecturesAdapter.getFilter().filter(charSequence);
                } catch (Exception e) {
                    loadLectures();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    lecturesAdapter.getFilter().filter(editable);
                } catch (Exception e) {
                    loadLectures();
                }
            }
        });

        return binding.getRoot();
    }

    private void loadLectures() {

        lectureModelArrayList = new ArrayList<>();

        databaseReference.orderByChild("userType").equalTo("lecture").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lectureModelArrayList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {

                            LectureModel lectureModel = ds.getValue(LectureModel.class);
                            lectureModelArrayList.add(lectureModel);

                        lecturesAdapter = new LecturesAdapter(getContext(), lectureModelArrayList);
                        binding.lectureRecycleView.setAdapter(lecturesAdapter);
                        lecturesAdapter.notifyDataSetChanged();
                    }

                } else {
                    binding.lectureTv.setText("no data found!!!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}