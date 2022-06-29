package com.example.gumastha;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gumastha.Model.Creditor_Transaction;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CreditorTransactionsAdapter extends RecyclerView.Adapter<CreditorTransactionsAdapter.ViewHolder> {
    Context context;
    ArrayList<Creditor_Transaction>list;
    public CreditorTransactionsAdapter(Context applicationContext, ArrayList<Creditor_Transaction> list) {
        context=applicationContext;
        this.list=list;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.chat_item,null);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CreditorTransactionsAdapter.ViewHolder holder, int position) {
        Creditor_Transaction creditor_transaction=list.get(position);
        System.out.println("Creditor Transaction: " +creditor_transaction);
        if(creditor_transaction.mode.equalsIgnoreCase("given")){
            holder.taken.setVisibility(View.GONE);
            holder.chat.setGravity(Gravity.LEFT);
            holder.given.setVisibility(View.VISIBLE);
            holder.gdesc.setText("An amount of  Rs "+creditor_transaction.amount+"/- has been given as credit on Transaction Id : "+creditor_transaction.TID);
            holder.gtime.setText(creditor_transaction.date);

        }else{
            holder.given.setVisibility(View.GONE);
            holder.chat.setGravity(Gravity.RIGHT);
            holder.taken.setVisibility(View.VISIBLE);
            holder.tdesc.setText("An amount of  Rs "+creditor_transaction.amount+"/- has been reduced");
           holder.ttime.setText(creditor_transaction.date);


        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView gdesc,gtime,tdesc,ttime;
        LinearLayout given,taken,chat;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            gdesc=itemView.findViewById(R.id.giventext);
            gtime=itemView.findViewById(R.id.giventime);
            tdesc=itemView.findViewById(R.id.takentext);
            ttime=itemView.findViewById(R.id.takentime);
            given=itemView.findViewById(R.id.given);
            taken=itemView.findViewById(R.id.taken);
            chat=itemView.findViewById(R.id.chatlayout);

        }
    }
}
