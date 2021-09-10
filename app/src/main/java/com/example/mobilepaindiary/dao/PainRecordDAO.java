package com.example.mobilepaindiary.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobilepaindiary.entity.LocFreqPair;
import com.example.mobilepaindiary.entity.PainRecord;

import java.util.List;

@Dao
public interface PainRecordDAO {
    @Query("SELECT * FROM PainRecord ORDER BY eid ASC")
    LiveData<List<PainRecord>> getAll();

    @Query("SELECT * FROM PainRecord WHERE entry_date = :entryDate AND user_email=:email")
    PainRecord findByDate(String entryDate, String email);

    @Insert
    void insert(PainRecord painRecord);

    @Delete
    void delete(PainRecord painRecord);

    @Update
    void updateRecord(PainRecord painRecord);

    @Query("DELETE FROM PainRecord")
    void deleteAll();
//
    @Query("DELETE FROM sqlite_sequence WHERE name = 'PainRecord'")
    void resetId();


    @Query("Select pain_location as painLocation, Count(eid) as count from PainRecord Where user_email =:userEmail AND pain_intensity > 0 Group By pain_location Order by pain_location")
    List<LocFreqPair> getCountGroupByLocation(String userEmail);

    @Query("SELECT * FROM PainRecord WHERE user_email =:userEmail AND entry_date = :date ORDER BY entry_date")
    PainRecord getRecordByEmailDate(String userEmail, String date);

    @Query("SELECT * FROM PainRecord WHERE user_email =:userEmail ORDER BY eid")
    List<PainRecord>  getRecordByEmail(String userEmail);

    @Query("SELECT * FROM PainRecord WHERE user_email = :email AND entry_date BETWEEN :start AND :end ORDER BY entry_date ASC")
    List<PainRecord> getRecordByDateRange(String email, String start, String end);

}
