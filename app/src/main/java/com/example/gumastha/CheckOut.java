package com.example.gumastha;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.example.gumastha.Controller.FirebaseUtils;
import com.example.gumastha.Model.BillProduct;
import com.example.gumastha.Model.Creditor;
import com.example.gumastha.Model.Product;
import com.example.gumastha.Model.Profit_data;
import com.example.gumastha.Model.Transaction;
import com.example.gumastha.View.Home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class CheckOut extends AppCompatActivity implements CheckOutInterface {
    RecyclerView recyclerView;
    TextView bill;
    ArrayList<Double> singleBill= new ArrayList<>();
    ImageView back;
    RadioGroup paymentmode;
    int fbill;
    FirebaseUser user;
    TextView send_bill;
    FirebaseFirestore db;
    Button checkout,addtocredit;
    private ArrayList<BillProduct> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            int permission=1;
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},permission);
        }
        db=FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_check_out);
        user=new FirebaseUtils().getCurrentUser();
        back=findViewById(R.id.cback);
        send_bill=findViewById(R.id.send_bill);
        paymentmode=findViewById(R.id.paymentmode);
        checkout=findViewById(R.id.checkoutfinal);
        addtocredit=findViewById(R.id.addtocredit);
        paymentmode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId!=R.id.credit){
                    addtocredit.setEnabled(false);
                }else{
                    addtocredit.setEnabled(true);
                }

            }
        });
        send_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBillToCustomer();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView=findViewById(R.id.billingcart);
        bill=findViewById(R.id.totalamount);
        addtocredit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                setProfit(list);
                checkOutCustomer("addtocreditors");
            }
        });
        checkout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                setProfit(list);
                checkOutCustomer("checkout");
            }
        });

        new FirebaseUtils(this).getCartProducts("CheckOut");
    }

    private void sendBillToCustomer() {
        Dialog dialog= new Dialog(this);
        View view=getLayoutInflater().inflate(R.layout.customer_popup,null);
        EditText popname=view.findViewById(R.id.popname);
        EditText phn=view.findViewById(R.id.popphnno);
        Button add=view.findViewById(R.id.addcreditor);

        String transactionMode="";
        if(paymentmode.getCheckedRadioButtonId()==R.id.online||paymentmode.getCheckedRadioButtonId()==R.id.offline||paymentmode.getCheckedRadioButtonId()==R.id.credit){
            switch(paymentmode.getCheckedRadioButtonId()){
                case R.id.offline:transactionMode="Offline";break;
                case R.id.online:transactionMode="Online";break;
                case R.id.credit:transactionMode="Credit";break;
            }

        }else{
            Toast.makeText(getApplicationContext(),"Check the payment mode",Toast.LENGTH_LONG).show();
            return;
        }

        dialog.setContentView(view);
        dialog.setTitle("Add Creditor");
        dialog.show();
        String finalTransactionMode = transactionMode;
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
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat dateOnly = new SimpleDateFormat("MM/dd/yyyy");

                    String bill="********** *Store Keeper* **********\nCustomer: *"+popname.getText().toString()+"*  Date: *"+dateOnly.format(cal.getTime())+"*\n*Product    Qty    Price*\n";
                    for (int i = 0; i < list.size(); i++) {
                        BillProduct p=list.get(i);
                        bill+=p.product.getPRODUCT_NAME()+"    "+p.quantity+"    ("+p.product.getSELLING_PRICE()+")"+singleBill.get(i)+"\n";
                    }
                    bill+="*Total bill: "+ getTotalBill(list) +"*\n";
                   bill+="*Payment Mode: "+ finalTransactionMode+"*\n*Thank you visit again!! :)*";
                    System.out.println(bill);
                    /*try{
                        SmsManager smsManager= SmsManager.getDefault();
                        System.out.println("Bill List: "+list.toString());
                        PendingIntent sentPI;
                        String SENT = "SMS_SENT";
                        sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0,new Intent(SENT), 0);
                        smsManager.sendTextMessage("+91"+phn.getText().toString(),null, bill,sentPI,null);
                        Toast.makeText(getApplicationContext(),"Sending Bill "+phn.getText().toString(),Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Failed to send",Toast.LENGTH_LONG).show();
                        System.out.println(e.getMessage());
                    }*/
                    String url="";
                    Intent i= new Intent(Intent.ACTION_VIEW);
                    try {
                         url="https://api.whatsapp.com/send?phone=+91"+phn.getText().toString()+"&text="+ URLEncoder.encode(bill,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setProfit(ArrayList<BillProduct> list) {
        double profit=0;
        LocalDate myObj = LocalDate.now(); // Create a date object
        String transactionDay=myObj.toString();
        for (BillProduct p:
                list) {
            Product product=p.product;
            if(product.getUNIT().equals("KG")){
                double pergram= Double.parseDouble(product.getSELLING_PRICE())/1000;
                double cpergram= Double.parseDouble(product.getCOST_PRICE())/1000;
                System.out.println(pergram);
                singleBill.add(p.quantity*pergram);
                profit+=( p.quantity*pergram-p.quantity*cpergram);

            }else{

                profit+=(p.quantity*Float.parseFloat(product.getSELLING_PRICE())- p.quantity*Float.parseFloat(product.getCOST_PRICE()));
                singleBill.add((double) (p.quantity*Integer.parseInt(product.getSELLING_PRICE())));
            }

        }
        Profit_data profit_data=new Profit_data();
        profit_data.date=transactionDay;
        profit_data.Profit=profit;
       // Toast.makeText(getApplicationContext(),"Profit : "+profit,Toast.LENGTH_LONG).show();
        double finalProfit = profit;
        db.collection("Profit").document(transactionDay).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(task.getResult().getData()==null)
                            db.collection("Profit").document(transactionDay).set(profit_data);
                        db.collection("Profit").document(transactionDay).update("Profit", FieldValue.increment(finalProfit));
                    }
                });

        db.collection("Total_Profit").document("Profit").update("Profit",FieldValue.increment(finalProfit));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkOutCustomer(String mode) {
        String transactionMode = null;
        if(paymentmode.getCheckedRadioButtonId()==R.id.online||paymentmode.getCheckedRadioButtonId()==R.id.offline||paymentmode.getCheckedRadioButtonId()==R.id.credit){
             switch(paymentmode.getCheckedRadioButtonId()){
                 case R.id.offline:transactionMode="Offline";break;
                 case R.id.online:transactionMode="Online";break;
                 case R.id.credit:transactionMode="Credit";break;
             }
            checkout.setEnabled(false);
           addtocredit.setEnabled(false);
        }else{
            Toast.makeText(getApplicationContext(),"Check the payment mode",Toast.LENGTH_LONG).show();
            return;
        }

        String transactionDate= String.valueOf(new Date());
        LocalDate myObj = LocalDate.now(); // Create a date object
        String transactionDay=myObj.toString();
        transactionDate=String.valueOf(new Date());
        String finalTransactionDate = transactionDate;

        String finalTransactionMode = transactionMode;
        db.collection("TID").document("TID").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        Integer TID= task.getResult().get("TID",Integer.class);
                        Transaction t= new Transaction(list, finalTransactionMode, finalTransactionDate,fbill,TID,user.getDisplayName());
                        com.google.firebase.Timestamp ts=com.google.firebase.Timestamp.now();
                        t.seconds=ts.getSeconds();
                        if(mode.equalsIgnoreCase("addtocreditors")){
                            Intent i= new Intent(CheckOut.this,Add_To_Creditor.class);
                            i.putExtra("transaction",t);
                            startActivity(i);
                            finish();
                        }else {
                            db.collection("Transactions")
                                    .document(String.valueOf(TID))
                                    .set(t)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), "Transaction updated", Toast.LENGTH_LONG).show();
                                            db.collection("TID").document("TID").update("TID", FieldValue.increment(1));
                                            updateInventory();
                                            Intent i = new Intent(CheckOut.this, Home.class);
                                            startActivity(i);
                                            finish();

                                        }
                                    });
                        }


                    }
                });


        

    }

    private void updateInventory() {
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
    public void setRecyclerView(ArrayList<BillProduct> list) {
        this.list=list;
        if(list.size()==0){
            Toast.makeText(getApplicationContext(),"No products in the cart",Toast.LENGTH_LONG).show();
            checkout.setEnabled(false);
            addtocredit.setEnabled(false);
        }else{

            Collections.sort(list, new Comparator<BillProduct>() {
                @Override
                public int compare(BillProduct o1, BillProduct o2) {
                    return o1.product.getPRODUCT_NAME().compareTo(o2.product.getPRODUCT_NAME());
                }
            });
            fbill=getTotalBill(list);
            bill.setText("Total Amount: "+fbill);
            checkout.setEnabled(true);
            addtocredit.setEnabled(true);

        }

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new CheckOutAdapter(this,list,singleBill));


    }
    private Integer getTotalBill(ArrayList<BillProduct> list) {
        double bill=0;
        for (BillProduct p:
                list) {
            Product product=p.product;

            if(product.getUNIT().equals("KG")){
                double pergram= Double.parseDouble(product.getSELLING_PRICE())/1000;
                System.out.println(pergram);
                singleBill.add (p.quantity*pergram);
                bill+= p.quantity*pergram;
            }else{
                bill+=(p.quantity*Integer.parseInt(product.getSELLING_PRICE()));
                singleBill.add((double) (p.quantity*Integer.parseInt(product.getSELLING_PRICE())));
            }

        }
        bill=Math.floor(bill);
        return (int)bill;
    }
}