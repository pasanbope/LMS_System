package com.pasanbopegamage.lms_system.Filters;

import android.widget.Filter;

import com.pasanbopegamage.lms_system.Adapters.LecturesAdapter;
import com.pasanbopegamage.lms_system.Models.LectureModel;

import java.util.ArrayList;

public class LecturesFilter extends Filter {

    LecturesAdapter lecturesAdapter;
    ArrayList<LectureModel> lectureModelArrayList;

    public LecturesFilter(LecturesAdapter lecturesAdapter, ArrayList<LectureModel> lectureModelArrayList) {
        this.lecturesAdapter = lecturesAdapter;
        this.lectureModelArrayList = lectureModelArrayList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence !=null && charSequence.length() > 0){

            charSequence = charSequence.toString().toUpperCase();

            ArrayList<LectureModel> filterModel = new ArrayList<>();
            for (int i=0; i<lectureModelArrayList.size(); i++){

                if (lectureModelArrayList.get(i).getLectureName().toUpperCase().contains(charSequence) ||
                        lectureModelArrayList.get(i).getLectureId().toUpperCase().contains(charSequence)){

                    filterModel.add(lectureModelArrayList.get(i));

                }
            }
            results.count = filterModel.size();
            results.values = filterModel;
        }
        else {
            results.count = lectureModelArrayList.size();
            results.values = lectureModelArrayList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        lecturesAdapter.lectureModelArrayList = (ArrayList<LectureModel>) filterResults.values;
        lecturesAdapter.notifyDataSetChanged();
    }
}
