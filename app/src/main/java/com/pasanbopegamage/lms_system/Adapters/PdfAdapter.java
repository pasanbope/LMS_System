package com.pasanbopegamage.lms_system.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pasanbopegamage.lms_system.Models.PdfModel;
import com.pasanbopegamage.lms_system.R;
import com.pasanbopegamage.lms_system.databinding.CustomeListviewBinding;

import java.util.ArrayList;

//public class PdfAdapter extends ArrayAdapter<PdfModel> {

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.ViewHolder> {

    private static final int REQUEST_CODE = 1000;
    private Context context;
    ArrayList<PdfModel> pdfModelArrayList;


        public PdfAdapter(Context context, ArrayList<PdfModel> pdfModelArrayList) {
        this.context = context;
        this.pdfModelArrayList = pdfModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custome_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PdfModel pdfModel = pdfModelArrayList.get(position);

      //  holder.binding.pdfLinkTv.setText("Materials");

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfModel.getPdfUrl());
        String name = storageReference.getName();
        holder.binding.pdfLinkTv.setText(name);

        holder.binding.pdfLinkTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfModel.getPdfUrl()));
                context.startActivity(browserIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pdfModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CustomeListviewBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomeListviewBinding.bind(itemView);
        }
    }
}
