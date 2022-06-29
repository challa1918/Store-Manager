package com.example.gumastha.Controller;

import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gumastha.AddToCreditorsInterface;
import com.example.gumastha.BillingFragmentInterface;
import com.example.gumastha.CartBuildingInterface;
import com.example.gumastha.CheckOutInterface;
import com.example.gumastha.CreditAccountInterface;
import com.example.gumastha.CreditorInterface;
import com.example.gumastha.Creditors;
import com.example.gumastha.Model.BillProduct;
import com.example.gumastha.Model.Creditor;
import com.example.gumastha.Model.Creditor_Transaction;
import com.example.gumastha.Model.ItemFragmentInterface;
import com.example.gumastha.Model.Product;
import com.example.gumastha.Model.Profit_data;
import com.example.gumastha.Model.Transaction;
import com.example.gumastha.ProfitInterface;
import com.example.gumastha.TransactionActivityInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;


public class FirebaseUtils implements FirebaseOperations {
    FirebaseAuth auth;
    FirebaseFirestore db;
    TreeMap<Integer,ArrayList<Product>>ProductList=new TreeMap<>();
    TreeMap<String,ArrayList<Product>>Cat=new TreeMap<>();
    CheckOutInterface checkOutInterface;
    String collectionref="Inventory";
    String[] Categories;
    AddToCreditorsInterface addToCreditorsInterface;
    BillingFragmentInterface billingFragmentInterface;
    CreditorInterface creditorInterface;
    ItemFragmentInterface itemFragmentInterface;
    CreditAccountInterface creditAccountInterface;
    CartBuildingInterface cartBuildingInterface;
    ProfitInterface profitInterface;
    TransactionActivityInterface transactionActivityInterface;
    public FirebaseUtils(){};
    public FirebaseUtils(ProfitInterface profitInterface){
        this.profitInterface=profitInterface;
    };
    public FirebaseUtils(ItemFragmentInterface itemFragmentInterface,String []Categories){
        this.itemFragmentInterface=itemFragmentInterface;
        this.Categories=Categories;

    }
    public FirebaseUtils(CreditAccountInterface creditAccountInterface){
        this.creditAccountInterface=creditAccountInterface;
    }
    public FirebaseUtils(AddToCreditorsInterface addToCreditorsInterface){
        this.addToCreditorsInterface=addToCreditorsInterface;
    }
    public  FirebaseUtils(CreditorInterface creditorInterface){
        this.creditorInterface=creditorInterface;
    }
    public FirebaseUtils(TransactionActivityInterface transactionActivityInterface){
        this.transactionActivityInterface=transactionActivityInterface;
    }
    public FirebaseUtils(CartBuildingInterface cartBuildingInterface){
        this.cartBuildingInterface=cartBuildingInterface;

    }
    public FirebaseUtils(BillingFragmentInterface billingFragmentInterface){
        this.billingFragmentInterface=billingFragmentInterface;

    }
    public FirebaseUtils(CheckOutInterface checkOutInterface){
        this.checkOutInterface=checkOutInterface;

    }
    @Override
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
    int i=0;
    @Override
    public ArrayList<Product> getProducts() {
       ArrayList<Product>list=new ArrayList<>();
        db=FirebaseFirestore.getInstance();

    db.collection(collectionref).addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
               ProductList.clear();
               Cat.clear();
            for (String cat:
                    Categories) {
                Cat.put(cat,new ArrayList<Product>());
            }
                db.collection(collectionref)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                int i=0;
                                for (QueryDocumentSnapshot document:
                                        task.getResult()) {
                                    Product p=document.toObject(Product.class);
                                    System.out.println(p);
                                    Cat.get(p.getCATEGORY()).add(p);
                                }
                                System.out.println(Cat);
                                for (Map.Entry<String,ArrayList<Product>> e:
                                        Cat.entrySet()) {
                                    if(e.getValue().size()>0)
                                    ProductList.put(i++,e.getValue());
                                }
                                itemFragmentInterface.setRecyclerView(ProductList);

                            }


                        });






        }
    });


        return  list;
    }

    @Override
    public void getCartProducts(String activity) {
        db=FirebaseFirestore.getInstance();
     String user=getCurrentUser().getDisplayName();
        db.collection(user+" Cart").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                ArrayList<BillProduct>list=new ArrayList<>();
                db.collection(user+" Cart")
                         .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot document:
                                        task.getResult()) {
                                    list.add(document.toObject(BillProduct.class));
                                }
                                if(activity.equals("CartBuilding"))
                                    cartBuildingInterface.setRecyclerView(list);
                                else
                                    checkOutInterface.setRecyclerView(list);


                            }
                        });


            }
        });

    }

    @Override
    public void getProductUsingCode(String code,CartBuildingInterface cartBuildingInterface) {
        this.cartBuildingInterface=cartBuildingInterface;
        db=FirebaseFirestore.getInstance();
        db.collection(collectionref)
                   .whereEqualTo("barcode",code)
                   .get()
                   .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                           @Override
                           public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                               if(task.getResult().getDocuments().size()!=0)
                                      cartBuildingInterface.sheetBottom(task.getResult().getDocuments().get(0).toObject(Product.class));
                               else
                                   cartBuildingInterface.noProductAvailableWithSuchCode(code);
                           }
                   });

    }




    @Override
    public void getFilteredCategories(String category) {

        if(category.equalsIgnoreCase("All"))
        {
            getProducts();
            return;
        }
        ProductList.clear();
        ArrayList<Product>list= new ArrayList<>();
        db=FirebaseFirestore.getInstance();
        String finalCategory = category;
        db.collection(collectionref)
                .whereEqualTo("category",category)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        int i=0;
                        for (QueryDocumentSnapshot document:
                                task.getResult()) {
                            list.add(document.toObject(Product.class));

                        }
                        if(list.size()!=0){
                            ProductList.put(0,list);
                            itemFragmentInterface.setRecyclerView(ProductList);
                        }
                        else
                            itemFragmentInterface.noDataAvailableUnderCategory(finalCategory);


                    }


                });

    }

    @Override
    public void getTransactions(String category) {
        db=FirebaseFirestore.getInstance();
        ArrayList<Transaction>list= new ArrayList<>();
        db.collection("Transactions").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if(category.equalsIgnoreCase("Transactions_Activity")||category.equalsIgnoreCase("Sales_Billing")){
                    db.collection("Transactions")
                            .orderBy("TID", Query.Direction.DESCENDING)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot documentSnapshot :
                                            task.getResult()) {
                                        list.add(documentSnapshot.toObject(Transaction.class));
                                    }
                                    if(category.equalsIgnoreCase("Sales_Billing"))
                                        billingFragmentInterface.setSales(list);
                                    else
                                        transactionActivityInterface.setTransactionList(list);
                                }
                            });

                }else {
                    db.collection("Transactions")
                            .orderBy("TID", Query.Direction.DESCENDING)
                            .limit(3)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot documentSnapshot :
                                            task.getResult()) {
                                        list.add(documentSnapshot.toObject(Transaction.class));
                                    }
                                    billingFragmentInterface.setTransactionList(list);
                                }
                            });
                }


            }
        });


    }

    @Override
    public void addCreditor(Creditor contact,String activity) {
        db=FirebaseFirestore.getInstance();
        db.collection("Creditors").document(contact.name).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if(task.getResult().getData()==null){
                                db.collection("Creditors").document(contact.name).set(contact);
                                if(activity.equals("add_to_credit"))
                                    addToCreditorsInterface.addingStatus("yes");
                                else
                                creditorInterface.addingStatus("yes");
                            }else{
                                if(activity.equals("add_to_credit"))
                                    addToCreditorsInterface.addingStatus("no");
                                else
                                creditorInterface.addingStatus("no");
                            }
                    }
                });
    }

    @Override
    public void getCreditors(String activity) {
        db=FirebaseFirestore.getInstance();
        db.collection("Creditors").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                   ArrayList<Creditor>list= new ArrayList<>();
                    db.collection("Creditors").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot documentSnapshot:
                                         task.getResult()) {
                                        list.add(documentSnapshot.toObject(Creditor.class));
                                    }
                                    if(activity.equalsIgnoreCase("creditors")){
                                        creditorInterface.setRecyclerView(list);
                                    }else if(activity.equalsIgnoreCase("add_to_creditors")){
                                        addToCreditorsInterface.setRecyclerView(list);
                                    }else {
                                        creditorInterface.setRecyclerView(list);
                                    }
                                }
                            });
            }
        });
    }

    @Override
    public void getCreditorTransactions(String creditor) {
        db=FirebaseFirestore.getInstance();
        db.collection("Creditors_Transactions").document(creditor).collection(creditor).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        ArrayList<Creditor_Transaction>list=new ArrayList<>();
                        db.collection("Creditors_Transactions").document(creditor).
                                collection(creditor).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot documentSnapshot:
                                                task.getResult()) {
                                            list.add(documentSnapshot.toObject(Creditor_Transaction.class));
                                        }
                                        creditAccountInterface.setRecyclerview(list);
                                    }
                                });
                    }
                });
    }

    @Override
    public void getcreditamount(String creditor) {
                    db=FirebaseFirestore.getInstance();
                        db.collection("Creditors").document(creditor).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                               creditAccountInterface.creditorsAmount(String.valueOf(task.getResult().toObject(Creditor.class).amount));
                                    }
                                });

    }

    @Override
    public void getProfitDetails() {
        db=FirebaseFirestore.getInstance();
        db.collection("Profit").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                    db.collection("Profit").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            ArrayList<Profit_data>list= new ArrayList<>();
                            for (QueryDocumentSnapshot documentSnapshot:
                                 task.getResult()) {
                                list.add(documentSnapshot.toObject(Profit_data.class));
                            }
                            profitInterface.setRecyclerview(list);
                        }
                    }) ;
            }
        });
    }

    @Override
    public void deleteCreditorAccount(String name) {
        db=FirebaseFirestore.getInstance();
        db.collection("Creditors").document(name).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                            creditAccountInterface.deleteStatus(name+" account deleted successfully");
                    }
                });

    }

    public  String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        String s[]=strDate.split(" ");

        return s[1]+" "+s[0];
    }


}
