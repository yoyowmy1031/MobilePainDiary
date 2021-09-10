package com.example.mobilepaindiary;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.mobilepaindiary.entity.PainRecord;
import com.example.mobilepaindiary.scheduletask.UploadWorker;
import com.example.mobilepaindiary.databinding.ActivityHomeBinding;
import com.example.mobilepaindiary.viewmodel.SharedViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;


import org.apache.commons.math3.geometry.euclidean.oned.Interval;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    private SharedViewModel sharedViewModel;
    private String currentDay;
    private String email;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get current day
        currentDay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        // get the sign in email to access data
        try {
            Bundle bundle = getIntent().getExtras();
            email = bundle.getString("email");
        } catch (Exception e) {
            Log.d(TAG, "onError:: Code : " + e.getMessage());
        }
        // inflate view
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.appBar.toolbar);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home_fragment,
                R.id.nav_report_fragment,
                R.id.nav_pain_entry_fragment,
                R.id.nav_daily_record_fragment,
                R.id.nav_map_fragment)
                //to display the Navigation button as a drawer symbol,not being shown as an Up button
                .setOpenableLayout(binding.drawerLayout)
                .build();

        FragmentManager fragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment)
                fragmentManager.findFragmentById(R.id.nav_host_fragment);


        NavController navController = navHostFragment.getNavController();
        //Sets up a NavigationView for use with a NavController.
        NavigationUI.setupWithNavController(binding.navView, navController);
        //Sets up a Toolbar for use with a NavController.
        NavigationUI.setupWithNavController(binding.appBar.toolbar,
                navController, mAppBarConfiguration);

        // write sample data to sharedviewmodel and call worker to update database
        sharedViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
                        .create(SharedViewModel.class);

        // make sure add sample record action start after data are cleared.
        if (clearSharedViewModel()){
            addSamplePainRecord();
        }
    }

    /**
     * Clear the existing data in the shared view model
     * @return
     */
    public boolean clearSharedViewModel(){
        // clear data and refill
        sharedViewModel.deleteAll();
        sharedViewModel.resetId();
        return true;
    }


    /**
     * get the user email from this activity
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * periodic worker manager request (not valid)
     */
//    public void callWorker() {
//        new Thread(new Runnable() {
//            public void run() {
//                //delay till 10pm
//                Calendar c = Calendar.getInstance();
//                c.set(Calendar.HOUR_OF_DAY, 10);
//                c.set(Calendar.MINUTE, 0);
//                long delay = c.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
//
//                // repeat the work every day start at 10 pm
//                PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
//                        UploadWorker.class, 24, TimeUnit.HOURS)
//                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
//                        .setInputData(new Data.Builder().putString("email", email).build())
//                        .build();
//                // enqueue the work request
//                WorkManager.getInstance().enqueue(periodicWorkRequest);
//            }
//        }).start();
//    }

    /**
     * add 10 sample records for tim@gmail.com
     */
    public void addSamplePainRecord() {
        sharedViewModel.insert(new PainRecord(email, "2021-04-09", 2, "elbow", "very_good", 10000, 7000, 14.7, 56, 1011));
        sharedViewModel.insert(new PainRecord(email, "2021-04-10", 2, "back", "average", 10000, 2000, 14.5, 50, 1012));
        sharedViewModel.insert(new PainRecord(email, "2021-04-12", 1, "back", "average", 10000, 7000, 14.7, 56, 1011));
        sharedViewModel.insert(new PainRecord(email, "2021-04-13", 1, "elbows", "very_good", 10000, 1800, 12.5, 65, 1010));
        sharedViewModel.insert(new PainRecord(email, "2021-04-17", 5, "knee", "average", 10000, 5300, 20.0, 65, 1008));
        sharedViewModel.insert(new PainRecord(email, "2021-04-18", 2, "knee", "very_good", 10000, 7000, 14.7, 56, 1011));
        sharedViewModel.insert(new PainRecord(email, "2021-04-27", 2, "back", "good", 10000, 6000, 14.3, 66, 1008));
        sharedViewModel.insert(new PainRecord(email, "2021-04-28", 7, "neck", "low", 10000, 1700, 21.5, 67, 1010));
        sharedViewModel.insert(new PainRecord(email, "2021-04-29", 3, "back", "average", 10000, 8700, 17.5, 67, 1011));
        sharedViewModel.insert(new PainRecord(email, "2021-04-30", 3, "back", "average", 10000, 8700, 17.5, 67, 1011));
    }
}