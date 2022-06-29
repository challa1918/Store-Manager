package com.example.gumastha;
        import android.Manifest;
        import android.annotation.SuppressLint;
        import android.app.Activity;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.graphics.Color;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.view.View;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.RadioButton;
        import android.widget.RadioGroup;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.cardview.widget.CardView;
        import androidx.constraintlayout.widget.ConstraintLayout;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.airbnb.lottie.LottieAnimationView;
        import com.bumptech.glide.Glide;
        import com.example.gumastha.Controller.FirebaseUtils;
        import com.example.gumastha.Model.BillProduct;
        import com.example.gumastha.Model.Product;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.android.material.bottomsheet.BottomSheetDialog;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.zxing.integration.android.IntentIntegrator;
        import com.google.zxing.integration.android.IntentResult;
        import com.mikhaellopez.circularimageview.CircularImageView;

        import org.jetbrains.annotations.NotNull;

        import java.io.File;
        import java.util.ArrayList;
        import java.util.zip.Inflater;

public class CartBuilding extends Activity implements CartBuildingInterface  {
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 1;
    private static final int CAMERA_REQUEST =1 ;
    LottieAnimationView getProduct,noProduct;
    RadioGroup radioGroup;
    RadioButton enter,scan;
    CardView edit;
    RecyclerView cartProducts;
    FirebaseUser user;
    FirebaseFirestore db;
    TextView nproducts;
    ConstraintLayout checkout;
    EditText cartcode;
    ArrayList<BillProduct> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_building);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }

        nproducts=findViewById(R.id.nproducts);
        checkout=findViewById(R.id.checkout);
        cartProducts=findViewById(R.id.cartproducts);
        user=new FirebaseUtils().getCurrentUser();
        noProduct=findViewById(R.id.noproducts);
        new FirebaseUtils(this).getCartProducts("CartBuilding");
        db=FirebaseFirestore.getInstance();
        enter=findViewById(R.id.enter);
        scan=findViewById(R.id.scan);
        cartcode=findViewById(R.id.cartcode);
        radioGroup=findViewById(R.id.radioGroup);
        edit=findViewById(R.id.edit);
        list=new ArrayList<>();
        if(getIntent()!=null){
            String code=(String)getIntent().getCharSequenceExtra("Code");
            if(code!=null){
                proceedWithCode(code);
            }
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.enter:
                      //  Toast.makeText(getApplicationContext(),enter.getText(),Toast.LENGTH_LONG).show();
                        edit.setVisibility(View.VISIBLE);
                        break;
                    case R.id.scan:
                     //   Toast.makeText(getApplicationContext(),scan.getText(),Toast.LENGTH_LONG).show();
                        edit.setVisibility(View.GONE);break;
                }
            }
        });
        getProduct=findViewById(R.id.animationView2);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.size()!=0){
                    Intent i = new Intent(CartBuilding.this,CheckOut.class);
                    startActivity(i);
                }else{
                    Toast.makeText(getApplicationContext(),R.string.no_products_cart,Toast.LENGTH_LONG).show();
                }

            }
        });
        getProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code="";
                 switch (radioGroup.getCheckedRadioButtonId()){
                     case R.id.enter:code=cartcode.getText().toString();
                       //  Toast.makeText(getApplicationContext(),"Code "+code,Toast.LENGTH_LONG).show();
                         proceedWithCode(code);
                         break;
                     case R.id.scan:
                         scanCode();
                         code=cartcode.getText().toString();
                         break;
                     default:Toast.makeText(getApplicationContext(),R.string.selectoption,Toast.LENGTH_LONG).show();
                 }}


        });


    }

    private void proceedWithCode(String code) {

        if(!code.equals("")){
            new FirebaseUtils().getProductUsingCode(code,this);
            cartcode.setText("");
        }
        else
            Toast.makeText(getApplicationContext(),R.string.validcode,Toast.LENGTH_LONG).show();

      //  sheetBottom(new Product());
    }

    private void scanCode() {
            IntentIntegrator intent= new IntentIntegrator(this);
            intent.setCaptureActivity(capture.class);
            intent.setOrientationLocked(false);
            intent.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            intent.setPrompt("Cover Entire Barcode while Scanning Code");
            intent.initiateScan();
        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

            IntentResult result= IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result!=null){
                if(result.getContents()!=null){
                    cartcode.setText(result.getContents());
                    proceedWithCode(cartcode.getText().toString());
                }else{
                    Toast.makeText(this,"No Result",Toast.LENGTH_LONG).show();
                }

            }else{
                super.onActivityResult(requestCode, resultCode, data);
            }

        }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void sheetBottom(Product product) {
        System.out.println("Function: "+product);
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
       @SuppressLint("InflateParams") View view =bottomSheetDialog.getLayoutInflater().inflate(R.layout.add_product_to_cart_botttom_sheet,null);
        bottomSheetDialog.setContentView(R.layout.add_product_to_cart_botttom_sheet);
        bottomSheetDialog.setTitle(R.string.sq);
        TextView add= bottomSheetDialog.findViewById(R.id.addtocart);
        TextView pname=bottomSheetDialog.findViewById(R.id.bspname);
        pname.setText(product.getPRODUCT_NAME());
        TextView plus=bottomSheetDialog.findViewById(R.id.plus);
        TextView inamount=bottomSheetDialog.findViewById(R.id.in_amount);
        TextView minus=bottomSheetDialog.findViewById(R.id.minus);
        EditText in_amount=bottomSheetDialog.findViewById(R.id.quant_in_amount);
        TextView number=bottomSheetDialog.findViewById(R.id.item_number);
        EditText grams=bottomSheetDialog.findViewById(R.id.inputquantity);

        ImageView productimage=bottomSheetDialog.findViewById(R.id.bsproduct);
        Glide.with(CartBuilding.this).load(product.getURI()).into(productimage);

        in_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!in_amount.getText().toString().equals(""))
                setGrams(Integer.parseInt(in_amount.getText().toString()));
            }

            private void setGrams(int parseInt) {

                Double gram_per_rupee=1000f/Double.parseDouble(product.getSELLING_PRICE());
                int gms= (int) (parseInt*gram_per_rupee);
                grams.setText(String.valueOf(gms));
            }
        });


        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                number.setText(String.valueOf(Integer.parseInt(number.getText().toString())+1));
                }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number.setText(String.valueOf(Integer.parseInt(number.getText().toString())-1));
            }
        });
        if(!product.getUNIT().equals("KG")){
            plus.setVisibility(View.VISIBLE);
            minus.setVisibility(View.VISIBLE);
            number.setVisibility(View.VISIBLE);
            grams.setVisibility(View.GONE);
            inamount.setVisibility(View.GONE);
            in_amount.setVisibility(View.GONE);

        }else{
            plus.setVisibility(View.GONE);
            minus.setVisibility(View.GONE);
            number.setVisibility(View.GONE);
            grams.setVisibility(View.VISIBLE);
            inamount.setVisibility(View.VISIBLE);
            in_amount.setVisibility(View.VISIBLE);

        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!product.getUNIT().equals("KG")){
                    if(!number.getText().toString().equals("0"))
                    if(product.getSTOCK_AVAILABLE()>=Integer.parseInt(number.getText().toString())){
                        BillProduct billProduct=new BillProduct(product,Integer.parseInt(number.getText().toString()));
                        addToCart(billProduct);
                    }else{
                        Toast.makeText(getApplicationContext(),R.string.inefficientstock,Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(getApplicationContext(),R.string.errorunitnumber,Toast.LENGTH_LONG).show();


                }else{
                    if(!grams.getText().toString().equals(""))
                    if(product.getSTOCK_AVAILABLE()>=Integer.parseInt(grams.getText().toString())){
                        BillProduct billProduct=new BillProduct(product,Integer.parseInt(grams.getText().toString()));
                        addToCart(billProduct);
                    }else{
                        Toast.makeText(getApplicationContext(),R.string.inefficientstock,Toast.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(),R.string.errorunitgram,Toast.LENGTH_LONG).show();

                }
                bottomSheetDialog.cancel();
            }
        });

        bottomSheetDialog.show();
    }

    @Override
    public void setRecyclerView(ArrayList<BillProduct> list) {
        this.list=list;
        nproducts.setText("Total products in cart : "+list.size());
    if(list.size()==0)noProduct.setVisibility(View.VISIBLE);
    else {
        noProduct.setVisibility(View.INVISIBLE);}

        cartProducts.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        cartProducts.setAdapter(new cartProductsAdapter(list,this));
    }

    @Override
    public void noProductAvailableWithSuchCode(String code) {
        Toast.makeText(getApplicationContext(),"No Product available with code "+code,Toast.LENGTH_LONG).show();
    }

    private void addToCart(BillProduct billProduct) {
        db.collection(user.getDisplayName()+" Cart").document(billProduct.product.getBARCODE()).set(billProduct)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                Toast.makeText(getApplicationContext(),"Addded Successfully to cart",Toast.LENGTH_LONG).show();
            }
        });
    }
}







