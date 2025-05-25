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

import com.example.dailyquoteapp.net.ApiClient;
import com.example.dailyquoteapp.net.HitokotoResponse;
import com.example.dailyquoteapp.net.QuoteApiService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView textQuote, textAuthor;
    private final List<String[]> quoteList = new ArrayList<>();
    private final Random random = new Random();

    private String[] latestNetQuote = null;

    private static final String PREFS_CUSTOM = "my_quotes";
    private static final String KEY_CUSTOM_TEXT = "custom_quote_text";
    private static final String KEY_CUSTOM_AUTHOR = "custom_quote_author";

    private static final String PREFS_FAV = "fav_quotes";
    private static final String KEY_FAV_LIST = "fav_list";

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
        Button btnFavList = findViewById(R.id.btn_fav_list);
        Button btnAllQuotes = findViewById(R.id.btn_all_quotes);

        loadQuotesFromAssets();
        loadUserQuote();
        fetchOnlineQuote(); // ✅ 从 hitokoto.cn 获取语录
        showRandomQuote();

        btnNext.setOnClickListener(v -> showRandomQuote());

        btnFav.setOnClickListener(v -> addCurrentToFavorite());

        btnShare.setOnClickListener(v -> {
            String shareText = textQuote.getText() + "\n" + textAuthor.getText();
            Intent share = new Intent(Intent.ACTION_SEND)
                    .setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(share, "分享到"));
        });

        btnCustomAdd.setOnClickListener(v ->
                startActivity(new Intent(this, QuoteAddActivity.class))
        );

        btnFavList.setOnClickListener(v ->
                startActivity(new Intent(this, FavoriteActivity.class))
        );

        btnAllQuotes.setOnClickListener(v ->
                startActivity(new Intent(this, AllQuotesActivity.class))
        );
    }

    private void loadQuotesFromAssets() {
        try {
            AssetManager am = getAssets();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(am.open("quotes.json"), "UTF-8")
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
            Toast.makeText(this, "加载本地语录失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserQuote() {
        SharedPreferences sp = getSharedPreferences(PREFS_CUSTOM, MODE_PRIVATE);
        String q = sp.getString(KEY_CUSTOM_TEXT, null);
        if (q != null) {
            String a = sp.getString(KEY_CUSTOM_AUTHOR, "佚名");
            quoteList.add(new String[]{q, a});
        }
    }

    /** 新增：从 hitokoto.cn 获取一句网络语录 */
    private void fetchOnlineQuote() {
        new Thread(() -> {
            try {
                QuoteApiService service = ApiClient.getChineseService();
                Call<HitokotoResponse> call = service.fetchChinese();
                Response<HitokotoResponse> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    HitokotoResponse data = resp.body();
                    latestNetQuote = new String[]{data.hitokoto, data.from};
                    quoteList.add(latestNetQuote); // ✅ 加入显示池
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showRandomQuote() {
        if (quoteList.isEmpty()) return;
        String[] qa = quoteList.get(random.nextInt(quoteList.size()));
        textQuote.setText(qa[0]);
        textAuthor.setText("—— " + qa[1]);
    }

    private void addCurrentToFavorite() {
        String text = textQuote.getText().toString();
        String author = textAuthor.getText().toString().replaceFirst("^——\\s*", "");

        try {
            SharedPreferences sp = getSharedPreferences(PREFS_FAV, MODE_PRIVATE);
            String raw = sp.getString(KEY_FAV_LIST, "[]");
            JSONArray favArr = new JSONArray(raw);

            for (int i = 0; i < favArr.length(); i++) {
                JSONObject obj = favArr.getJSONObject(i);
                if (text.equals(obj.getString("text"))
                        && author.equals(obj.getString("author"))) {
                    Toast.makeText(this, "已在收藏列表中", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            JSONObject newObj = new JSONObject();
            newObj.put("text", text);
            newObj.put("author", author);
            favArr.put(newObj);

            sp.edit().putString(KEY_FAV_LIST, favArr.toString()).apply();
            Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("MainActivity", "收藏失败", e);
            Toast.makeText(this, "收藏失败", Toast.LENGTH_SHORT).show();
        }
    }
}
