package com.example.dailyquoteapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView textQuote, textAuthor;
    private final List<String[]> quoteList = new ArrayList<>();
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textQuote = findViewById(R.id.text_quote);
        textAuthor = findViewById(R.id.text_author);

        Button btnNext = findViewById(R.id.btn_next);
        Button btnFav = findViewById(R.id.btn_fav);
        Button btnShare = findViewById(R.id.btn_share);
        Button btnCustomAdd = findViewById(R.id.btn_custom_add);

        loadQuotesFromAssets();
        loadUserQuote();

        showRandomQuote();

        btnNext.setOnClickListener(v -> showRandomQuote());
        btnCustomAdd.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, QuoteAddActivity.class))
        );
        // btnFav / btnShare 的逻辑可在此扩展
    }

    private void loadQuotesFromAssets() {
        try {
            AssetManager am = getAssets();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(am.open("quotes.json"))
            );
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            JSONArray arr = new JSONArray(sb.toString());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                quoteList.add(new String[]{
                        obj.getString("text"),
                        obj.optString("author", "佚名")
                });
            }
        } catch (Exception e) {
            Log.e("MainActivity", "加载本地语录失败", e);
        }
    }

    private void loadUserQuote() {
        SharedPreferences sp = getSharedPreferences("my_quotes", MODE_PRIVATE);
        String q = sp.getString("custom_quote_text", null);
        if (q != null) {
            String a = sp.getString("custom_quote_author", "佚名");
            quoteList.add(new String[]{q, a});
        }
    }

    private void showRandomQuote() {
        if (quoteList.isEmpty()) return;
        String[] qa = quoteList.get(random.nextInt(quoteList.size()));
        textQuote.setText(qa[0]);
        textAuthor.setText("—— " + qa[1]);
    }
}
