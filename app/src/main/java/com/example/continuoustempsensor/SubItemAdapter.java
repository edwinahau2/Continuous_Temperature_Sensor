package com.example.continuoustempsensor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubItemAdapter extends RecyclerView.Adapter<SubItemAdapter.SubItemViewHolder> {

    private List<SubItem> subItemList;

    public SubItemAdapter(List<SubItem> subItemList) {
        this.subItemList = subItemList;
    }

    @NonNull
    @Override
    public SubItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_days, parent, false);
        return new SubItemViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull SubItemViewHolder holder, int position) {
        SubItem subItem = subItemList.get(position);
        holder.Week.setText(subItem.getWeekDay());
        holder.Temp.setText(subItem.getTemp());
    }

    @Override
    public int getItemCount() {
        return subItemList.size();
    }

    public class SubItemViewHolder extends RecyclerView.ViewHolder {

        TextView Week, Temp;

        public SubItemViewHolder(View itemView) {
            super(itemView);
            Week = itemView.findViewById(R.id.theDay);
            Temp = itemView.findViewById(R.id.theTemp);
        }

    }
}
