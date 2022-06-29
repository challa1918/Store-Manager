package com.example.gumastha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gumastha.Controller.FirebaseUtils;
import com.example.gumastha.Model.Transaction;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class Transactions extends AppCompatActivity implements  TransactionActivityInterface{
    RecyclerView transactionlist;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }
        back=findViewById(R.id.transaction_back);
        transactionlist=findViewById(R.id.transaction_list);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        itemDecor.setDrawable(getDrawable(R.drawable.divider));
        transactionlist.addItemDecoration(itemDecor);
        new FirebaseUtils(this).getTransactions("Transactions_Activity");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void setTransactionList(ArrayList<Transaction> list) {
        if(list.size()>0){
            transactionlist.setLayoutManager(new LinearLayoutManager(this));
            transactionlist.setAdapter(new transactionListAdapter(this,list));
        }else{
            Toast.makeText(this,"No Recent Transactions",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void NoTransactionsAvailableError() {
                Toast.makeText(getApplicationContext(),"NO transactions available",Toast.LENGTH_LONG).show();
        }
}