package com.example.gumastha;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gumastha.Controller.FirebaseUtils;
import com.example.gumastha.Model.Profit_data;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Profit extends AppCompatActivity implements  ProfitInterface{
    RecyclerView profitlist;
    TextView totalProfit,todays_profit;
    ImageView profitback;
    FirebaseFirestore db=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));
        profitlist=findViewById(R.id.profitlist);
        profitback=findViewById(R.id.profit_back);
        totalProfit=findViewById(R.id.total_profit);
        todays_profit=findViewById(R.id.todays_profit);
        new FirebaseUtils(this).getProfitDetails();
        profitback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        db.collection("Total_Profit").document("Profit").
                get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            totalProfit.setText("Total Profit : "+String.format("%.2f",task.getResult().get("Profit",Double.class)));
                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setRecyclerview(ArrayList<Profit_data> list) {
        setTodaysProfit(list);
        if(list.size()==0){
            Toast.makeText(getApplicationContext(),"No Data available ",Toast.LENGTH_LONG).show();
        }else{
            profitlist.setLayoutManager(new LinearLayoutManager(this));
            profitlist.setAdapter(new ProfitListAdapter(getApplicationContext(),list));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setTodaysProfit(ArrayList<Profit_data> list) {
        double amount=0;
        LocalDate myObj = LocalDate.now();
        String transactionDay=myObj.toString();
        for (Profit_data p:
             list) {
            if(p.date.equals(transactionDay)){
                amount+=p.Profit;
            }
        }
        todays_profit.setText("Todays's Profit : "+String.format("%.2f",amount));
    }
}