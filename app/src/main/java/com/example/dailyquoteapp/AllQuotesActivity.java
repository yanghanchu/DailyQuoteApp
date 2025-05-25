package com.example.dailyquoteapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyquoteapp.db.QuoteEntity;
import com.example.dailyquoteapp.net.QuoteRepository;
import com.example.dailyquoteapp.ui.QuoteAdapter;

import java.util.Arrays;
import java.util.List;

public class AllQuotesActivity extends AppCompatActivity {

    private QuoteRepository repo;
    private RecyclerView rv;
    private Spinner spinner;
    private final List<String> categories = Arrays.asList("Chinese", "Custom");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_quotes);

        repo = new QuoteRepository(this);

        new Thread(() -> {
            repo.loadCustomToDb();
            repo.fetchAndSaveChinese();
        }).start();

        rv = findViewById(R.id.rv_quotes);
        rv.setLayoutManager(new LinearLayoutManager(this));

        spinner = findViewById(R.id.spinner_category);
        spinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        ));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                List<QuoteEntity> list = repo.getByCategory(categories.get(pos));
                rv.setAdapter(new QuoteAdapter(list));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}