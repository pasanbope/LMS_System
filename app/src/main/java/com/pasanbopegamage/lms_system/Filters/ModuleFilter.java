package com.pasanbopegamage.lms_system.Filters;

import android.widget.Filter;

import com.pasanbopegamage.lms_system.Adapters.ModuleAdapter;
import com.pasanbopegamage.lms_system.Models.ModuleModel;

import java.util.ArrayList;

public class ModuleFilter extends Filter {

    ModuleAdapter moduleAdapter;
    ArrayList<ModuleModel> moduleModelArrayList;

    public ModuleFilter(ModuleAdapter moduleAdapter, ArrayList<ModuleModel> moduleModelArrayList) {
        this.moduleAdapter = moduleAdapter;
        this.moduleModelArrayList = moduleModelArrayList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence !=null && charSequence.length() > 0){

            charSequence = charSequence.toString().toUpperCase();

            ArrayList<ModuleModel> filterModel = new ArrayList<>();
            for (int i=0; i<moduleModelArrayList.size(); i++){

                if (moduleModelArrayList.get(i).getModule().toUpperCase().contains(charSequence)){

                    filterModel.add(moduleModelArrayList.get(i));

                }
            }
            results.count = filterModel.size();
            results.values = filterModel;
        }
        else {
            results.count = moduleModelArrayList.size();
            results.values = moduleModelArrayList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        moduleAdapter.moduleModelArrayList = (ArrayList<ModuleModel>) filterResults.values;
        moduleAdapter.notifyDataSetChanged();
    }
}
