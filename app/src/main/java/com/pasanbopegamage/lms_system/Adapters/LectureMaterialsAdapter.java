package com.pasanbopegamage.lms_system.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pasanbopegamage.lms_system.Models.LectureMaterialModel;
import com.pasanbopegamage.lms_system.Models.PdfModel;
import com.pasanbopegamage.lms_system.R;
import com.pasanbopegamage.lms_system.databinding.RawLectureMaterialsBinding;

import java.util.ArrayList;

public class LectureMaterialsAdapter extends RecyclerView.Adapter<LectureMaterialsAdapter.ViewHolder> {

    private Context context;
    ArrayList<LectureMaterialModel> lectureMaterialModelArrayList;
    ArrayList<PdfModel> pdfList;
    DatabaseReference materialRef;

    public LectureMaterialsAdapter(Context context, ArrayList<LectureMaterialModel> lectureMaterialModelArrayList) {
        this.context = context;
        this.lectureMaterialModelArrayList = lectureMaterialModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.raw_lecture_materials,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        LectureMaterialModel lectureMaterialModel = lectureMaterialModelArrayList.get(position);


        holder.binding.titleTv.setText(lectureMaterialModel.getLectureTitle());
        holder.binding.descriptionTv.setText(lectureMaterialModel.getLectureDescription());
        holder.binding.dateTv.setText(lectureMaterialModel.getLectureDate());

        loadLectureName(lectureMaterialModel.getUid(),holder);
        loadPdfLinks(lectureMaterialModel.getId(),holder);

        if (lectureMaterialModel.getUid().equals(FirebaseAuth.getInstance().getUid())){
            holder.binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete")
                            .setMessage("Are you sure want to delete this material")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteMaterial(lectureMaterialModel,holder);
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });
        }else {
            holder.binding.deleteBtn.setVisibility(View.GONE);
        }



    }

    private void deleteMaterial(LectureMaterialModel lectureMaterialModel, ViewHolder holder) {
        String id = lectureMaterialModel.getId();
        materialRef = FirebaseDatabase.getInstance().getReference("ModuleMaterials");
        materialRef.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Successfully deleted.....", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLectureName(String uid, ViewHolder holder) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = ""+snapshot.child("lectureName").getValue();
                holder.binding.nameTv.setText(name);;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPdfLinks(String id, ViewHolder holder) {

        pdfList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ModuleMaterials");
        databaseReference.child(id).child("PDFLinks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pdfList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        PdfModel pdfModel = ds.getValue(PdfModel.class);
                        pdfList.add(pdfModel);
                        Log.d("DSDDS",pdfList.toString());
                    }
                    PdfAdapter pdfAdapter = new PdfAdapter(context,pdfList);
                    holder.binding.pdfListRV.setAdapter(pdfAdapter);
                    pdfAdapter.notifyDataSetChanged();
                }
                else {
                    holder.binding.pdfListRV.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return lectureMaterialModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RawLectureMaterialsBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = RawLectureMaterialsBinding.bind(itemView);
        }
    }
}
