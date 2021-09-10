package com.example.mobilepaindiary.viewmodel;


import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobilepaindiary.database.PainRecordDatabase;
import com.example.mobilepaindiary.entity.LocFreqPair;
import com.example.mobilepaindiary.entity.PainRecord;
import com.example.mobilepaindiary.repository.PainRecordRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class SharedViewModel extends AndroidViewModel {
    private PainRecordRepository rRepository;
    private LiveData<List<PainRecord>> allRecords;
    private MutableLiveData<String> mEmail;
    private MutableLiveData<Double> mTemperature;
    private MutableLiveData<Integer> mPressure;
    private MutableLiveData<Integer> mHumidity;



    public SharedViewModel(Application application) {
        super(application);
        rRepository = new PainRecordRepository(application);
        allRecords = rRepository.getAllRecords();
        mEmail = new MutableLiveData<String>();
        mTemperature = new MutableLiveData<Double>();
        mPressure = new MutableLiveData<Integer>();
        mHumidity = new MutableLiveData<Integer>();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByDateFuture(final String entryDate, final String userEmail){
        return rRepository.findByDateFuture(entryDate, userEmail);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<LocFreqPair>> getCountGroupByLocation(final String mEmail){
        return rRepository.getCountGroupByLocation(mEmail);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecord>> getRecordByDateRange(final String email, final String start, final String end) {
        return rRepository.getRecordByDateRange(email, start, end);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> getRecordByEmailDate(final String email, final String date){
        return rRepository.getRecordByEmailDate(email, date);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecord> > getRecordByEmail(final String email){
        return rRepository.getRecordByEmail(email);
    }


    public LiveData<List<PainRecord>> getAllRecords() {
        return allRecords;
    }
    public void insert(PainRecord painRecord) {

        rRepository.insert(painRecord);
    }
    public void deleteAll() {

        rRepository.deleteAll();
    }
    public void resetId(){
        rRepository.resetId();
    }
    public void update(PainRecord painRecord) {

        rRepository.updateRecord(painRecord);
    }
    public void setEmail(String email) {
        mEmail.setValue(email);
    }
    public LiveData<String> getEmail() {
        return mEmail;
    }
    public void setTemperature(Double temperature) {
        mTemperature.setValue(temperature);
    }
    public LiveData<Double> getTemperature() {
        return mTemperature;
    }
    public void setPressure(Integer pressure) {
        mPressure.setValue(pressure);
    }
    public LiveData<Integer> getPressure() {
        return mPressure;
    }
    public void setHumidity(Integer humidity) {
        mHumidity.setValue(humidity);
    }
    public LiveData<Integer> getHumidity() {
        return mHumidity;
    }


}