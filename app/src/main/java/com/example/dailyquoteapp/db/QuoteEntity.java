package com.example.dailyquoteapp.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quotes")
public class QuoteEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String text;
    public String author;
    public String category;  // "Chinese" or "Custom"

    public QuoteEntity(String text, String author, String category) {
        this.text = text;
        this.author = author;
        this.category = category;
    }
}

