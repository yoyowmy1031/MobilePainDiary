package com.example.mobilepaindiary.chart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobilepaindiary.HomeActivity;
import com.example.mobilepaindiary.databinding.ChartPieStepsFragmentBinding;
import com.example.mobilepaindiary.entity.PainRecord;
import com.example.mobilepaindiary.viewmodel.SharedViewModel;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ChartPieStepsFragment extends Fragment {
    private ChartPieStepsFragmentBinding stepBinding;
    private SharedViewModel sharedViewModel;
    private String userEmail;



    public ChartPieStepsFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState){
        stepBinding = ChartPieStepsFragmentBinding.inflate(inflater, container, false);
        View view = stepBinding.getRoot();

        sharedViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
                        .create(SharedViewModel.class);
        try{
            userEmail = ((HomeActivity)getActivity()).getEmail();
        }
        catch (Exception e){
            e.getMessage();
        }

        String currentDay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


        // get the pain record for the current day
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            CompletableFuture<PainRecord> recordsCompletableFuture = sharedViewModel.getRecordByEmailDate(userEmail,currentDay);
            recordsCompletableFuture.thenApply(records -> {
                ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();
                // if there is existing data update it
                if (records != null) {
                    pieEntries.add(new PieEntry(records.actualStep,"Walked Steps"));
                    pieEntries.add(new PieEntry(records.goalStep-records.actualStep,"Remaining Steps"));
                    setPieChart(pieEntries);
                }else{
                    Toast.makeText(getActivity(), "user has not entered any pain record today", Toast.LENGTH_SHORT).show();
                }
                return records;
            });
        }
        return view;
    }

    // enter the data for the pie chart and setup the legend, title and description
    public void setPieChart (ArrayList<PieEntry> pieEntries){
        PieDataSet pieDataSet = new PieDataSet(pieEntries,null);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextSize(16);
        PieData pieData = new PieData(pieDataSet);
        stepBinding.stepPie.setData(pieData);
        Legend legend = stepBinding.stepPie.getLegend();
        legend.setTextSize(12);
        legend.setWordWrapEnabled(true);
        stepBinding.stepPie.getDescription().setText("Remaining Steps for Today");
        stepBinding.stepPie.getDescription().setTextSize(16);
        stepBinding.stepPie.animateXY(2000,2000);
        stepBinding.stepPie.invalidate();//refresh the chart
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stepBinding = null;
    }
}
