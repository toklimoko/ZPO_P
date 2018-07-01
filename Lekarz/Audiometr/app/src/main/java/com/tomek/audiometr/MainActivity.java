package com.tomek.audiometr;

import android.app.Dialog;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.PopupWindow;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


// obsluga przycisków:

    public Button btnKal;
    public Button btnStart;


    private Dialog dialog;

    public void kal(){
        btnKal = (Button) findViewById(R.id.btn_kal);
        btnKal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentKal = new Intent(MainActivity.this,Main2Activity.class);

                startActivity(intentKal);
            }
        });

    }

    public void badanie(){
        btnStart = (Button) findViewById(R.id.btn_badanie);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentBad = new Intent(MainActivity.this,Main3Activity.class);

                startActivity(intentBad);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        setVolumeControlStream(AudioManager.STREAM_MUSIC); //pozwala na kontrolę głośności

        kal();
        badanie();
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
    public boolean onNavigationItemSelected(MenuItem item) { //obsluga paska bocznego
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //gdy wybrano start - otworz aktywnosc 1
        if (id == R.id.nav_start) {

            //nic nie daję, bo nie ma potrzeby w tej aktywności

            //gdy wybrano kalibruj - otworz aktywnosc 2

        }else if (id == R.id.nav_bt) {

//            dialog = new ConnectionDialog(MainActivity.this, Main3Activity.mActivityCallback);
//            dialog.show();

        } else if (id == R.id.nav_kalibruj) {

            Intent intentKal = new Intent(MainActivity.this,Main2Activity.class);
            startActivity(intentKal);

            //gdy wybrano jak badac - otworz okno popup
        } else if (id == R.id.nav_info) {

            Intent intentInfo = new Intent(MainActivity.this,PopUp0.class);
            startActivity(intentInfo);

            //gdy wybrano zamknij - zamknij bieżącą aktywnosc
        } else if (id == R.id.nav_powrot) {
            finish();
            //System.exit(0); //drugi sposób zamknięcia aktywności
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
