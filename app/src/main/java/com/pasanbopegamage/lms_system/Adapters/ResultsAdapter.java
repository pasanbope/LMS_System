package com.pasanbopegamage.lms_system.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pasanbopegamage.lms_system.Models.ResultsModel;
import com.pasanbopegamage.lms_system.R;
import com.pasanbopegamage.lms_system.databinding.RawShowResultsBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {

    private Context context;
    ArrayList<ResultsModel> resultsModelArrayList;
    DatabaseReference resultRef;

    public ResultsAdapter(Context context, ArrayList<ResultsModel> resultsModelArrayList) {
        this.context = context;
        this.resultsModelArrayList = resultsModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.raw_show_results,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ResultsModel model = resultsModelArrayList.get(position);

        holder.binding.typeTv.setText(model.getResultType()+" Results");

        loadLectureName(model.getUid(),holder);

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(model.getTimestamp());
        //format timestamp to dd/MM/yyyy
        String date = DateFormat.format("dd/MM/yyyy",calendar).toString();

        holder.binding.dateTv.setText(date);
        holder.binding.descriptionTv.setText(model.getResultDescription());

        convertExcelUrlToName(model.getExcelUrl(),holder);

        holder.binding.excelUrlTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getExcelUrl()));
                context.startActivity(browserIntent);
            }
        });


        if (model.getUid().equals(FirebaseAuth.getInstance().getUid())){

            holder.binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete")
                            .setMessage("Are you sure want to delete this result")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteResult(model,holder);
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

    private void deleteResult(ResultsModel model, ViewHolder holder) {
        String id = model.getId();
        resultRef = FirebaseDatabase.getInstance().getReference("Results");
        resultRef.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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

    private void convertExcelUrlToName(String excelUrl, ViewHolder holder) {

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(excelUrl);
        String name = storageReference.getName();
        holder.binding.excelUrlTv.setText(name);
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
        return resultsModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RawShowResultsBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = RawShowResultsBinding.bind(itemView);
        }
    }
}
