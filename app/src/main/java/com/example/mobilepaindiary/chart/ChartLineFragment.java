package com.example.mobilepaindiary.chart;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobilepaindiary.picker.DatePickerFragment;
import com.example.mobilepaindiary.HomeActivity;
import com.example.mobilepaindiary.databinding.ChartLineFragmentBinding;
import com.example.mobilepaindiary.entity.PainRecord;
import com.example.mobilepaindiary.viewmodel.SharedViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static android.content.ContentValues.TAG;

public class ChartLineFragment extends Fragment implements DatePickerDialog.OnDateSetListener{
    private ChartLineFragmentBinding lineBinding;
    private static final String TAG = "LineChart";
    private int flag;
    private SharedViewModel sharedViewModel;
    private String start;
    private String end;
    private String weather;
    private String userEmail;
    private ArrayList<Entry> weatherValues;
    private ArrayList<Entry> painValues;

    public ChartLineFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        lineBinding = ChartLineFragmentBinding.inflate(inflater, container, false);
        View view = lineBinding.getRoot();

        lineBinding.corrButton.setVisibility(View.VISIBLE);
        flag = -1;
        activateSpinner();
        sharedViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
                        .create(SharedViewModel.class);

        // Line Graph Load button
        lineBinding.loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = ((HomeActivity) getActivity()).getEmail();
                start = lineBinding.startDate.getText().toString();
                end = lineBinding.endDate.getText().toString();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                if(!userEmail.isEmpty() && userEmail !=null
                && !start.isEmpty() && start !=null
                && !end.isEmpty() && end !=null){
                    try {
                        // verify is start date is earlier than end date
                        Date startDate = format.parse(start);
                        Date endDate= format.parse(end);
                        if (startDate.compareTo(endDate) <= 0) {
                            try{
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                    CompletableFuture<List<PainRecord>> recordsCompletableFuture = sharedViewModel.getRecordByDateRange(userEmail, start, end);
                                    recordsCompletableFuture.thenApply(records -> {
                                        weather = lineBinding.weatherSpinner.getSelectedItem().toString();
                                        // if there is existing data update it
                                        if (records!=null) {
                                            lineBinding.corrButton.setVisibility(View.VISIBLE);
                                            drawLineChart(records,weather);
                                            // show the correlation button
                                        }
                                        else{
                                            lineBinding.notice.setText("No data in this time range");
                                        }
                                        return records;
                                    });
                                }
                            }catch(Exception e){
                                Log.d(TAG, "onError:: Code : "+ e.getMessage());
                            }
                        }
                        else{
                            lineBinding.notice.setText("Please make sure start date is earlier than end date");
                        }
                    } catch (ParseException e) {
                        Log.d(TAG, "onError:: Code : "+ e.getMessage());
                    }
                }else{
                    Toast.makeText(getActivity(),"Please select all fields to generate the line chart",Toast.LENGTH_LONG);
                }
            }
        });

        // Start Data Picker
        lineBinding.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 0;
                setDatePicker(v);
            }
        });
        // End Data Picker
        lineBinding.end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 1;
                setDatePicker(v);
            }
        });

        // correlation test result
        lineBinding.corrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // worker thread
                new Thread(new Runnable() {
                    public void run() {
                        if(weatherValues!=null && painValues !=null){
                            String result = testCorrelation();
                            // update view in UI thread
                            lineBinding.corrValue.post(new Runnable() {
                                public void run() {
                                    lineBinding.corrValue.setText(result);
                                }
                            });
                        }else{
                            // update view in UI thread
                            lineBinding.corrValue.post(new Runnable() {
                                public void run() {
                                    lineBinding.notice.setText("Need to generate line chart first");
                                }
                            });
                        }
                    }
                }).start();


            }
        });

        return view;
    }

    /**
     * Set up common Line Chart Style
     * @param lineDataSet
     */
    public void setLineStyle(LineDataSet lineDataSet){

        lineDataSet.setLineWidth(1.75f);
        lineDataSet.setCircleRadius(5f);
        lineDataSet.setCircleHoleRadius(2.5f);
        lineDataSet.setDrawValues(false);
    }

    /**
     * set up spinner
     */
    public void activateSpinner(){
        // spinner list
        List<String> list = new ArrayList<String>();
        list.add("temperature");
        list.add("humidity");
        list.add("pressure");
        // initiate Array Adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new
                ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        //selected item will look like a spinner set from XML
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lineBinding.weatherSpinner.setAdapter(spinnerAdapter);
        lineBinding.weatherSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {
                        String weatherVariable = parent.getItemAtPosition(pos).toString();
                        if(!weatherVariable.isEmpty()){
                            SharedPreferences sharedPref= requireActivity(). getSharedPreferences("weatherVariable", Context.MODE_PRIVATE);
                            SharedPreferences.Editor spEditor = sharedPref.edit();
                            spEditor.putString("weatherVariable", weatherVariable);
                            spEditor.apply();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
    }

    /**
     * set up date picker
     * @param v
     */
    public void setDatePicker(View v){
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.setTargetFragment(ChartLineFragment.this, 0);
        datePicker.show(getFragmentManager(), "date picker");
    }

    /**
     *
     * @param records
     * @param weather
     */
    public void drawLineChart(List<PainRecord> records, String weather){
        new Thread(new Runnable() {
            public void run() {
                weatherValues = new ArrayList<>();
                painValues = new ArrayList<>();
                List<String> xAxisValues = new ArrayList<>();

                // add entry to weathervalues and painvalues according to weather variable type
                switch (weather){
                    case "temperature":
                        for (int i = 0; i <records.size(); i++) {
                            weatherValues.add(new Entry(i,(int)records.get(i).getTemperature()));
                            painValues.add(new Entry(i,records.get(i).getPainIntensity()));
                            xAxisValues.add(records.get(i).getDate());
                        }
                        break;
                    case "humidity":
                        for (int i =0; i<records.size(); i++) {
                            weatherValues.add(new Entry(i,records.get(i).getHumidity()));
                            painValues.add(new Entry(i,records.get(i).getPainIntensity()));
                            xAxisValues.add(records.get(i).getDate());
                        }
                        break;
                    case "pressure":
                        for (int i=0; i<records.size(); i++) {
                            weatherValues.add(new Entry(i,records.get(i).getPressure()));
                            painValues.add(new Entry(i,records.get(i).getPainIntensity()));
                            xAxisValues.add(records.get(i).getDate());
                        }
                        break;
                }
                LineDataSet weatherLine = new LineDataSet(weatherValues,"Weather Value");
                LineDataSet painLine = new LineDataSet(painValues,"Pain Intensity");

                //set color for both lines
                setLineStyle(weatherLine);
                setLineStyle(painLine);
                painLine.setColor(Color.RED);
                painLine.setCircleColor(Color.RED);
                painLine.setHighLightColor(Color.RED);
                // update view in UI thread
                lineBinding.lineChart.post(new Runnable() {
                    public void run() {
                        // enable scaling and dragging
                        lineBinding.lineChart.setDragEnabled(true);
                        lineBinding.lineChart.setScaleEnabled(true);

                        //set data set
                        LineData lineData = new LineData(weatherLine,painLine);
                        lineBinding.lineChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));
                        lineBinding.lineChart.setData(lineData);

                        //set description
                        Description description = new Description();
                        description.setText("Weather Pain Trend");
                        lineBinding.lineChart.setDescription(description);

                        // refresh the graph
                        lineBinding.lineChart.invalidate();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lineBinding = null;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        if (flag == 0) {
            lineBinding.startDate.setText(dateStr);
        } else if (flag == 1) {
            lineBinding.endDate.setText(dateStr);
        }
    }

    /**
     * Calculate the correlation statistics
     * @return A string indicating the p-value and r-value of the weather-pain correlation
     */
    public String testCorrelation(){
        // two column array: 1st column=first array, 1st column=second array
        double [][] data = new double[weatherValues.size()][];
        for (int i = 0; i < weatherValues.size(); i++) {
            double weather = weatherValues.get(i).getY();
            double pain = painValues.get(i).getY();
            double pair[] = {weather,pain};
            data[i] = pair;
        }

         // create a realmatrix
        RealMatrix m = MatrixUtils.createRealMatrix(data);

        // correlation test: x-y
        PearsonsCorrelation pc = new PearsonsCorrelation(m);
            RealMatrix corM = pc.getCorrelationMatrix();
        // significant test of the correlation coefficient (p-value)
        RealMatrix pM = pc.getCorrelationPValues();
        return(" p value:" + pM.getEntry(0, 1)+ "\n" + " r value: " + corM.getEntry(0, 1));
    }
}

