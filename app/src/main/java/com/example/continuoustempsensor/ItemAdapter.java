package com.example.continuoustempsensor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.RecursiveAction;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<Item> itemList;

    ItemAdapter (List<Item> items) {
        this.itemList = items;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards, parent, false);
        return new ItemViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.tvItemTitle.setText(item.getWeek());
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.rvSubItem.getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setInitialPrefetchItemCount(item.getSubItemList().size());
        SubItemAdapter subItemAdapter = new SubItemAdapter(item.getSubItemList());
        holder.rvSubItem.setLayoutManager(layoutManager);
        holder.rvSubItem.setAdapter(subItemAdapter);
        holder.rvSubItem.setRecycledViewPool(viewPool);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvItemTitle;
        RecyclerView rvSubItem;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemTitle = itemView.findViewById(R.id.item_title);
            rvSubItem = itemView.findViewById(R.id.rv_sub_item);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
