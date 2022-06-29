package com.example.gumastha.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.gumastha.Controller.FirebaseUtils;
import com.example.gumastha.GoodsFragment;
import com.example.gumastha.R;
import com.example.gumastha.billing_fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity implements  BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    Fragment itemFragment;
    Fragment billingFragment;
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active ;
    FrameLayout frame;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }
        itemFragment = new GoodsFragment(this);
        billingFragment = new billing_fragment(Home.this);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        fm.beginTransaction().add(R.id.frame, billingFragment, "2").hide(billingFragment).commit();
        fm.beginTransaction().add(R.id.frame, itemFragment, "1").commit();
        active=itemFragment;
        bottomNavigationView.setOnNavigationItemSelectedListener(Home.this);

        frame = findViewById(R.id.frame);
        Toast.makeText(getApplicationContext(), new FirebaseUtils().getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.goods:
                fm.beginTransaction().hide(active).show(itemFragment).commit();
                active = itemFragment;
                return true;

            case R.id.billing:
                fm.beginTransaction().hide(active).show(billingFragment).commit();
                active = billingFragment;
                return true;

        }

        return false;

    }
}