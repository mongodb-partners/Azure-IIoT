package com.example.sensorapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlertsRCView extends RecyclerView.Adapter<AlertsRCView.ViewHolder> {

    private final List<String> mFailureData;
    private final List<String> mDeviceName;
    private final List<String> mDate;
    private final LayoutInflater mInflater;
    private final Boolean misAckList;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    AlertsRCView(Context context, List<String> failure_data, List<String> device_name, List<String> date, Boolean isAckList) {
        this.mInflater = LayoutInflater.from(context);
        this.mFailureData = failure_data;
        this.mDeviceName = device_name;
        this.mDate = date;
        this.misAckList = isAckList;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String failure_type = mFailureData.get(position);
        String device_name = mDeviceName.get(position);
        String dates = mDate.get(position);
        holder.failureType.setText(failure_type);
        holder.deviceName.setText(device_name);
        holder.timestamp.setText(dates);

        if(misAckList) {
            holder.itemView.setBackgroundResource(R.drawable.bg_ack_card);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mFailureData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView failureType;
        TextView deviceName;
        TextView timestamp;

        ViewHolder(View itemView) {
            super(itemView);
            failureType = itemView.findViewById(R.id.failure_type);
            deviceName = itemView.findViewById(R.id.device_name);
            timestamp = itemView.findViewById(R.id.timestamp);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mFailureData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
