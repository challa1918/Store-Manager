package com.example.gumastha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.L;
import com.example.gumastha.Model.Transaction;

public class Transaction_Display extends AppCompatActivity {
    RecyclerView billlist;
    TextView tid,date,amount;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_display);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }
        tid=findViewById(R.id.tid);
        date=findViewById(R.id.tdate);
        back=findViewById(R.id.tdback);
        amount=findViewById(R.id.tamount);
        billlist=findViewById(R.id.bill_list);
        Transaction t = (Transaction) getIntent().getSerializableExtra("Transaction");
        billlist.setLayoutManager(new LinearLayoutManager(this));
        billlist.setAdapter(new BillListAdapter(getApplicationContext(),t));
        tid.setText("Transaction ID: "+t.TID);
        String Timeago= (String) DateUtils.getRelativeTimeSpanString(t.seconds*1000);
        date.setText(Timeago);
        amount.setText("Amount : "+t.billAmount);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}