package com.example.gumastha;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gumastha.Controller.FirebaseUtils;
import com.example.gumastha.Model.BillProduct;
import com.example.gumastha.Model.Creditor;
import com.example.gumastha.Model.Creditor_Transaction;
import com.example.gumastha.Model.Transaction;
import com.example.gumastha.View.Home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;

public class ActivityCreditorListAdapter extends RecyclerView.Adapter<ActivityCreditorListAdapter.ViewHolder> {
    Context context;
    ArrayList<Creditor>list;
    Transaction t;
    AddToCreditorsInterface addToCreditorsInterface;

    public ActivityCreditorListAdapter(Context context, ArrayList<Creditor> list, Transaction t, AddToCreditorsInterface addToCreditorsInterface) {
        this.context=context;
        this.list=list;
        this.t=t;
        this.addToCreditorsInterface=addToCreditorsInterface;

    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View listItem = layoutInflater.inflate(R.layout.credit_list_item, parent, false);
        ActivityCreditorListAdapter.ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }
    public void filterList(ArrayList<Creditor> list) {
        this.list =list;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull ActivityCreditorListAdapter.ViewHolder holder, int position) {
        Creditor creditor=list.get(position);
        holder.inname.setText(creditor.name.substring(0,2));
        holder.name.setText(creditor.name);
        holder.amount.setText(String.valueOf(creditor.amount));
        holder.phone.setText(creditor.number);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Creditor_Transaction creditor_transaction=new Creditor_Transaction();
                MaterialAlertDialogBuilder materialAlertDialogBuilder=new MaterialAlertDialogBuilder(context);
                materialAlertDialogBuilder.setTitle("Confirmation");
                materialAlertDialogBuilder.setMessage("Are you sure do you want to add credit to "+creditor.name+" ?");
                materialAlertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        creditor_transaction.generatedBy=new FirebaseUtils().getCurrentUser().getDisplayName();
                        creditor_transaction.mode="given";
                        creditor_transaction.amount=t.billAmount;
                        creditor_transaction.TID=t.TID;
                        creditor_transaction.date=new FirebaseUtils().getCurrentTimeStamp();
                        FirebaseFirestore db=FirebaseFirestore.getInstance();
                        t.transactionMode="Credit : "+creditor.name;
                        db.collection("Creditors").document(creditor.name).update("amount",FieldValue.increment(t.billAmount));
                        db.collection("Creditors_Transactions").document(creditor.name).collection(creditor.name).document(creditor_transaction.date).set(creditor_transaction)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        db.collection("Transactions")
                                                .document(String.valueOf(t.TID))
                                                .set(t)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                        addToCreditorsInterface.finishActivity();
                                                       Toast.makeText(context, "Transaction updated", Toast.LENGTH_LONG).show();
                                                        db.collection("TID").document("TID").update("TID", FieldValue.increment(1));
                                                        updateInventory();
                                                        Intent i = new Intent(context, Home.class);
                                                        context.startActivity(i);

                                                    }
                                                });
                                    }
                                });

                    }
                });
                materialAlertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        materialAlertDialogBuilder.create().cancel();
                    }
                });
                materialAlertDialogBuilder.create().show();

            }
        });

    }

    private void updateInventory() {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        FirebaseUser user=new FirebaseUtils().getCurrentUser();
        db.collection(user.getDisplayName()+" Cart").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document:
                                task.getResult()) {
                            BillProduct productBilling=document.toObject(BillProduct.class);
                            db.collection(user.getDisplayName()+" Cart").document(productBilling.product.getBARCODE()).delete();
                            db.collection("Inventory").document(productBilling.product.getBARCODE()).update("stock_AVAILABLE",FieldValue.increment(-productBilling.quantity));

                        }
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
