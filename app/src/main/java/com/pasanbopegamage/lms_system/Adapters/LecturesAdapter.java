package com.pasanbopegamage.lms_system.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pasanbopegamage.lms_system.Filters.LecturesFilter;
import com.pasanbopegamage.lms_system.Models.LectureModel;
import com.pasanbopegamage.lms_system.R;
import com.pasanbopegamage.lms_system.databinding.RawStudentsBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LecturesAdapter extends RecyclerView.Adapter<LecturesAdapter.ViewHolder> implements Filterable {

    private Context context;
    public ArrayList<LectureModel> lectureModelArrayList,filterList;
    private LecturesFilter filter;


    public LecturesAdapter(Context context, ArrayList<LectureModel> lectureModelArrayList) {
        this.context = context;
        this.lectureModelArrayList = lectureModelArrayList;
        this.filterList = lectureModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.raw_students, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        LectureModel lectureModel = lectureModelArrayList.get(position);

        String image = lectureModel.getLectureImage();


        try {
            Picasso.get().load(image).placeholder(R.drawable.man).into(holder.binding.profilePic);
        } catch (Exception e) {
            holder.binding.profilePic.setImageResource(R.drawable.man);
        }

        holder.binding.nameTv.setText("Name :"+" "+lectureModel.getLectureName());
        holder.binding.emailTv.setText("Email :"+" "+lectureModel.getLectureEmail());
        holder.binding.studentIdTv.setText("Id :"+" "+lectureModel.getLectureId());
        holder.binding.studentStatusTv.setText("Status :"+" "+lectureModel.getAccountStatus());
    }

    @Override
    public int getItemCount() {
        return lectureModelArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new LecturesFilter(this, filterList);
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
