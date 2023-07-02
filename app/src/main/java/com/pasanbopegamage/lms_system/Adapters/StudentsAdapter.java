package com.pasanbopegamage.lms_system.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pasanbopegamage.lms_system.Filters.StudentsFilter;
import com.pasanbopegamage.lms_system.Models.StudentsModel;
import com.pasanbopegamage.lms_system.R;
import com.pasanbopegamage.lms_system.databinding.RawStudentsBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.ViewHolder> implements Filterable {

    private Context context;
    public ArrayList<StudentsModel> studentsModelArrayList, filterList;
    private StudentsFilter filter;
    private String selectedStatus;;

    public StudentsAdapter(Context context, ArrayList<StudentsModel> studentsModelArrayList) {
        this.context = context;
        this.studentsModelArrayList = studentsModelArrayList;
        this.filterList = studentsModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.raw_students, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        StudentsModel studentsModel = studentsModelArrayList.get(position);
        String image = studentsModel.getStudentImage();


        try {
            Picasso.get().load(image).placeholder(R.drawable.man).into(holder.binding.profilePic);
        } catch (Exception e) {
            holder.binding.profilePic.setImageResource(R.drawable.man);
        }

        holder.binding.nameTv.setText("Name :" + " " + studentsModel.getStudentName());
        holder.binding.emailTv.setText("Email :" + " " + studentsModel.getStudentEmail());
        holder.binding.studentIdTv.setText("Id :" + " " + studentsModel.getStudentId());
        holder.binding.studentStatusTv.setText("Status :" + " " + studentsModel.getAccountStatus());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                bottomSheetDialog.setContentView(R.layout.dialog_update_student);

                String[] status = {"pending","approve","suspend"};

                TextView typeTv = bottomSheetDialog.findViewById(R.id.typeTv);
                TextView statusTv = bottomSheetDialog.findViewById(R.id.statusTv);
                TextView NameTv = bottomSheetDialog.findViewById(R.id.NameTv);
                CircleImageView ProfilePic = bottomSheetDialog.findViewById(R.id.ProfilePic);
                TextView updateBtn = bottomSheetDialog.findViewById(R.id.updateBtn);
                ImageButton closeBtn = bottomSheetDialog.findViewById(R.id.closeBtn);
                Spinner spinnerBtn = bottomSheetDialog.findViewById(R.id.spinnerBtn);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,R.layout.item_spinner,status);
                adapter.setDropDownViewResource(R.layout.item_spinner);
                spinnerBtn.setAdapter(adapter);

                NameTv.setText(studentsModel.getStudentName());
                typeTv.setText("User type :"+" "+studentsModel.getUserType());
                statusTv.setText("Account status :"+" "+studentsModel.getAccountStatus());

                try {
                    Picasso.get().load(image).placeholder(R.drawable.man).into(ProfilePic);
                } catch (Exception e) {
                    ProfilePic.setImageResource(R.drawable.man);
                }

                bottomSheetDialog.show();

                spinnerBtn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedStatus = adapterView.getItemAtPosition(i).toString();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                updateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateAccountStatus(studentsModel,bottomSheetDialog);
                    }

                    private void updateAccountStatus(StudentsModel studentsModel, BottomSheetDialog bottomSheetDialog) {

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("accountStatus",selectedStatus);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                        databaseReference.child(studentsModel.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                bottomSheetDialog.dismiss();
                                Toast.makeText(context, "status updated", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentsModelArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new StudentsFilter(this, filterList);
        }
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RawStudentsBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RawStudentsBinding.bind(itemView);
        }
    }
}
