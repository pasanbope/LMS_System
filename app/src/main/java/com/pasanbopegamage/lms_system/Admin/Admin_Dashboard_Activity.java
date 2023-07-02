package com.pasanbopegamage.lms_system.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.pasanbopegamage.lms_system.Adapters.ViewPagerAdapter;
import com.pasanbopegamage.lms_system.R;
import com.pasanbopegamage.lms_system.Splash_Activity;
import com.pasanbopegamage.lms_system.databinding.ActivityAdminDashboardBinding;

public class Admin_Dashboard_Activity extends AppCompatActivity {

    ActivityAdminDashboardBinding binding;

    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new ViewPagerAdapter(Admin_Dashboard_Activity.this);
        binding.view.setAdapter(adapter);

        binding.tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.view.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        binding.view.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tab.getTabAt(position).select();
            }
        });

    }

    private void showLogoutDialog() {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Admin_Dashboard_Activity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(Admin_Dashboard_Activity.this).inflate(R.layout.layout_warning_dialog, (ConstraintLayout) findViewById(R.id.layoutDialogContainer)

        );

        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitile)).
                setText(getResources().getString(R.string.warning_title));
        ((TextView) view.findViewById(R.id.textMessage)).
                setText(getResources().getString(R.string.dummy_text));
        ((TextView) view.findViewById(R.id.buttonYes)).
                setText(getResources().getString(R.string.yes));
        ((TextView) view.findViewById(R.id.buttonNo)).
                setText(getResources().getString(R.string.no));

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               
                    LogOut();
                    alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void LogOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(Admin_Dashboard_Activity.this, Splash_Activity.class));
        finish();
    }

}