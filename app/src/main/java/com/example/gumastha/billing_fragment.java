package com.example.gumastha;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gumastha.Controller.FirebaseUtils;
import com.example.gumastha.Model.Product;
import com.example.gumastha.Model.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;

public class billing_fragment extends Fragment implements BillingFragmentInterface{

    private static final String ARG_PARAM1 = "param1";
    Context context;
    private static final String ARG_PARAM2 = "param2";
    CircularImageView profile;
    ImageView transactionnext;
    FirebaseUser user;
    RecyclerView transactionlist;
    ArrayList<Transaction>list;
    TextView transactiontext,more,salestoday,totalsales,see_profits;
    ConstraintLayout billing,expandable,transaction,creditors;


    public billing_fragment() {
        // Required empty public constructor
    }

    public billing_fragment(Context context) {
        // Required empty public constructor
        this.context=context;
    }


    public static billing_fragment newInstance(String param1, String param2) {
        billing_fragment fragment = new billing_fragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        user=new FirebaseUtils().getCurrentUser();
        View view=inflater.inflate(R.layout.fragment_billing_fragment, container, false);
        profile=view.findViewById(R.id.profile);
        billing=view.findViewById(R.id.billinglayout);
        transactiontext=view.findViewById(R.id.ttext);
        transaction=view.findViewById(R.id.transactionslayout);
        expandable=view.findViewById(R.id.expandable);
        creditors=view.findViewById(R.id.creditlayout);
        salestoday=view.findViewById(R.id.salestoday);
        totalsales=view.findViewById(R.id.totalsales);
        see_profits=view.findViewById(R.id.seeprofits);
        more=view.findViewById(R.id.more);
        list=new ArrayList<>();
        transactionlist=view.findViewById(R.id.recent_transaction_list);
        transactionnext=view.findViewById(R.id.transactionnext);
        transactionnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                      expand();
            }
        });
        transactiontext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expand();
            }
        });
        new FirebaseUtils(billing_fragment.this).getTransactions("Billing");
        new FirebaseUtils(billing_fragment.this).getTransactions("Sales_Billing");
        see_profits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPassword();
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(context,Transactions.class);
                context.startActivity(i);
            }
        });
        creditors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(context,Creditors.class);
                context.startActivity(i);
            }
        });


        billing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,CartBuilding.class);
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        Glide.with(context).load(user.getPhotoUrl()).into(profile);
        return view;
    }

    private void expand() {
        if(expandable.getVisibility()==View.GONE){
            TransitionManager.beginDelayedTransition(transaction,new AutoTransition());
            expandable.setVisibility(View.VISIBLE);
            transactionnext.setRotation(90);
        }else{
            TransitionManager.beginDelayedTransition(transaction,new AutoTransition());
            expandable.setVisibility(View.GONE);
            transactionnext.setRotation(360);
        }
    }

    @Override
    public void setTransactionList(ArrayList<Transaction> list) {
        
        if(list.size()>0){
            transactionlist.setLayoutManager(new LinearLayoutManager(context));
            transactionlist.setAdapter(new transactionListAdapter(context,list));
        }else{
            Toast.makeText(context,"No Recent Transactions",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void setSales(ArrayList<Transaction> list) {
        double st=0,ts = 0;
        for (Transaction transaction:
             list) {
            ts+=transaction.billAmount;
            String date[]= new Date().toString().split(" ");
            String today=date[0]+" "+date[1]+" "+date[2];
            if(transaction.transactionDate.contains(today)){
                st+=transaction.billAmount;
            }
        }
        salestoday.setText("Sales Today : "+String.format("%.2f",st));
        totalsales.setText("Total Sales : "+String.format("%.2f",ts));
    }
    private void verifyPassword() {
        final BottomSheetDialog dialog = new BottomSheetDialog(context,R.style.DialogStyle);
        dialog.setContentView(R.layout.password);
        dialog.setTitle(R.string.sq);
        dialog.cancel();
        dialog.show();
        TextView one=dialog.findViewById(R.id.one);
        TextView two=dialog.findViewById(R.id.two);
        TextView three=dialog.findViewById(R.id.three);
        TextView four=dialog.findViewById(R.id.four);
        TextView verify=dialog.findViewById(R.id.verify_pass);
        one.requestFocus();
        dialog.setCancelable(true);
        assert one != null;
        one.setSelectAllOnFocus(true);
        assert two != null;
        two.setSelectAllOnFocus(true);
        assert three != null;
        three.setSelectAllOnFocus(true);
        assert four != null;
        four.setSelectAllOnFocus(true);
        one.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                two.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        two.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                three.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        three.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                four.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        four.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(four.getWindowToken(), 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password=one.getText().toString()+two.getText().toString()+three.getText().toString()+four.getText().toString();
                FirebaseFirestore.getInstance().collection("Password").document("pass")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                if(password.equalsIgnoreCase(task.getResult().getString("pass"))){
                                    Toast.makeText(context,"Verified Successfully",Toast.LENGTH_LONG).show();
                                    dialog.cancel();
                                    Intent i=new Intent(context,Profit.class);
                                    startActivity(i);

                                }else {
                                    Toast.makeText(context,"Invalid Password",Toast.LENGTH_LONG).show();
                                    dialog.cancel();

                                }
                            }
                        });
            }
        });
    }

}