package com.example.dailyquoteapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    // 收藏用 SharedPreferences 名称 & 键
    private static final String PREFS_FAV = "fav_quotes";
    private static final String KEY_FAV_LIST = "fav_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textQuote  = findViewById(R.id.text_quote);
        textAuthor = findViewById(R.id.text_author);

        Button btnNext      = findViewById(R.id.btn_next);
        Button btnFav       = findViewById(R.id.btn_fav);
        Button btnShare     = findViewById(R.id.btn_share);
        Button btnCustomAdd = findViewById(R.id.btn_custom_add);
        Button btnFavList   = findViewById(R.id.btn_fav_list);  // 查看收藏列表
        Button btnAllQuotes = findViewById(R.id.btn_all_quotes);

        loadQuotesFromAssets();
        loadUserQuote();
        showRandomQuote();

        btnNext.setOnClickListener(v -> showRandomQuote());
        btnCustomAdd.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, QuoteAddActivity.class))
        );
        btnFav.setOnClickListener(v -> addCurrentToFavorite());
        btnFavList.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, FavoriteActivity.class))
        );
        // btnShare / btnAllQuotes 可按需扩展
    }

    private void loadQuotesFromAssets() {
        try {
            AssetManager am = getAssets();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(am.open("quotes.json"))
            );
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

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

    /** 将当前显示的语录加入“我的收藏” */
    private void addCurrentToFavorite() {
        String text   = textQuote.getText().toString();
        String author = textAuthor.getText().toString().replaceFirst("^——\\s*", "");

        try {
            SharedPreferences sp = getSharedPreferences(PREFS_FAV, MODE_PRIVATE);
            String raw = sp.getString(KEY_FAV_LIST, "[]");
            JSONArray favArr = new JSONArray(raw);

            // 检查是否已收藏，去重
            for (int i = 0; i < favArr.length(); i++) {
                JSONObject obj = favArr.getJSONObject(i);
                if (text.equals(obj.getString("text"))
                        && author.equals(obj.getString("author"))) {
                    Toast.makeText(this, "已在收藏列表中", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // 新增到数组
            JSONObject newObj = new JSONObject();
            newObj.put("text", text);
            newObj.put("author", author);
            favArr.put(newObj);

            // 存回 SharedPreferences
            sp.edit()
                    .putString(KEY_FAV_LIST, favArr.toString())
                    .apply();

            Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("MainActivity", "收藏语录失败", e);
            Toast.makeText(this, "收藏失败", Toast.LENGTH_SHORT).show();
        }
    }
}
