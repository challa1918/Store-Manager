package com.example.gumastha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gumastha.Model.Profit_data;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfitListAdapter extends RecyclerView.Adapter<ProfitListAdapter.ViewHolder> {
    Context context;
    ArrayList<Profit_data>list;
    public ProfitListAdapter(Context applicationContext, ArrayList<Profit_data> list) {
        this.context=applicationContext;
        this.list=list;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.profit_list_item,null);
        ViewHolder viewHolder= new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProfitListAdapter.ViewHolder holder, int position) {
        Profit_data profit_data=list.get(position);
        holder.date.setText(profit_data.date);
        holder.amount.setText(String.format("%.2f",profit_data.Profit));


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date,amount;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.pdate);
            amount=itemView.findViewById(R.id.pamount);
        }
    }
}
