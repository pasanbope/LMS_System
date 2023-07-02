package com.pasanbopegamage.lms_system.Filters;

import android.widget.Filter;

import com.pasanbopegamage.lms_system.Adapters.StudentsAdapter;
import com.pasanbopegamage.lms_system.Models.StudentsModel;

import java.util.ArrayList;

public class StudentsFilter extends Filter {

    StudentsAdapter studentsAdapter;
    ArrayList<StudentsModel> studentsModelArrayList;

    public StudentsFilter(StudentsAdapter studentsAdapter, ArrayList<StudentsModel> studentsModelArrayList) {
        this.studentsAdapter = studentsAdapter;
        this.studentsModelArrayList = studentsModelArrayList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence !=null && charSequence.length() > 0){

            charSequence = charSequence.toString().toUpperCase();

            ArrayList<StudentsModel> filterModel = new ArrayList<>();
            for (int i=0; i<studentsModelArrayList.size(); i++){

                if (studentsModelArrayList.get(i).getStudentName().toUpperCase().contains(charSequence) ||
                        studentsModelArrayList.get(i).getStudentId().toUpperCase().contains(charSequence)){

                    filterModel.add(studentsModelArrayList.get(i));

                }
            }
            results.count = filterModel.size();
            results.values = filterModel;
        }
        else {
            results.count = studentsModelArrayList.size();
            results.values = studentsModelArrayList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        studentsAdapter.studentsModelArrayList = (ArrayList<StudentsModel>) filterResults.values;
        studentsAdapter.notifyDataSetChanged();
    }
}
