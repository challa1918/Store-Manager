package com.example.gumastha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.gumastha.Controller.FirebaseUtils;
import com.example.gumastha.Model.Creditor;
import com.example.gumastha.Model.Product;
import com.example.gumastha.Model.Transaction;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Add_To_Creditor extends AppCompatActivity implements AddToCreditorsInterface{
    RecyclerView clist;
    double amount;
    Dialog dialog;
    ArrayList<Creditor>list;
    LinearLayout add_new_creditor;
    Transaction transaction;
    FirebaseUtils firebaseUtils;
    AddToCreditorsInterface addToCreditorsInterface;
    ActivityCreditorListAdapter activityCreditorListAdapter;
    android.widget.SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));
        setContentView(R.layout.activity_add_to_creditor);
        add_new_creditor=findViewById(R.id.add_new_creditor);
        list=new ArrayList<>();
        searchView=findViewById(R.id.search_creditors);
        clist=findViewById(R.id.activity_creditor_list);
        transaction=(Transaction) getIntent().getSerializableExtra("transaction");
        new FirebaseUtils(this).getCreditors("Add_To_Creditors");
        addToCreditorsInterface=new Add_To_Creditor();
        firebaseUtils=new FirebaseUtils(this);
        add_new_creditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp(Add_To_Creditor.this);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
    }

    private void filter(String newText) {
        ArrayList<Creditor>updatedlist=new ArrayList<>();
        System.out.println("Creditors List : "+list);
        for (Creditor creditor:
                list) {
            if(creditor.name.toLowerCase().contains(newText.toLowerCase()))
            {
                updatedlist.add(creditor);
            }

        }
        System.out.println("Updated Creditors List : "+updatedlist);
        if(updatedlist.size()==0){
            Toast.makeText(getApplicationContext(),"No Data Found",Toast.LENGTH_LONG).show();
        }else{
                activityCreditorListAdapter.filterList(updatedlist);
        }

    }

    @Override
    public void addingStatus(String status) {
        if(status.equalsIgnoreCase("yes")){
            Toast.makeText(getApplicationContext(),R.string.creditor_Add,Toast.LENGTH_LONG).show();
            dialog.cancel();
        }else{
            Toast.makeText(getApplicationContext(),R.string.error_creditor_add,Toast.LENGTH_LONG).show();
        }

        System.out.println("Contact added: "+status);
    }

    @Override
    public void setRecyclerView(ArrayList<Creditor> list) {
        this.list=list;
        if(list.size()==0){
            Toast.makeText(getApplicationContext(),"No Creditors Available",Toast.LENGTH_LONG).show();
        }else{
            clist.setLayoutManager(new LinearLayoutManager(this));
            activityCreditorListAdapter=new ActivityCreditorListAdapter(this,list,transaction,addToCreditorsInterface);
            clist.setAdapter(activityCreditorListAdapter);
        }

    }

    @Override
    public void finishActivity() {
        finish();
    }

    private void showPopUp(Context context) {
        View view=getLayoutInflater().inflate(R.layout.creditor_popup,null);
        EditText popname=view.findViewById(R.id.popname);
        EditText phn=view.findViewById(R.id.popphnno);
        Button add=view.findViewById(R.id.addcreditor);
        dialog=new Dialog(context);
        dialog.setContentView(view);
        dialog.setTitle("Add Creditor");
        dialog.show();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popname.getText().toString().equals("")) {
                    popname.setError("Name cannot be empty");
                    popname.requestFocus();

                }
                else if(phn.getText().toString().equals("")){
                    phn.setError("Phone number cannot be empty");
                    phn.requestFocus();
                }else if(phn.getText().toString().length()!=10){
                    phn.setError("Invalid Phone number");
                    phn.requestFocus();
                }else{
                    Creditor contact=new Creditor();
                    contact.setName(popname.getText().toString());
                    contact.setNumber(phn.getText().toString());
                    System.out.println("Creditor "+contact);

                    firebaseUtils.addCreditor(contact,"add_to_creditors");

                }
            }
        });

    }

}