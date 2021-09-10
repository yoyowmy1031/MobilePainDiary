package com.example.mobilepaindiary.chart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobilepaindiary.HomeActivity;
import com.example.mobilepaindiary.databinding.ChartPieLocationFragmentBinding;
import com.example.mobilepaindiary.entity.LocFreqPair;
import com.example.mobilepaindiary.entity.PainRecord;
import com.example.mobilepaindiary.viewmodel.SharedViewModel;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChartPieLocationFragment extends Fragment {
    private ChartPieLocationFragmentBinding locationBinding;
    private SharedViewModel sharedViewModel;
    private String userEmail;


    public ChartPieLocationFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        locationBinding = ChartPieLocationFragmentBinding.inflate(inflater, container, false);
        View view = locationBinding.getRoot();

        sharedViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
                        .create(SharedViewModel.class);
        try{
            userEmail = ((HomeActivity)getActivity()).getEmail();
        }
        catch (Exception e){
            e.getMessage();
        }

        //get the pain location frequency of all data stored in under the current user.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            CompletableFuture<List<LocFreqPair>> locFreqPairsCompletableFuture = sharedViewModel.getCountGroupByLocation(userEmail);
            locFreqPairsCompletableFuture.thenApply(locFreqPairs -> {
                ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();
                // if there is existing data get the data
                    if (locFreqPairs.size() > 0) {
                        for (LocFreqPair entry : locFreqPairs)
                            pieEntries.add(new PieEntry(entry.getCount(), entry.getLocation()));

                        // add dataset to the pie chart
                        PieDataSet pieDataSet = new PieDataSet(pieEntries,null);
                        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                        pieDataSet.setValueTextSize(16);
                        PieData pieData = new PieData(pieDataSet);
                        locationBinding.locPie.setData(pieData);
                        // set the legend, title and description of the pie chart
                        Legend legend = locationBinding.locPie.getLegend();
                        legend.setTextSize(12);
                        legend.setWordWrapEnabled(true);
                        locationBinding.locPie.getDescription().setText("Pain Location Summary");
                        locationBinding.locPie.getDescription().setTextSize(16);
                        locationBinding.locPie.animateXY(2000,2000);
                        locationBinding.locPie.invalidate();
                    }else
                        Toast.makeText(getActivity(), "user has no pain record", Toast.LENGTH_SHORT).show();
                    return locFreqPairs;
            });
        }


        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        locationBinding = null;
    }


}


