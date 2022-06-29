package com.example.gumastha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.gumastha.Controller.FirebaseUtils;
import com.example.gumastha.Model.Creditor;
import com.example.gumastha.Model.Creditor_Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Creditor_Account extends AppCompatActivity implements CreditAccountInterface {
    TextView inname,name,amount;
    ImageView back;
    Toolbar toolbar;
    Button taken;
    EditText inputamount;
    RecyclerView caccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditor_account);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }
        toolbar = (Toolbar) findViewById(R.id.creditor_toolbar);
        toolbar.inflateMenu(R.menu.credit_menu);
        taken=findViewById(R.id.takenbutton);
        inputamount=findViewById(R.id.inputamount);
        Creditor creditor=(Creditor) getIntent().getSerializableExtra("Creditor");
        System.out.println(creditor);
        inname=findViewById(R.id.creditinName);
        caccount=findViewById(R.id.caccount);
        name=findViewById(R.id.creditName);
        amount=findViewById(R.id.camount);
        back=findViewById(R.id.creditback);
        inname.setText(creditor.name.substring(0,2));
        name.setText(creditor.name);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==R.id.delete_account)
                {
                    MaterialAlertDialogBuilder materialAlertDialogBuilder=new MaterialAlertDialogBuilder(Creditor_Account.this);
                    materialAlertDialogBuilder.setTitle("Delete");
                    materialAlertDialogBuilder.setMessage("Are you sure want to delete account ??");
                    materialAlertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new FirebaseUtils(new Creditor_Account()).deleteCreditorAccount(creditor.name);
                            finish();
                        }
                    });
                    materialAlertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                materialAlertDialogBuilder.create().dismiss();
                        }
                    });
                    materialAlertDialogBuilder.create().show();
                }

                return false;
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        new FirebaseUtils(this).getcreditamount(creditor.name);

        new FirebaseUtils(this).getCreditorTransactions(creditor.name);
        taken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputamount.getText().toString().equals("")){
                    inputamount.setError("Enter Amount ");
                    inputamount.requestFocus();
                }else if(creditor.amount==0.0||creditor.amount<Double.parseDouble(inputamount.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Can't accept amount !! As there is no credit to be paid or less amount",Toast.LENGTH_LONG).show();
                    inputamount.setText("");
                }else{
                    FirebaseFirestore db= FirebaseFirestore.getInstance();
                    Creditor_Transaction creditor_transaction=new Creditor_Transaction();
                    creditor_transaction.generatedBy=new FirebaseUtils().getCurrentUser().getDisplayName();
                    creditor_transaction.mode="taken";
                    creditor_transaction.amount=Integer.parseInt(inputamount.getText().toString());
                    creditor_transaction.date=new FirebaseUtils().getCurrentTimeStamp();
                    db.collection("Creditors_Transactions").document(creditor.name).collection(creditor.name)
                    .document(String.valueOf(creditor_transaction.date))
                    .set(creditor_transaction)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            db.collection("Creditors").document(creditor.name).update("amount", FieldValue.increment(-Integer.parseInt(inputamount.getText().toString())));
                            Toast.makeText(getApplicationContext(),"Account updated Successfully",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

    }

    @Override
    public void setRecyclerview(ArrayList<Creditor_Transaction> list) {

        if(list.size()==0){
            Toast.makeText(getApplicationContext(),"No Transactions Left",Toast.LENGTH_LONG).show();
            caccount.setLayoutManager(new LinearLayoutManager(this));
            caccount.setAdapter(new CreditorTransactionsAdapter(Creditor_Account.this.getApplicationContext(),list));

        }else{
            caccount.setLayoutManager(new LinearLayoutManager(this));
            caccount.setAdapter(new CreditorTransactionsAdapter(Creditor_Account.this.getApplicationContext(),list));
        }
    }

    @Override
    public void creditorsAmount(String status) {
            amount.setText("Total credit amount : "+status);
    }

    @Override
    public void deleteStatus(String status) {
        //Toast.makeText(Creditor_Account.this,status,Toast.LENGTH_LONG).show();
    }


}