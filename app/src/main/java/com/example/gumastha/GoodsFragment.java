package com.example.gumastha;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gumastha.Controller.FirebaseUtils;
import com.example.gumastha.Model.ItemFragmentInterface;
import com.example.gumastha.Model.Product;
import com.example.gumastha.Model.Transaction;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class GoodsFragment extends Fragment implements ItemFragmentInterface {
    ImageView profile,menu;
    FirebaseUser user;
    RecyclerView recyclerView;
    Context context;
    View view;
    Spinner filter;
    GroupViewAdapter groupViewAdapter;
    ArrayList<Product>list;
   SearchView searchView;
    ExtendedFloatingActionButton add;
    String categories[];

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    public GoodsFragment(){}
    public GoodsFragment(Context context) {
        this.context=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.goods_fragment, container, false);

        user=new FirebaseUtils().getCurrentUser();
        add=view.findViewById(R.id.addnew);
        filter=view.findViewById(R.id.filter);
        profile=view.findViewById(R.id.profile);
        searchView=view.findViewById(R.id.searchView);
        menu=view.findViewById(R.id.menu);
        categories=context.getResources().getStringArray(R.array.cat_array);
        Glide.with(view.getContext()).load(user.getPhotoUrl()).into(profile);
        recyclerView=view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               Toast.makeText(getContext(),categories[position],Toast.LENGTH_LONG).show();
               new FirebaseUtils(GoodsFragment.this,categories).getFilteredCategories(categories[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this.getContext(),R.layout.spinner_item,categories){
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position%2 == 1) {
                    // Set the item background color
                    tv.setBackgroundColor(Color.parseColor("#DAF7A6"));
                }
                else {
                    // Set the alternate item background color
                    tv.setBackgroundColor(Color.parseColor("#DAF7A6"));
                }
                return view;
            }
        };;
        filter.setPrompt("Select an item");

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        filter.setAdapter(spinnerArrayAdapter);

        list=new ArrayList<>();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(GoodsFragment.this.getContext(),Add_Product.class);
                startActivity(i);
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

        
        return view;
    }

    private void filter(String newText) {
        TreeMap<String,ArrayList<Product>>map=new TreeMap<>();
        TreeMap<Integer,ArrayList<Product>>inventory=new TreeMap<>();
        int i=0;
        for (Product product:
             list) {
            if(product.getPRODUCT_NAME().toLowerCase().contains(newText))
            {  if(!map.containsKey(product.getCATEGORY()))map.put(product.getCATEGORY(),new ArrayList<Product>());
                map.get(product.getCATEGORY()).add(product);
            }

        }
        if(map.size()==0){
            Toast.makeText(getContext(),"No Data Found",Toast.LENGTH_LONG).show();
        }else{
            for (Map.Entry<String,ArrayList<Product>> e:
                    map.entrySet()) {
                if(e.getValue().size()>0)
                    inventory.put(i++,e.getValue());
            }
            groupViewAdapter.filterList(inventory);
        }

    }


    @Override
    public void setRecyclerView(TreeMap<Integer, ArrayList<Product>> list) {
        if(list.size()==0){
            Toast.makeText(getContext(),R.string.networklow,Toast.LENGTH_LONG).show();
        }else{
            this.list.clear();
            groupViewAdapter=new GroupViewAdapter(list,view.getContext());
             recyclerView.setAdapter(groupViewAdapter);
            for (int i = 0; i < list.size(); i++) {
                this.list.addAll(list.get(i));
            }
        }


    }

    @Override
    public void noDataAvailableUnderCategory(String category) {
        Toast.makeText(context,"No products avaialable in "+category, Toast.LENGTH_LONG).show();

    }
    private void showBottomSheetDialog() {
        LinearLayout home,transactions,creditors,cart,billing,sharing,logout;
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(GoodsFragment.this.getContext());
        bottomSheetDialog.setContentView(R.layout.mainmenu);
        home=bottomSheetDialog.findViewById(R.id.home_layout);
        transactions=bottomSheetDialog.findViewById(R.id.transaction_layout);
        creditors=bottomSheetDialog.findViewById(R.id.creditor_layout);
        sharing=bottomSheetDialog.findViewById(R.id.Share_Application);
        cart=bottomSheetDialog.findViewById(R.id.cart_layout);
        billing=bottomSheetDialog.findViewById(R.id.billing_layout);
        logout=bottomSheetDialog.findViewById(R.id.logout_layout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(context,"Logged Out",Toast.LENGTH_LONG).show();
                Intent i = new Intent(context,MainActivity.class);
                startActivity(i);
                getActivity().finish();

            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
            }
        });
        transactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, Transactions.class);
                startActivity(i);
                bottomSheetDialog.cancel();
            }
        });
        creditors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,Creditors.class);
                startActivity(i);
                bottomSheetDialog.cancel();
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,CheckOut.class);
                startActivity(i);
                bottomSheetDialog.cancel();
            }
        });
        billing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,CartBuilding.class);
                startActivity(i);
                bottomSheetDialog.cancel();
            }
        });
        sharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Stock Manager\nDownload the application now!!\nhttps://drive.google.com/file/d/1b0ftSRbqdYJSwQ6lhzv5W1cJ3LTo9Z2m/view?usp=sharing");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });
        bottomSheetDialog.show();
    }
}