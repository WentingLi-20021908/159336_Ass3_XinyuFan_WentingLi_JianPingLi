package com.example.myapplication.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.myapplication.dao.MemoDao;
import com.example.myapplication.model.AMemo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {AMemo.class}, version = 1)
public abstract class MemoDatabase extends RoomDatabase {
    public abstract MemoDao memoDao();

    private static volatile MemoDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MemoDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MemoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    MemoDatabase.class,
                                    "memo_database")
                            .addCallback(callback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback callback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

        }
    };
}
