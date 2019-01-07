package com.example.segyu.capstone;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final Button mainbutton1 = findViewById(R.id.mainButton1);//이게 첫화면 누르면 채팅창나오는 버튼
        mainbutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//여기서 챗봇 액티비티로 이동
                Intent intent = new Intent(getBaseContext(), ChatBot.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        final Button mainbutton2 =  findViewById(R.id.mainButton2);
        mainbutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//여기서 챗봇 액티비티로 이동
                Intent intent = new Intent(getBaseContext(), GoogleMap.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        final Button mainbutton3 = (Button) findViewById(R.id.mainButton3);//이게 첫화면 누르면 채팅창나오는 버튼
        mainbutton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//여기서 챗봇 액티비티로 이동
                Intent intent = new Intent(getBaseContext(), Layout1.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        final Button mainbutton4 = (Button) findViewById(R.id.mainButton4);//이게 첫화면 누르면 채팅창나오는 버튼
        mainbutton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//여기서 챗봇 액티비티로 이동
                Intent intent = new Intent(getBaseContext(), Layout1.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager manager = getFragmentManager();//프래그먼트를이용한 화면전환


        if (id == R.id.nav_camera) {
            Intent intent = new Intent(getBaseContext(), Layout1.class);//액티비티를 이용한 화면전환
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(getBaseContext(), Layout2.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);


        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(getBaseContext(), Layout3.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(getBaseContext(), Layout4.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);


        } else if (id == R.id.nav_share) {
//        manager.beginTransaction().replace(R.id.container, new Fragment1()).addToBackStack(null).commit();
        } else if (id == R.id.nav_send) {
//            manager.beginTransaction().replace(R.id.container, new Fragment1()).addToBackStack(null).commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

