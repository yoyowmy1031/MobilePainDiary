package com.example.mobilepaindiary.scheduletask;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import androidx.work.Data;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.mobilepaindiary.HomeActivity;
import com.example.mobilepaindiary.entity.PainRecord;
import com.example.mobilepaindiary.viewmodel.SharedViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class UploadWorker extends Worker {
    private SharedViewModel sharedViewModel;
    private String email;
    private String currentDay;
    private PainRecord painRecord;
    private Data data;
    private static final String TAG = "uploadWorker";

    public UploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        sharedViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance((Application) getApplicationContext()).create(SharedViewModel.class);
        currentDay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        email = getInputData().getString("email");
        data = getDataForToday();
        writeRecordToFirebase(data);
        Log.e(TAG, "work request received");
        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }

    /**
     * Method to write record passed to Firebase database
     *
     * @param data
     */
    public void writeRecordToFirebase(Data data) {
        int eid = data.getInt("eid", -1);
        Log.e(TAG, String.valueOf(eid)+data.getString("date") + data.getString("email"));
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("painRecords");
        if (eid != -1) {
            String eidStr = String.valueOf(eid);
            mDatabase.child(eidStr).setValue(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Write was successful!
                            Log.e(TAG, "firebase database upload success");
                            Log.e(TAG, data.getString("date") + data.getString("email"));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Write failed
                            Log.e(TAG, "firebase database upload failed");
                            Log.e(TAG, e.getMessage());
                        }
                    });
        } else {
            Log.e(TAG, "Get empty data for record");
        }
    }

    public Data getDataForToday() {
        PainRecord getPainRecord = getPainRecordForToday();
        if (getPainRecord != null) {
            Data newData = new Data.Builder().putInt("eid", getPainRecord.eid)
                    .putString("date", getPainRecord.entryDate)
                    .putString("email", getPainRecord.userEmail)
                    .putInt("painIntensity", getPainRecord.painIntensity)
                    .putString("painLocation", getPainRecord.painLocation)
                    .putString("mood", getPainRecord.mood)
                    .putInt("actualStep", getPainRecord.actualStep)
                    .putInt("goalStep", getPainRecord.goalStep)
                    .putDouble("temperature", getPainRecord.temperature)
                    .putInt("humidity", getPainRecord.humidity)
                    .putInt("pressure", getPainRecord.pressure)
                    .build();
            return newData;
        } else {
            Log.e(TAG, "No Record added for today");
        }
        return new Data(null);
    }

    /**
     * @return Data object that stores the attributes of the pain record for current day
     */
    public PainRecord getPainRecordForToday() {

        if (currentDay != null && email != null
                && !currentDay.isEmpty() && !email.isEmpty()) {
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    CompletableFuture<PainRecord> customerCompletableFuture = sharedViewModel.findByDateFuture(currentDay, email);
                    customerCompletableFuture.thenApply(aPainRecord -> {
                        // if there is existing data put the attributes into Data object
                        if (aPainRecord != null) {
                            painRecord = aPainRecord;
                        } else {
                            // if there is not existing data, report error
                            Log.e(TAG, "No Record added for today");
                        }
                        return aPainRecord;
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "No Record added for today");
            }
        } else {
            Log.e(TAG, "Email or Date is null");
        }
        return painRecord;
    }

}
