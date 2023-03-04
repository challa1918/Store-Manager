package com.example.gumastha;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.gumastha.Model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Add_Product extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final int RESULT_LOAD_IMAGE = 2;
    Spinner spin,cat;
    ImageView back,calendar,scanner,img;
    TextView mfd,code;
    File image;
    Uri selectedImage;
    FirebaseFirestore db;
    ExtendedFloatingActionButton addtostore;
    String unit,categorycheck;
    android.content.res.Resources res;
    EditText product,stock,sp,cp,months;
    private static final int MY_CAMERA_PERMISSION_CODE = 1;
    private static final int CAMERA_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath="none";
    String[] units ;
    String[] categories ;
    String[] spincategories ;
// This line is added to demo
    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   getWindow().setEnterTransition(new Slide(Gravity.BOTTOM));
        setContentView(R.layout.activity_add_product);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));
        res=getResources();
        units = res.getStringArray(R.array.units_array);
        categories =res.getStringArray(R.array.cat_array);
        spincategories=res.getStringArray(R.array.cat_array);
        spincategories[0]="Select Category";
        scanner=findViewById(R.id.bcr);
        img=findViewById(R.id.circularImageView);
        mfd=findViewById(R.id.mfd);
        calendar=findViewById(R.id.calendar);
        spin = findViewById(R.id.units);
        cat=findViewById(R.id.categories);
        back=findViewById(R.id.back);
        code=findViewById(R.id.code);
        product=findViewById(R.id.productName);
        cp=findViewById(R.id.cp);
        sp=findViewById(R.id.sp);
        stock=findViewById(R.id.stock);
        addtostore=findViewById(R.id.addtostore);

        months=findViewById(R.id.months);

        scanner.setOnClickListener(view -> scanCode());
        addtostore.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    adddItemToInventory();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar myCalendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "yyyy-MM-dd"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        mfd.setText(sdf.format(myCalendar.getTime())+"  ");
                    }

                };

                new DatePickerDialog(Add_Product.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }

        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               // Toast.makeText(getApplicationContext(),categories[position],Toast.LENGTH_LONG).show();
                categorycheck=categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });
        spin.setOnItemSelectedListener(Add_Product.this);
        ArrayAdapter ad= new ArrayAdapter(this,android.R.layout.simple_spinner_item,units);
        ArrayAdapter ad1= new ArrayAdapter(this,android.R.layout.simple_spinner_item,spincategories);
        ad.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        ad1.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        cat.setAdapter(ad1);
        spin.setAdapter(ad);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       // Toast.makeText(getApplicationContext(),units[position],Toast.LENGTH_LONG).show();
        unit=units[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getApplicationContext(),"No Change",Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

          //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            currentPhotoPath="not_none";

           img.setImageURI(selectedImage);

        }else
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            image= new File(currentPhotoPath);
            img.setImageURI(Uri.fromFile(image));
            selectedImage=Uri.fromFile(image);

        } else{
            IntentResult result= IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result!=null){
                if(result.getContents()!=null){
                    code.setText(result.getContents());
                }else{
                    Toast.makeText(this,"No Result",Toast.LENGTH_LONG).show();
                }

            }else{
                super.onActivityResult(requestCode, resultCode, data);
            }

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


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    private void scanCode() {
        IntentIntegrator intent= new IntentIntegrator(this);
        intent.setCaptureActivity(capture.class);
        intent.setOrientationLocked(false);
        intent.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intent.setPrompt("Cover Entire Barcode while Scanning Code");
        intent.initiateScan();
    }
    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_layout);


        LinearLayout photo = bottomSheetDialog.findViewById(R.id.takephoto);
        LinearLayout gallery = bottomSheetDialog.findViewById(R.id.gallery);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    dispatchTakePictureIntent();
                    bottomSheetDialog.cancel();
                }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                    bottomSheetDialog.cancel();
            }
        });



        bottomSheetDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void adddItemToInventory() throws ParseException {

        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle(R.string.progressTitle);
        progressDialog.setCancelable(false);
        progressDialog.show();
        final String Product_Name=product.getText().toString();
        final String Cost_Price=cp.getText().toString();
        final String Selling_Price=sp.getText().toString();
        final String Barcode=code.getText().toString();
        final String Quantity=stock.getText().toString();
        String Manufacturing_date=mfd.getText().toString().trim();
        String BestBefore=months.getText().toString();



        final Product[] p = new Product[1];
        final StorageReference storageReference= FirebaseStorage.getInstance().getReference();

        if(Product_Name.equals("")){
            Toast.makeText(this,"Enter Product name",Toast.LENGTH_LONG).show();
            product.requestFocus();
            progressDialog.cancel();
        }else if(Cost_Price.equals("")){
            Toast.makeText(this,"Enter Cost Price",Toast.LENGTH_LONG).show();
            cp.requestFocus();
            progressDialog.cancel();
        }else if(Selling_Price.equals("")){
            Toast.makeText(this,"Enter Selling Price",Toast.LENGTH_LONG).show();
            sp.requestFocus();
            progressDialog.cancel();
        }else if(BestBefore.equals("")) {
            Toast.makeText(this, "Enter Best Before", Toast.LENGTH_LONG).show();
            months.requestFocus();
            progressDialog.cancel();
        }else if(Quantity.equals("")){
            Toast.makeText(this,"Enter Stock value",Toast.LENGTH_LONG).show();
            stock.requestFocus();
            progressDialog.cancel();
        }else if(Manufacturing_date.equals("Manufacturing Date : (YYYY/MM/DD)")){
            Toast.makeText(this,"Select Manufacturing Date",Toast.LENGTH_LONG).show();
            mfd.requestFocus();
            progressDialog.cancel();
        }else if(Barcode.equals("")){
            Toast.makeText(this,"Enter or Scan Barcode",Toast.LENGTH_LONG).show();
            code.requestFocus();
            progressDialog.cancel();
        }else if(unit.equals("Select Units")){
            Toast.makeText(this,"Select Valid Units",Toast.LENGTH_LONG).show();
            spin.requestFocus();
            progressDialog.cancel();
        }else if(categorycheck.equals("Select Category")){
            Toast.makeText(this,"Select Valid Category",Toast.LENGTH_LONG).show();
            cat.requestFocus();
            progressDialog.cancel();
        }else if(currentPhotoPath.equals("none")){
            Toast.makeText(this,"Select Product Image",Toast.LENGTH_LONG).show();
            progressDialog.cancel();
            img.requestFocus();
        }
        else {
            progressDialog.setMessage("Uploading Image");

            LocalDate date= LocalDate.parse(Manufacturing_date);
            LocalDate returnvalue = date.plusMonths(Integer.parseInt(BestBefore));
            String Expiry=returnvalue.toString();
            storageReference.child(Barcode).putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.setMessage("Uploading Product Data");
                    Toast.makeText(Add_Product.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    storageReference.child(Barcode).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            p[0] =new Product(Product_Name,Cost_Price,Selling_Price,Integer.parseInt(Quantity),Barcode, String.valueOf(uri),unit,Manufacturing_date,Expiry,categorycheck);
                            db= FirebaseFirestore.getInstance();

                            db.collection("Inventory")
                                    .document(Barcode)
                                    .set(p[0])
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Toast.makeText(Add_Product.this,Product_Name+" Added Succcessfully",Toast.LENGTH_LONG).show();
                                            progressDialog.cancel();
                                            startActivity(getIntent());
                                            finish();


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(Add_Product.this,"Failed to add new product",Toast.LENGTH_LONG).show();
                                            progressDialog.cancel();

                                        }
                                    });

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Error Due to: "+e.getMessage());
                    Toast.makeText(Add_Product.this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
            });



            Toast.makeText(this,"All details are Filled",Toast.LENGTH_LONG).show();



        }
    }



}


