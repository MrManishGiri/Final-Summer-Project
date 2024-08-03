package com.lcc.happycanteen;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lcc.happycanteen.activity.CustomerActivity;
import com.lcc.happycanteen.activity.OrderActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        startActivity(new Intent(MainActivity.this, CustomerActivity.class));
                        return true;
                    case R.id.navigation_cart:
                        startActivity(new Intent(MainActivity.this, OrderActivity.class));
                        return true;
                    case R.id.navigation_favorite:
                        startActivity(new Intent(MainActivity.this, FavoriteActivity.class));
                        return true;
                    case R.id.navigation_notification:
                        startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                        return true;
                }
                return false;
            }
        });
    }
}