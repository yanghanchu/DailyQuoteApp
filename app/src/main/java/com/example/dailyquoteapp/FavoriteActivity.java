package com.example.dailyquoteapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private static final String PREFS_FAV    = "fav_quotes";
    private static final String KEY_FAV_LIST = "fav_list";

    private ListView lvFav;
    private List<String> displayList;       // 用于 ArrayAdapter 显示
    private List<JSONObject> jsonList;      // 原始 JSON 对象，用于删除后重建
    private ArrayAdapter<String> adapter;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        sp = getSharedPreferences(PREFS_FAV, MODE_PRIVATE);
        lvFav = findViewById(R.id.list_favorites);

        // 1. 读取并解析 SharedPreferences 中的收藏 JSON
        displayList = new ArrayList<>();
        jsonList    = new ArrayList<>();
        String raw = sp.getString(KEY_FAV_LIST, "[]");
        try {
            JSONArray favArr = new JSONArray(raw);
            for (int i = 0; i < favArr.length(); i++) {
                JSONObject obj = favArr.getJSONObject(i);
                jsonList.add(obj);
                String text   = obj.getString("text");
                String author = obj.getString("author");
                displayList.add(text + "\n—— " + author);
            }
        } catch (Exception ignored) { }

        // 2. 用 ArrayAdapter 绑定 ListView
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                displayList
        );
        lvFav.setAdapter(adapter);

        // 3. 设置长按删除
        lvFav.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                confirmDelete(position);
                return true;  // 表示事件已消费
            }
        });
    }

    /** 弹出确认框，删除指定位置的收藏 */
    private void confirmDelete(int pos) {
        new AlertDialog.Builder(this)
                .setTitle("删除收藏")
                .setMessage("确定要删除这条收藏吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAt(pos);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /** 真正执行删除并同步到 SharedPreferences */
    private void removeAt(int pos) {
        // 从两个列表中移除
        jsonList.remove(pos);
        displayList.remove(pos);
        adapter.notifyDataSetChanged();

        // 重建 JSON 数组并保存
        JSONArray newArr = new JSONArray();
        for (JSONObject obj : jsonList) {
            newArr.put(obj);
        }
        sp.edit()
                .putString(KEY_FAV_LIST, newArr.toString())
                .apply();
    }
}
