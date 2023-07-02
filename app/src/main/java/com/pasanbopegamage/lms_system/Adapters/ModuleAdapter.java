package com.pasanbopegamage.lms_system.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pasanbopegamage.lms_system.Filters.ModuleFilter;
import com.pasanbopegamage.lms_system.Lecture.Show_Module_Details_Activity;
import com.pasanbopegamage.lms_system.Models.ModuleModel;
import com.pasanbopegamage.lms_system.R;
import com.pasanbopegamage.lms_system.databinding.RowModuleBinding;

import java.util.ArrayList;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ViewHolder> implements Filterable {

    private Context context;
    public ArrayList<ModuleModel> moduleModelArrayList,filterList;
    private DatabaseReference moduleRef;
    private ModuleFilter filter;
    private String userType;


    public ModuleAdapter(Context context, ArrayList<ModuleModel> moduleModelArrayList,String userType) {
        this.context = context;
        this.moduleModelArrayList = moduleModelArrayList;
        this.filterList = moduleModelArrayList;
        this.userType = userType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_module, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ModuleModel moduleModel = moduleModelArrayList.get(position);

        holder.binding.moduleTv.setText(moduleModel.getModule());

        if (userType.equals("student")){
            holder.binding.deleteBtn.setVisibility(View.GONE);
        }else {
            holder.binding.deleteBtn.setVisibility(View.VISIBLE);
        }


        if (moduleModel.getUid().equals(FirebaseAuth.getInstance().getUid())){

            holder.binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete")
                            .setMessage("Are you sure want to delete this module")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteModule(moduleModel,holder);
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsPopup(moduleModel);
            }
        });
    }

    private void showOptionsPopup(ModuleModel moduleModel) {

        String[] options = {"Exam Results","Lecture Dates","Lecture Materials"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(moduleModel.getModule()).setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                   startActivity(moduleModel,"Exam Results");
                }
                else if (i == 1){
                    startActivity(moduleModel,"Lecture Dates");
                }
                else {
                    startActivity(moduleModel,"Lecture Materials");
                }
            }
        });
        builder.show();
    }

    private void startActivity(ModuleModel moduleModel,String status) {
        Intent intent = new Intent(context, Show_Module_Details_Activity.class);
        intent.putExtra("moduleName",moduleModel.getModule());
        intent.putExtra("moduleId",moduleModel.getId());
        intent.putExtra("status",status);
        context.startActivity(intent);
    }

    private void deleteModule(ModuleModel moduleModel, ViewHolder holder) {

        String id = moduleModel.getId();
        moduleRef = FirebaseDatabase.getInstance().getReference("Modules");
        moduleRef.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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

    @Override
    public int getItemCount() {
        return moduleModelArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ModuleFilter(this, filterList);
        }
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowModuleBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowModuleBinding.bind(itemView);
        }
    }
}
