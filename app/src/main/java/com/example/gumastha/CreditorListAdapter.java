package com.example.gumastha;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gumastha.Model.Creditor;
import com.example.gumastha.Model.Product;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.TreeMap;

public class CreditorListAdapter extends RecyclerView.Adapter<CreditorListAdapter.ViewHolder> {
    Context context;
    ArrayList<Creditor>list;

    public CreditorListAdapter(Context context, ArrayList<Creditor> list) {
        this.context=context;
        this.list=list;

    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View listItem = layoutInflater.inflate(R.layout.credit_list_item, parent, false);
        CreditorListAdapter.ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CreditorListAdapter.ViewHolder holder, int position) {
        Creditor creditor=list.get(position);
        holder.inname.setText(creditor.name.substring(0,2));
        holder.name.setText(creditor.name);
        holder.amount.setText(String.valueOf(creditor.amount));
        holder.phone.setText(creditor.number);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,Creditor_Account.class);
                i.putExtra("Creditor",creditor);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView inname,name,phone,amount;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            inname=itemView.findViewById(R.id.inName);
            name=itemView.findViewById(R.id.creditName);
            phone=itemView.findViewById(R.id.creditPhone);
            amount=itemView.findViewById(R.id.creditAmount);
        }
    }
}
