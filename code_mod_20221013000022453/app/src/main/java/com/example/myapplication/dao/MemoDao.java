package com.example.myapplication.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.model.AMemo;

import java.util.List;

@Dao
public interface MemoDao {
    @Insert
    long insert(AMemo memo);

    @Query("SELECT * FROM amemo ORDER BY modDate DESC")
    List<AMemo> getAllMemos();

    @Update
    void update(AMemo aMemo);
}
