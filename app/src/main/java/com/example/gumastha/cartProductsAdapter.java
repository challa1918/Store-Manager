package com.example.gumastha;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.bumptech.glide.Glide;
import com.example.gumastha.Controller.FirebaseUtils;
import com.example.gumastha.Model.BillProduct;
import com.example.gumastha.Model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class cartProductsAdapter extends RecyclerView.Adapter<cartProductsAdapter.ViewHolder> {
    Context context;
    View listItem;
    FirebaseFirestore db;
    FirebaseUser user;
    ArrayList<BillProduct> list;

    public cartProductsAdapter(ArrayList<BillProduct> list, Context context) {
        this.context = context;
        this.list = list;
        db=FirebaseFirestore.getInstance();
        user=new FirebaseUtils().getCurrentUser();
    }

    public cartProductsAdapter() {

    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        listItem = layoutInflater.inflate(R.layout.cartitem, parent, false);
        cartProductsAdapter.ViewHolder viewHolder = new cartProductsAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull @NotNull cartProductsAdapter.ViewHolder holder, int position) {
        BillProduct product = list.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetBottom(product);
            }
        });
        holder.cartpname.setText(product.product.getPRODUCT_NAME());
        Glide.with(context.getApplicationContext()).load(product.product.getURI()).into(holder.pimage);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pimage;
        TextView cartpname;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            pimage = itemView.findViewById(R.id.cproduct);
            cartpname = itemView.findViewById(R.id.cme);

        }

    }


    public void sheetBottom(BillProduct product) {
        System.out.println("Function: " + product.product.getPRODUCT_NAME());
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        @SuppressLint("InflateParams") View view = bottomSheetDialog.getLayoutInflater().inflate(R.layout.add_product_to_cart_botttom_sheet, null);
        bottomSheetDialog.setContentView(R.layout.cart_customizing_bottomsheet_layout);
        bottomSheetDialog.setTitle(R.string.sq);
        TextView add = bottomSheetDialog.findViewById(R.id.addtocart);
        TextView pname = bottomSheetDialog.findViewById(R.id.bspname);
        pname.setText(product.product.getPRODUCT_NAME());
        TextView plus = bottomSheetDialog.findViewById(R.id.plus);
        TextView minus = bottomSheetDialog.findViewById(R.id.minus);
        TextView remove=bottomSheetDialog.findViewById(R.id.remove);
        TextView number = bottomSheetDialog.findViewById(R.id.item_number);
        EditText grams = bottomSheetDialog.findViewById(R.id.inputquantity);
        ImageView productimage = bottomSheetDialog.findViewById(R.id.bsproduct);

        Glide.with(context).load(product.product.getURI()).into(productimage);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                number.setText(String.valueOf(Integer.parseInt(number.getText().toString()) + 1));
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number.setText(String.valueOf(Integer.parseInt(number.getText().toString()) - 1));
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
                MaterialAlertDialogBuilder materialAlertDialogBuilder= new MaterialAlertDialogBuilder(context);
                materialAlertDialogBuilder.setTitle(R.string.deltitle);
                materialAlertDialogBuilder.setMessage("Are you sure  do you want to remove "+product.product.getPRODUCT_NAME()+" ?");
                materialAlertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection(user.getDisplayName()+" Cart").document(product.product.getBARCODE()).delete().
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            Toast.makeText(context.getApplicationContext(), R.string.removed, Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });
                materialAlertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                materialAlertDialogBuilder.create().show();

            }
        });
        if (!product.product.getUNIT().equals("KG")) {
            plus.setVisibility(View.VISIBLE);
            minus.setVisibility(View.VISIBLE);
            number.setVisibility(View.VISIBLE);
            grams.setVisibility(View.GONE);
            number.setText(String.valueOf(product.quantity));

        } else {
            plus.setVisibility(View.GONE);
            minus.setVisibility(View.GONE);
            number.setVisibility(View.GONE);
            grams.setVisibility(View.VISIBLE);
            grams.setText(String.valueOf(product.quantity));

        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!product.product.getUNIT().equals("KG")) {
                    if (!number.getText().toString().equals("0"))
                        if (product.product.getSTOCK_AVAILABLE() >= Integer.parseInt(number.getText().toString())) {
                            BillProduct billProduct = new BillProduct(product.product, Integer.parseInt( number.getText().toString()));
                            addToCart(billProduct);
                        } else {
                            Toast.makeText(context, R.string.inefficientstock, Toast.LENGTH_LONG).show();
                        }
                    else
                        Toast.makeText(context, R.string.errorunitnumber, Toast.LENGTH_LONG).show();


                } else {
                    if (!grams.getText().toString().equals(""))
                        if (product.product.getSTOCK_AVAILABLE() >= Integer.parseInt(grams.getText().toString())) {
                            BillProduct billProduct = new BillProduct(product.product,Integer.parseInt( grams.getText().toString()));
                            addToCart(billProduct);
                        } else {
                            Toast.makeText(context, R.string.inefficientstock, Toast.LENGTH_LONG).show();
                        }
                    else
                        Toast.makeText(context, R.string.errorunitgram, Toast.LENGTH_LONG).show();

                }
                bottomSheetDialog.cancel();
            }
        });

        bottomSheetDialog.show();
    }

    private void addToCart(BillProduct billProduct) {
        db.collection(user.getDisplayName()+" Cart").document(billProduct.product.getBARCODE()).set(billProduct)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        Toast.makeText(context,"Cart Updated Successfully",Toast.LENGTH_LONG).show();
                    }
                });
    }
}
