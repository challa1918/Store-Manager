package com.example.gumastha;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.airbnb.lottie.L;
import com.example.gumastha.Controller.FirebaseUtils;
import com.example.gumastha.Model.Creditor;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gumastha.databinding.ActivityCreditorsBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Creditors extends AppCompatActivity implements CreditorInterface {

    private ActivityCreditorsBinding binding;
    FloatingActionButton fab;
    Dialog dialog;
    FirebaseUtils firebaseUtils;
    RecyclerView creditorList;
    ImageView back;
    TextView totalcredit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog=new Dialog(Creditors.this);
        binding = ActivityCreditorsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = this.getWindow();
        firebaseUtils=new FirebaseUtils(this);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        back=findViewById(R.id.creditors_back);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle("Creditors");
        totalcredit=findViewById(R.id.total_credit_amount);
        creditorList=findViewById(R.id.creditors_list);
        fab=findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showPopUp( Creditors.this);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        firebaseUtils.getCreditors("Creditors");


    }

    private void showPopUp(Context context) {
       View view=getLayoutInflater().inflate(R.layout.creditor_popup,null);
       EditText popname=view.findViewById(R.id.popname);
       EditText phn=view.findViewById(R.id.popphnno);
       Button add=view.findViewById(R.id.addcreditor);

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
                else if(popname.getText().toString().length()<=1) {
                    popname.setError("Length of the name should be greater than three letters");
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

                    firebaseUtils.addCreditor(contact,"creditors");

                }
            }
        });

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
        if(list.size()==0){
            Toast.makeText(getApplicationContext(),"NO Creditors available",Toast.LENGTH_LONG).show();
        }else{
            Collections.sort(list, new Comparator<Creditor>() {
                @Override
                public int compare(Creditor o1, Creditor o2) {
                    return o1.name.compareTo(o2.name);
                }
            });
            System.out.println("Creditor "+list+" "+list.size());
            int i=0;
            for (Creditor  creditor:
                 list) {
                i+=creditor.amount;
            }
            totalcredit.setText("Total Credit : "+ i);
            creditorList.setLayoutManager(new LinearLayoutManager(this));
            creditorList.setAdapter(new CreditorListAdapter(Creditors.this,list));
        }
    }
}