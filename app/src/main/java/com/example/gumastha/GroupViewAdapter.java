package com.example.gumastha;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gumastha.Model.Product;
import java.util.ArrayList;
import java.util.TreeMap;

public class GroupViewAdapter extends RecyclerView.Adapter<GroupViewAdapter.ViewHolder> {

    private TreeMap<Integer, ArrayList<Product>> inventory;
    View listItem;
    Context context;

    public GroupViewAdapter(TreeMap<Integer, ArrayList<Product>> items, Context context) {
        inventory = items;
        this.context=context;
    }

    public void filterList(TreeMap<Integer, ArrayList<Product>> inventory) {
        this.inventory =inventory;
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
       listItem= layoutInflater.inflate(R.layout.group, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Product p=inventory.get(position).get(0);
        holder.categoryname.setText(String.valueOf(p.getCATEGORY()));
        MemberAdapter memberAdapter=new MemberAdapter(inventory.get(position),context);
        holder.itelmlist.setLayoutManager(new LinearLayoutManager(context));
        holder.itelmlist.setAdapter(memberAdapter);

    }

    @Override
    public int getItemCount() {
        return inventory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView categoryname;
        RecyclerView itelmlist;

        public ViewHolder(View listItem) {
            super(listItem);
            categoryname=listItem.findViewById(R.id.categoryheader);
            itelmlist=listItem.findViewById(R.id.itemslist);
        }



    }
}