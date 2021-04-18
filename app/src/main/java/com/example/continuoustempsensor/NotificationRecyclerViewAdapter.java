package com.example.continuoustempsensor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.MyViewHolder> {

    List<NotifItem> data;

    public NotificationRecyclerViewAdapter(List<NotifItem> list) {
        this.data = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notif_cards, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle, mTime;
        private ImageView mImg;
        public MyViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.txtTitle);
            mTime = itemView.findViewById(R.id.txtTime);
            mImg = itemView.findViewById(R.id.hotANDcold);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mTitle.setText(data.get(position).getTitle());
        holder.mTime.setText(data.get(position).getDescription());
        holder.mImg.setImageResource(data.get(position).getImg());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(String title, String subtitle, int drawable, int position) {
        data.add(position, new NotifItem(drawable, title, subtitle));
        notifyItemInserted(position);
    }
}
