package com.example.programa;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TvProgram.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TvProgramDao tvProgramDao();
}

