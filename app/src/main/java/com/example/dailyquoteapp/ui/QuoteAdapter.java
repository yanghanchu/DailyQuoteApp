package com.example.dailyquoteapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyquoteapp.R;
import com.example.dailyquoteapp.db.QuoteEntity;

import java.util.List;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.VH> {
    private final List<QuoteEntity> data;
    public QuoteAdapter(List<QuoteEntity> list) {
        data = list;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quote, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        QuoteEntity q = data.get(position);
        holder.tvText.setText("“" + q.text + "”");
        holder.tvAuthor.setText("—— " + q.author);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvText, tvAuthor;
        VH(View v) {
            super(v);
            tvText = v.findViewById(R.id.tv_quote);
            tvAuthor = v.findViewById(R.id.tv_author);
        }
    }
}
