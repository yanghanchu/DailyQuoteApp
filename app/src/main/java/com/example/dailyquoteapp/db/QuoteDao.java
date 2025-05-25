package com.example.dailyquoteapp.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QuoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(QuoteEntity quote);

    @Query("SELECT * FROM quotes WHERE category = :cat")
    List<QuoteEntity> loadByCategory(String cat);

    @Query("SELECT * FROM quotes")
    List<QuoteEntity> loadAll();
}
