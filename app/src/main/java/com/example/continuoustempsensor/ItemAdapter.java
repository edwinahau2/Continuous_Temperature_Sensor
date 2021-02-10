package com.example.continuoustempsensor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private Context mContext;
    private List<Item> itemList;

    ItemAdapter (Context context, List<Item> items) {
        this.itemList = items;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards, parent, false);
        final ItemViewHolder viewHolder = new ItemViewHolder(layout);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dataLog = new Intent(mContext.getApplicationContext(), DataLog.class);
                mContext.startActivity(dataLog);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.tvItemTitle.setText(item.getWeek());
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvItemTitle;
        CardView cardView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemTitle = itemView.findViewById(R.id.item_title);
            cardView = itemView.findViewById(R.id.cardNum);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
