package com.example.mobilepaindiary.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.mobilepaindiary.scheduletask.AlarmReceiver;
import com.example.mobilepaindiary.picker.TimePickerFragment;
import com.example.mobilepaindiary.HomeActivity;
import com.example.mobilepaindiary.R;
import com.example.mobilepaindiary.databinding.PainDataEntryFragmentBinding;
import com.example.mobilepaindiary.entity.PainRecord;
import com.example.mobilepaindiary.viewmodel.SharedViewModel;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;


public class PainDataEntryFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {
    private PainDataEntryFragmentBinding entryBinding;
    private SharedViewModel sharedViewModel;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private String currentDay;
    private String email;
    private Double temperature;
    private Integer humidity;
    private Integer pressure;
    private String mood = null;
    private Integer painIntensity = -1;
    private String painLocation = null;
    private Integer goalSteps = -1;
    private Integer actualSteps = -1;


    public PainDataEntryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        entryBinding = PainDataEntryFragmentBinding.inflate(inflater, container, false);
        View view = entryBinding.getRoot();
        // activate spinner and radio button interaction
        activateSpinner();
        activateRadioButton();

        // get Sharedviewmodel
        sharedViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(
                getActivity().getApplication()).create(SharedViewModel.class);

        // Set Alarm Button
        entryBinding.setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimePicker(v);
            }
        });

        // SAVE BUTTON
        entryBinding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyData()) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        CompletableFuture<PainRecord> customerCompletableFuture = sharedViewModel.findByDateFuture(currentDay, email);
                        customerCompletableFuture.thenApply(painRecord -> {
                            // if there is existing data update it
                            if (painRecord != null) {
                                painRecord.painIntensity = painIntensity;
                                painRecord.painLocation = painLocation;
                                painRecord.mood = mood;
                                painRecord.goalStep = goalSteps;
                                painRecord.actualStep = actualSteps;
                                sharedViewModel.update(painRecord);
                                entryBinding.entryNotice.setText("Update was successful for entry on: " + painRecord.entryDate);

                            } else {
                                // if there is not existing data, create record and store in sharedViewModel
                                PainRecord newRecord = new PainRecord(email, currentDay, painIntensity,
                                        painLocation, mood, goalSteps, actualSteps, temperature, humidity, pressure);
                                sharedViewModel.insert(newRecord);
                                // notice for successful save operation
                                entryBinding.entryNotice.setText("Added Record: " + currentDay);
                            }
                            return painRecord;
                        });
                    }
                }
                disableEdit();
            }
        });

        //EDIT BUTTON
        entryBinding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEdit();
            }
        });


        return view;
    }

    /**
     * Setup and activate the spinner
     */
    public void activateSpinner() {
        // spinner list
        List<String> list = new ArrayList<String>();
        list.add("back");
        list.add("head");
        list.add("knees");
        list.add("hips");
        list.add("abdomen");
        list.add("elbows");
        list.add("shoulders");
        list.add("shins");
        list.add("jaw");
        list.add("facial");

        // initiate Array Adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new
                ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        //selected item will look like a spinner set from XML
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        entryBinding.painLocationSpinner.setAdapter(spinnerAdapter);
        entryBinding.painLocationSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {
                        // get selected pain location from spinner
                        String painLocation = parent.getItemAtPosition(pos).toString();
                        if (!painLocation.isEmpty()) {
                            SharedPreferences sharedPref = requireActivity().getSharedPreferences("painLocation", Context.MODE_PRIVATE);
                            SharedPreferences.Editor spEditor = sharedPref.edit();
                            spEditor.putString("painLocation", painLocation);
                            spEditor.apply();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    /**
     * Activate Radio Button
     */
    public void activateRadioButton() {
        entryBinding.veryGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mood = "very_good";
            }
        });
        entryBinding.good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mood = "good";
            }
        });
        entryBinding.average.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mood = "average";
            }
        });
        entryBinding.low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mood = "low";
            }
        });
        entryBinding.veryLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mood = "very_low";
            }
        });
    }

    /**
     * Verify if all the required fields are filled in
     *
     * @param aEmail
     * @param aDate
     * @param aPainIntensity
     * @param aPainLoc
     * @param aMood
     * @param aGoal
     * @param anActual
     * @param aTemp
     * @param aHumid
     * @param aPress
     * @return true if all the information are filled in
     */
    public boolean dataAllFilled(String aEmail, String aDate,
                                 Integer aPainIntensity,
                                 String aPainLoc, String aMood,
                                 Integer aGoal, Integer anActual, Double aTemp,
                                 Integer aHumid, Integer aPress) {

        if (aEmail.isEmpty() || aEmail == null) {
            Toast.makeText(getActivity(), "Useremail has not been filled", Toast.LENGTH_LONG).show();
            return false;
        }
        if (aPainIntensity < 0) {
            Toast.makeText(getActivity(), "Please choose a pain intensity level", Toast.LENGTH_LONG).show();
            return false;
        }
        if (aPainLoc.isEmpty() || painLocation == null) {
            Toast.makeText(getActivity(), "Please choose a pain location", Toast.LENGTH_LONG).show();
            return false;
        }
        if (aMood.isEmpty() || aMood == null) {
            Toast.makeText(getActivity(), "Please choose a mood", Toast.LENGTH_LONG).show();
            return false;
        }
        if (aDate.isEmpty() || aDate == null) {
            Toast.makeText(getActivity(), "Current Date has not been found", Toast.LENGTH_LONG).show();
            return false;
        }
        if (aGoal < 0 || anActual < 0) {
            Toast.makeText(getActivity(), "Please enter a positive number for goal and actual step field", Toast.LENGTH_LONG).show();
            return false;
        }
        if (aEmail.isEmpty() || aEmail == null) {
            Toast.makeText(getActivity(), "User email has not been filled", Toast.LENGTH_LONG).show();
            return false;
        }
        if (aTemp == null || aHumid == null || aPress == null) {
            Toast.makeText(getActivity(), "Weather information has not been found", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;

    }

    /**
     * Disable the editable fields
     */
    public void disableEdit() {
        entryBinding.painIntensitySlider.setEnabled(false);
        entryBinding.radioGroup.setEnabled(false);
        entryBinding.painLocationSpinner.setEnabled(false);
        entryBinding.goalTextField.setEnabled(false);
        entryBinding.actualTextField.setEnabled(false);
        entryBinding.saveButton.setEnabled(false);
    }

    /**
     * Enable the editable fields
     */
    public void enableEdit() {
        entryBinding.painIntensitySlider.setEnabled(true);
        entryBinding.radioGroup.setEnabled(true);
        entryBinding.painLocationSpinner.setEnabled(true);
        entryBinding.goalTextField.setEnabled(true);
        entryBinding.actualTextField.setEnabled(true);
        entryBinding.saveButton.setEnabled(true);
    }


    /**
     * get live data from home fragment including user email and weather variable
     *
     * @param viewModel
     */
    public void getDataFromHome(SharedViewModel viewModel) {
        try {
            // get the data from Home activity and Home Fragment
            email = ((HomeActivity) getActivity()).getEmail();
            SharedPreferences sharedPref = requireActivity().
                    getSharedPreferences("weather", Context.MODE_PRIVATE);
            temperature = Double.parseDouble(sharedPref.getString("temperature", null));
            pressure = Integer.valueOf(sharedPref.getString("pressure", null));
            humidity = Integer.valueOf(sharedPref.getString("humidity", null));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error getting data from Home", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get the time of the TimePicker to start an alarm
     *
     * @param view
     * @param hourOfDay
     * @param minute
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        Calendar current = Calendar.getInstance();
        String setTime = new SimpleDateFormat("HH:mm").format(c.getTime());
        entryBinding.alarmTime.setText(getString(R.string.alarm_time) + setTime);
        if(current.getTimeInMillis() >= c.getTimeInMillis()){
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
        c.add(Calendar.MINUTE, -2);
        startAlarm(c);
    }


    /**
     * Set Time Picker
     */
    public void setTimePicker(View v) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.setTargetFragment(PainDataEntryFragment.this, 0);
        timePicker.show(getFragmentManager(), "time picker");
    }


    /**
     * Set the Alarm with the time parameter
     *
     * @param c
     */
    private void startAlarm(Calendar c) {
//        entryBinding.entryNotice.setText(c.get(Calendar.MINUTE) +" // "+ String.valueOf(c.getTimeInMillis()));
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        intent.putExtra("alert", "It's time to fill in your pain diary");
        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    /**
     * @return True if all data are filled with correct format
     */
    public boolean verifyData() {
        boolean valid = false;
        // get today's date in format
        currentDay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        // get required values from the view
        painIntensity = (int) entryBinding.painIntensitySlider.getValue();
        painLocation = entryBinding.painLocationSpinner.getSelectedItem().toString();
        String goalStepsStr = entryBinding.goalTextField.getText().toString();
        String actualStepsStr = entryBinding.actualTextField.getText().toString();
        getDataFromHome(sharedViewModel);
        if(goalStepsStr.isEmpty()|| actualStepsStr.isEmpty() || goalStepsStr == null || actualStepsStr == null)
            entryBinding.entryNotice.setText("Please fill in the actual and goal step");
        try {
            // convert string to int for goalSteps and actualSteps fields
            goalSteps = Integer.parseInt(goalStepsStr);
            actualSteps = Integer.parseInt(actualStepsStr);
            // make sure all required fields has value
            if (dataAllFilled(email, currentDay, painIntensity, painLocation, mood, goalSteps, actualSteps, temperature, humidity, pressure)) {
                valid = true;
            } else {
                entryBinding.entryNotice.setText("Incomplete Entry: Please make sure all fields are completed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            entryBinding.entryNotice.setText("Incomplete Entry: Please make sure all fields are completed");
        }
        return valid;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        entryBinding = null;
    }
}
