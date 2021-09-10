package com.example.mobilepaindiary.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilepaindiary.databinding.RvLayoutBinding;
import com.example.mobilepaindiary.entity.PainRecord;
//import com.example.mobilepaindiary.model.DailyRecord;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter <RecyclerViewAdapter.ViewHolder> {
    private List<PainRecord> records;

    public RecyclerViewAdapter(List<PainRecord> records) {
        this.records = records;
    }
    //This method creates a new view holder that is constructed with a new View
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate from a layout
        RvLayoutBinding binding= RvLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);

    }

    // this method binds the view holder created with data that will be displayed
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder viewHolder, int position)
    {
        final PainRecord record = records.get(position);

        viewHolder.binding.tvRvemail.setText(record.getUserEmail());
        viewHolder.binding.tvRvid.setText(Integer.toString(record.getEid()));
        viewHolder.binding.tvRvhumid.setText(Integer.toString(record.getHumidity()));
        viewHolder.binding.tvRvpress.setText(Integer.toString(record.getPressure()));
        viewHolder.binding.tvRvtemp.setText(Integer.toString((int) record.getTemperature()));
        viewHolder.binding.tvRvdate.setText(record.getDate());
        viewHolder.binding.tvRvintensity.setText(Integer.toString(record.getPainIntensity()));
        viewHolder.binding.tvRvlocation.setText(record.getPainLocation());
        viewHolder.binding.tvRvmood.setText(record.getMood());
        viewHolder.binding.tvRvactual.setText((Integer.toString(record.getActualStep())));
        viewHolder.binding.tvRvgoal.setText((Integer.toString(record.getGoalStep())));
    }
    @Override
    public int getItemCount() {
        return records.size();
    }
    public void addRecord(List<PainRecord> newRecords) {
        records = newRecords;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RvLayoutBinding binding;
        public ViewHolder(RvLayoutBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}