package com.example.gumastha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gumastha.Model.BillProduct;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.ViewHolder> {
    Context context;
    View view;
    ArrayList<BillProduct>list;
    ArrayList<Double>singleBill;
    public CheckOutAdapter(Context context, ArrayList<BillProduct> list, ArrayList<Double> singleBill) {
        this.context=context;
        this.list=list;
        this.singleBill=singleBill;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(context).inflate(R.layout.bill_list_item,parent,false);
        ViewHolder viewHolder= new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CheckOutAdapter.ViewHolder holder, int position) {
        BillProduct product=list.get(position);
        holder.pname.setText(product.product.getPRODUCT_NAME());
        holder.pquant.setText( String.valueOf(product.quantity));
        holder.pprice.setText(String.valueOf(singleBill.get(position)));
        holder.pricedisplay.setText("("+product.product.getSELLING_PRICE()+")");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView pname,pquant,pprice,pricedisplay;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            pname=itemView.findViewById(R.id.billpname);
            pquant=itemView.findViewById(R.id.billquant);
            pprice=itemView.findViewById(R.id.billprice);
            pricedisplay=itemView.findViewById(R.id.pricingdis);
        }
    }
}
