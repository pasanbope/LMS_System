package com.pasanbopegamage.lms_system.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pasanbopegamage.lms_system.Fragments.Admin_Show_Lectures_Fragment;
import com.pasanbopegamage.lms_system.Fragments.Admin_Show_Students_Fragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Admin_Show_Students_Fragment();
            case 1:
                return new Admin_Show_Lectures_Fragment();
            default:
                return new Admin_Show_Students_Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
