package com.example.programa;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TvProgramDao {
    @Insert
    void insert(TvProgram tvProgram);

    @Query("SELECT * FROM tv_programs WHERE startTime = :selectedDate")
    List<TvProgram> getTvPrograms(String selectedDate);
}
