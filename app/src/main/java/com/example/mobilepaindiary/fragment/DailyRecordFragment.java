package com.example.mobilepaindiary.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mobilepaindiary.HomeActivity;
import com.example.mobilepaindiary.adapter.RecyclerViewAdapter;
import com.example.mobilepaindiary.databinding.DailyRecordFragmentBinding;
import com.example.mobilepaindiary.entity.PainRecord;
import com.example.mobilepaindiary.viewmodel.SharedViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class DailyRecordFragment extends Fragment {
    private DailyRecordFragmentBinding recordBinding;
    private SharedViewModel sharedViewModel;
    private List<PainRecord> records;
    private RecyclerViewAdapter adapter;

    public DailyRecordFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        recordBinding = DailyRecordFragmentBinding.inflate(getLayoutInflater());
        View view = recordBinding.getRoot();

        // initiate pain record list
        records=new ArrayList<PainRecord>();
        String userEmail = ((HomeActivity) getActivity()).getEmail();

        //initiate recycler adapter
        adapter = new RecyclerViewAdapter(records);

        //creates a line divider between rows
        recordBinding.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                LinearLayoutManager.VERTICAL));
        recordBinding.recyclerView.setAdapter(adapter);
        recordBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        // we make sure that AndroidViewModelFactory creates the view model
        // so it can accept the Application as the parameter
        sharedViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
                        .create(SharedViewModel.class);

         //get all records of the user from the database
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            { CompletableFuture<List<PainRecord>> recordCompletableFuture = sharedViewModel.getRecordByEmail(userEmail);
                recordCompletableFuture.thenApply(records -> {
                    // fill in the recyclerview adapter with the pain records
                    adapter.addRecord(records);
                    return records;
                });
            }
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recordBinding = null;
    }
}