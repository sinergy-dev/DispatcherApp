package com.example.appdispatcher.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdispatcher.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private ArrayList titleList, messageList, dateList;
    private Context context;

    public NotificationAdapter(Context context, ArrayList titleList, ArrayList messageList, ArrayList dateList) {
        this.titleList = titleList;
        this.messageList = messageList;
        this.dateList = dateList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_notification_item, parent, false);
        return new ViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.title.setText(String.valueOf(titleList.get(position)));
        holder.message.setText(String.valueOf(messageList.get(position)));
        holder.datetime.setText(String.valueOf(dateList.get(position)));
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, message, datetime;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            message = itemView.findViewById(R.id.itemMessage);
            datetime = itemView.findViewById(R.id.itemdate_time);
        }
    }
}
