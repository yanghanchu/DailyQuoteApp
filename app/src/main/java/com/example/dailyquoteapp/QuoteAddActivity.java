package com.example.dailyquoteapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class QuoteAddActivity extends AppCompatActivity {

    private EditText editQuoteContent, editQuoteAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_add);

        editQuoteContent = findViewById(R.id.edit_quote_content);
        editQuoteAuthor = findViewById(R.id.edit_quote_author);
        Button btnSave = findViewById(R.id.btn_save_quote);

        btnSave.setOnClickListener(v -> {
            String text = editQuoteContent.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "请输入语录内容", Toast.LENGTH_SHORT).show();
                return;
            }
            String author = editQuoteAuthor.getText().toString().trim();
            SharedPreferences sp = getSharedPreferences("my_quotes", MODE_PRIVATE);
            sp.edit()
                    .putString("custom_quote_text", text)
                    .putString("custom_quote_author", author.isEmpty() ? "佚名" : author)
                    .apply();
            Toast.makeText(this, "语录已保存，下次展示时可见", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
