package com.example.gumastha;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gumastha.Model.BillProduct;
import com.example.gumastha.Model.Product;
import com.example.gumastha.Model.Transaction;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;

import static android.content.Context.WINDOW_SERVICE;

public class BillListAdapter extends RecyclerView.Adapter<BillListAdapter.ViewHolder>  {
    Transaction t;
    View view;
    ArrayList<BillProduct>list;
    ArrayList<Double>singleBill;
    Context context;
    public BillListAdapter(Context applicationContext, Transaction t) {
        this.t=t;
        this.list=t.list;
        context=applicationContext;
        singleBill=new ArrayList<>();
        getTotalBill(list);

    }

    public BillListAdapter() {

    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item,parent,false);
        BillListAdapter.ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BillListAdapter.ViewHolder holder, int position) {
        BillProduct product=list.get(position);
        System.out.println("BillListdapter: "+product);
        holder.pname.setText(product.product.getPRODUCT_NAME());
        holder.pquant.setText("Quantity : "+String.valueOf(product.quantity));
        holder.pprice.setText("Price: "+String.valueOf(singleBill.get(position)));
        Glide.with(context).load(product.product.getURI()).into(holder.pimage);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pimage;
        TextView pname,pprice,pquant;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            pimage=itemView.findViewById(R.id.pimage);
            pname=itemView.findViewById(R.id.productName);
            pquant=itemView.findViewById(R.id.stock);
            pprice=itemView.findViewById(R.id.sep);
        }
    }
    private double getTotalBill(ArrayList<BillProduct> list) {
        double bill=0;
        for (BillProduct p:
                list) {
            Product product=p.product;
            if(product.getUNIT().equals("KG")){
                double pergram= Double.parseDouble(product.getSELLING_PRICE())/1000;
                singleBill.add(p.quantity*pergram);
                bill+= p.quantity*pergram;
            }else{
                bill+=(p.quantity*Integer.parseInt(product.getSELLING_PRICE()));
                singleBill.add((double) (p.quantity*Integer.parseInt(product.getSELLING_PRICE())));
            }

        }

        return bill;
    }
}
