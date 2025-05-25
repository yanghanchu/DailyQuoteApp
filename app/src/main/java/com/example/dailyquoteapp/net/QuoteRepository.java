package com.example.dailyquoteapp.net;

import android.content.Context;

import androidx.room.Room;

import com.example.dailyquoteapp.db.QuoteDao;
import com.example.dailyquoteapp.db.QuoteEntity;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class QuoteRepository {
    private final QuoteDao dao;

    public QuoteRepository(Context context) {
        dao = Room.databaseBuilder(context, com.example.dailyquoteapp.db.AppDatabase.class, "quote_db")
                .allowMainThreadQueries()
                .build()
                .quoteDao();
    }

    public void fetchAndSaveChinese() {
        try {
            Call<HitokotoResponse> call = ApiClient.getChineseService().fetchChinese();
            Response<HitokotoResponse> resp = call.execute();
            if (resp.isSuccessful() && resp.body() != null) {
                HitokotoResponse data = resp.body();
                QuoteEntity quote = new QuoteEntity(data.hitokoto, data.from, "Chinese");
                dao.insert(quote);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCustomToDb() {
        List<QuoteEntity> all = dao.loadByCategory("Custom");
        if (all.isEmpty()) {
            dao.insert(new QuoteEntity("This is your first custom quote!", "You", "Custom"));
        }
    }

    public List<QuoteEntity> getByCategory(String cat) {
        return dao.loadByCategory(cat);
    }

}