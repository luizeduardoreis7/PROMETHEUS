package com.example.programa;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tv_programs")
public class TvProgram {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String startTime;
    public String programName;
}