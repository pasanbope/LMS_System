package com.pasanbopegamage.lms_system.Adapters;

import android.content.Context;
import android.content.DialogInterface;
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
import com.pasanbopegamage.lms_system.Models.ScheduleDatesModel;
import com.pasanbopegamage.lms_system.R;
import com.pasanbopegamage.lms_system.databinding.RawShowDatesBinding;

import java.util.ArrayList;

public class ScheduledDateAdapter extends RecyclerView.Adapter<ScheduledDateAdapter.ViewHolder> {

    private Context context;
    ArrayList<ScheduleDatesModel> scheduleDatesModelArrayList;
    DatabaseReference dateRef;

    public ScheduledDateAdapter(Context context, ArrayList<ScheduleDatesModel> scheduleDatesModelArrayList) {
        this.context = context;
        this.scheduleDatesModelArrayList = scheduleDatesModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.raw_show_dates,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ScheduleDatesModel model = scheduleDatesModelArrayList.get(position);

        loadLectureName(model.getUid(),holder);

        holder.binding.dateTv.setText(model.getDate());
        holder.binding.timeTv.setText(model.getTime());
        holder.binding.locationTv.setText(model.getLocation());

        if (model.getUid().equals(FirebaseAuth.getInstance().getUid())){

            holder.binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete")
                            .setMessage("Are you sure want to delete scheduled date")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteDate(model,holder);
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
        }
        else {
            holder.binding.deleteBtn.setVisibility(View.GONE);
        }
    }

    private void deleteDate(ScheduleDatesModel model, ViewHolder holder) {
        String id = model.getId();
        dateRef = FirebaseDatabase.getInstance().getReference("ScheduleDates");
        dateRef.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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
                holder.binding.nameTv.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return scheduleDatesModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RawShowDatesBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = RawShowDatesBinding.bind(itemView);
        }
    }
}
