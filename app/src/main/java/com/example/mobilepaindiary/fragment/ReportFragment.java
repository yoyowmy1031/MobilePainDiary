package com.example.mobilepaindiary.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobilepaindiary.R;
import com.example.mobilepaindiary.chart.ChartLineFragment;
import com.example.mobilepaindiary.chart.ChartPieLocationFragment;
import com.example.mobilepaindiary.chart.ChartPieStepsFragment;
import com.example.mobilepaindiary.databinding.ReportFragmentBinding;
import com.example.mobilepaindiary.entity.PainRecord;
import com.example.mobilepaindiary.viewmodel.SharedViewModel;

public class ReportFragment extends Fragment {

    private ReportFragmentBinding reportBinding;
    private SharedViewModel sharedViewModel;


    public ReportFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the View for this fragment using binding
        reportBinding = ReportFragmentBinding.inflate(inflater, container, false);
        View view = reportBinding.getRoot();

        sharedViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
                        .create(SharedViewModel.class);

        // replace the chart fragment according to button clicked
        reportBinding.locPie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ChartPieLocationFragment());
            }
        });
        reportBinding.painWeatherLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ChartLineFragment());
            }
        });
        reportBinding.remainSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ChartPieStepsFragment());
            }
        });
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        reportBinding = null;
    }



    /**
     * Replace the chart in fragment container view with the new fragment
     *
     * @param fragment
     */
    private void replaceFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .commit();
    }


}


