package com.example.gumastha;

import android.annotation.SuppressLint;
import android.app.Dialog;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gumastha.Model.BillProduct;
import com.example.gumastha.Model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.w3c.dom.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MemberAdapter extends  RecyclerView.Adapter<MemberAdapter.ViewHolder>{
    ArrayList<Product>list;
    public Context context;
   MemberAdapter(ArrayList<Product> list, Context context){
        this.context=context;
        this.list=list;
   }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View listItem = layoutInflater.inflate(R.layout.product_list_item, parent, false);
       MemberAdapter.ViewHolder viewHolder = new MemberAdapter.ViewHolder(listItem);
        return viewHolder;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull @NotNull MemberAdapter.ViewHolder holder, int position) {
            Product product=list.get(position);
           // System.out.println(product);
            holder.pname.setText(product.getPRODUCT_NAME());
            holder.sp.setText(product.getSELLING_PRICE());
            holder.stock.setText("Available: "+String.valueOf(product.getSTOCK_AVAILABLE()));
            Glide.with(context).load(product.getURI()).into(holder.pimage);
            holder.pimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog d= new Dialog(context);
                    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.product_image, null);
                    Glide.with(context).load(product.getURI()).into((ImageView) view.findViewById(R.id.image));
                    d.setContentView(view);
                    d.setCancelable(true);
                    d.show();

                }
            });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cp="****";
                BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(context);
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.productdetailsmod, null);
                bottomSheetDialog.setContentView(view);
                Glide.with(context).load(product.getURI()).into((ImageView) view.findViewById(R.id.dispimage));
                TextView sep= view.findViewById(R.id.dissp);
                ImageView copycode=view.findViewById(R.id.copycode);
                TextView dcp= view.findViewById(R.id.discp);
                TextView code=view.findViewById(R.id.discode);
                TextView ded= view.findViewById(R.id.disexp);
                LinearLayout disatc=view.findViewById(R.id.disatc);
                LinearLayout disdelete=view.findViewById(R.id.disdelete);
                TextView dpname= view.findViewById(R.id.dispname);
                TextView category=view.findViewById(R.id.discat);
                TextView stock=view.findViewById(R.id.disstock);
                TextView atctext=view.findViewById(R.id.atctext);
                ImageView atcimage=view.findViewById(R.id.atcimg);
                ImageView editsp=view.findViewById(R.id.editsp);
                ImageView editcp=view.findViewById(R.id.editcp);
                ImageView editstock=view.findViewById(R.id.editstock);
                TextView deltext=view.findViewById(R.id.deletetext);
                ImageView delimage=view.findViewById(R.id.deleteimg);
                code.setText("Code : "+product.getBARCODE());
                sep.setText("Selling Price : "+product.getSELLING_PRICE());
                dcp.setText("Cost Price :  "+cp);
                ded.setText("Expiry Date : "+product.getEXPIRY_DATE());
                dpname.setText(product.getPRODUCT_NAME());
                category.setText("Category : "+product.getCATEGORY());
                stock.setText("Stock Available : "+String.valueOf(product.getSTOCK_AVAILABLE()));
                bottomSheetDialog.show();
                dcp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(dcp.getText().toString().contains(cp))
                            verifyPassword(product,dcp);
                    }
                });
                disatc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToCart(product);
                        bottomSheetDialog.cancel();
                    }
                });
                atctext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToCart(product);
                        bottomSheetDialog.cancel();
                    }
                });
                atcimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToCart(product);
                        bottomSheetDialog.cancel();
                    }
                });
                copycode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Code", product.getBARCODE());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context.getApplicationContext(), "Code Copied !! ",Toast.LENGTH_LONG).show();
                    }
                });
                disdelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteProduct(product.getBARCODE(),product.getURI());
                        bottomSheetDialog.cancel();
                    }
                });
                delimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteProduct(product.getBARCODE(),product.getURI());
                        bottomSheetDialog.cancel();
                    }
                });
                deltext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteProduct(product.getBARCODE(),product.getURI());
                        bottomSheetDialog.cancel();
                    }
                });
                editcp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(dcp.getText().toString().contains(cp))
                            verifyPassword(product,dcp);
                        else {
                            updateField(product.getBARCODE(), product.getCOST_PRICE(), product, "Cost Price");
                            bottomSheetDialog.cancel();
                        }
                    }
                });
                editsp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateField(product.getBARCODE(), product.getSELLING_PRICE(),product,"Selling Price");
                        bottomSheetDialog.cancel();
                    }
                });
                editstock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateField(product.getBARCODE(), String.valueOf(product.getSTOCK_AVAILABLE()),product,"Stock");
                        bottomSheetDialog.cancel();
                    }
                });
            }
        });
        LocalDate date= LocalDate.parse(product.getEXPIRY_DATE());
        LocalDate current = LocalDate.now();
        final long days = ChronoUnit.DAYS.between(current,date);

        String info="";
        if(product.getSTOCK_AVAILABLE()<10){
            holder.status.setVisibility(View.VISIBLE);
            if(product.getSTOCK_AVAILABLE()>0)
                info="Low stock";
            else
                info="Out Of Stock";
            holder.status.setText(info);
        }
        if(days<10){
            holder.status.setVisibility(View.VISIBLE);
            if(!info.equals(""))
                info+=" |";
            if(days>0)
               info+= " Expiring in "+String.valueOf(days)+" days";
            if(days<=0)
                info+=" Expired";
            holder.status.setText(info);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addToCart(Product product) {
        LocalDate date= LocalDate.parse(product.getEXPIRY_DATE());
        LocalDate current = LocalDate.now();
        final long days = ChronoUnit.DAYS.between(current,date);
       if(product.getSTOCK_AVAILABLE()<=0){
           Toast.makeText(context.getApplicationContext(),"Stock isn't available",Toast.LENGTH_LONG).show();
           return;
       }
        if(days<0){
            Toast.makeText(context.getApplicationContext(),"Stock is expired..",Toast.LENGTH_LONG).show();
            return;
        }
        Intent i= new Intent(context,CartBuilding.class);
        i.putExtra("Code",product.getBARCODE());
        context.startActivity(i);

    }
    private void deleteProduct(String barcode,String url){

       MaterialAlertDialogBuilder materialAlertDialogBuilder= new MaterialAlertDialogBuilder(context);
       materialAlertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               FirebaseFirestore db=FirebaseFirestore.getInstance();
               db.collection("Inventory").document(barcode).delete()
                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull @NotNull Task<Void> task) {
                               StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                               storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                       Toast.makeText(context.getApplicationContext(), "Product deleted Successfully ",Toast.LENGTH_LONG).show();

                                   }
                               });
                           }
                       });
               Toast.makeText(context.getApplicationContext(), "Product deleted Successfully ",Toast.LENGTH_LONG).show();

           }
       });
       materialAlertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {

           }
       });
       materialAlertDialogBuilder.setTitle("Deleting Product");
       materialAlertDialogBuilder.setMessage(R.string.deleteproductstring);
       materialAlertDialogBuilder.setCancelable(false);
       materialAlertDialogBuilder.create().show();

    }
    private  void updateField(String barcode,String oldvalue,Product product,String field){

       MaterialAlertDialogBuilder materialAlertDialogBuilder=new MaterialAlertDialogBuilder(context);
       materialAlertDialogBuilder.setTitle("Updating "+field);
       View view=LayoutInflater.from(context).inflate(R.layout.update_popup,null);
       materialAlertDialogBuilder.setView(view);
       EditText old= view.findViewById(R.id.popold);
       EditText updated=view.findViewById(R.id.popnew);
       old.setText(oldvalue);
       old.setEnabled(false);
        Button updatefield=view.findViewById(R.id.popupdate);
        materialAlertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog= materialAlertDialogBuilder.create();
        alertDialog.show();
        updatefield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newvalue=updated.getText().toString();
                System.out.println("New Value : "+newvalue);

                switch (field){
                    case "Selling Price":product.setSELLING_PRICE(newvalue);break;
                    case  "Cost Price":product.setCOST_PRICE(newvalue);break;
                    case "Stock":product.setSTOCK_AVAILABLE(Integer.parseInt(newvalue));
                }
                FirebaseFirestore db= FirebaseFirestore.getInstance();
                System.out.println("Updated : "+product);
                db.collection("Inventory").document(barcode).set(product)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {

                            }
                        });
                Toast.makeText(context.getApplicationContext(), "Product "+field+" Updated",Toast.LENGTH_LONG).show();
                alertDialog.dismiss();

            }
        });


    }

    private void verifyPassword(Product product,TextView dcp) {
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
                                    dcp.setText("Cost Price: "+product.getCOST_PRICE());
                                    dialog.cancel();
                                }else {
                                    Toast.makeText(context,"Invalid Password",Toast.LENGTH_LONG).show();

                                }
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       ImageView pimage;
       TextView pname,stock,sp,status;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            pimage=itemView.findViewById(R.id.pimage);
            pname=itemView.findViewById(R.id.productName);
            stock=itemView.findViewById(R.id.stock);
            sp=itemView.findViewById(R.id.sep);
            status=itemView.findViewById(R.id.status);

        }
    }
}
