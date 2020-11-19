package com.example.continuoustempsensor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BtAdapter extends RecyclerView.Adapter<BtAdapter.BtViewHolder> {

    Context mContext;
    List<BtDevice> mData;
    private OnDeviceListener mOnDeviceListener;

    public BtAdapter(Context mContext, List<BtDevice> mData, OnDeviceListener onDeviceListener) {
        this.mContext = mContext;
        this.mData = mData;
        this.mOnDeviceListener = onDeviceListener;
    }

    @NonNull
    @Override
    public BtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layout;
        layout = LayoutInflater.from(mContext).inflate(R.layout.item_devices, parent, false);
        return new BtViewHolder(layout, mOnDeviceListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BtViewHolder holder, final int position) {
        holder.tv_title.setText(mData.get(position).getDevice());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class BtViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_title;
        OnDeviceListener onDeviceListener;

        public BtViewHolder(View itemView, OnDeviceListener onDeviceListener) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            this.onDeviceListener = onDeviceListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onDeviceListener.onDeviceClick(getAdapterPosition());
        }
    }

    public interface OnDeviceListener {
        void onDeviceClick(int position);
    }

}
