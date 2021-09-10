package com.example.mobilepaindiary.repository;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.room.Query;

import com.example.mobilepaindiary.entity.LocFreqPair;
import com.example.mobilepaindiary.dao.PainRecordDAO;
import com.example.mobilepaindiary.database.PainRecordDatabase;
import com.example.mobilepaindiary.entity.PainRecord;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class PainRecordRepository {
    private PainRecordDAO painRecordDAO;
    private LiveData<List<PainRecord>> allRecords;

    public PainRecordRepository(Application application){
        PainRecordDatabase db = PainRecordDatabase.getInstance(application);
        painRecordDAO =db.painRecordDAO();
        allRecords= painRecordDAO.getAll();
    }
    // Room executes this query on a separate thread
    public LiveData<List<PainRecord>> getAllRecords() {

        return allRecords;
    }

    public void insert(final PainRecord painRecord){
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDAO.insert(painRecord);
            } });
    }
    public void deleteAll(){
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDAO.deleteAll();
            } });
    }
    public void resetId(){
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDAO.resetId();
            } });
    }

    public void delete(final PainRecord painRecord){
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDAO.delete(painRecord);
            } });
    }
    public void updateRecord(final PainRecord painRecord){
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDAO.updateRecord(painRecord);
            } });
    }

    //Address the error when using CompletableFuture and its methods, API level 24 instead of 16 as the minimum SDK version
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByDateFuture(final String date, final String email) {
        return CompletableFuture.supplyAsync(new Supplier<PainRecord>() {
            @Override
            public PainRecord get() {

                return painRecordDAO.findByDate(date,email);
            }
        }, PainRecordDatabase.databaseWriteExecutor);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<LocFreqPair>> getCountGroupByLocation(final String email) {
        return CompletableFuture.supplyAsync(new Supplier<List<LocFreqPair>>() {
            @Override
            public List<LocFreqPair> get() {
                return painRecordDAO.getCountGroupByLocation(email);
            }
        }, PainRecordDatabase.databaseWriteExecutor);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecord>> getRecordByDateRange(final String email, final String start, final String end) {
        return CompletableFuture.supplyAsync(new Supplier<List<PainRecord>>() {
            @Override
            public List<PainRecord> get() {
                return painRecordDAO.getRecordByDateRange(email, start, end);
            }
        }, PainRecordDatabase.databaseWriteExecutor);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> getRecordByEmailDate(final String email, final String date) {
        return CompletableFuture.supplyAsync(new Supplier<PainRecord>() {
            @Override
            public PainRecord get() {

                return painRecordDAO.getRecordByEmailDate(email,date);
            }
        }, PainRecordDatabase.databaseWriteExecutor);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecord> > getRecordByEmail(final String email) {
        return CompletableFuture.supplyAsync(new Supplier<List<PainRecord> >() {
            @Override
            public List<PainRecord>  get() {

                return painRecordDAO.getRecordByEmail(email);
            }
        }, PainRecordDatabase.databaseWriteExecutor);
    }


}