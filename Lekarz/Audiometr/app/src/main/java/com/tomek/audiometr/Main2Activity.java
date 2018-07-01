package com.tomek.audiometr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.MotionEvent;
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
import android.widget.ProgressBar;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private Button btnKal;

    private Context context;
    private CharSequence text;
    private Toast toast;
    private MotionEvent event;

    public void playSound() {

        btnKal = (Button) findViewById(R.id.btn_kal);
        btnKal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Play play = new Play(2000, 0.1, 4); //czestotliwosc = 2000 Hz - wartosc z zakresu najlepszej slyszalnosci ucha
                toast.show();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setLogo(R.drawable.logo); //dodawanie logo do toolbaru (pasek na gorze)
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //mój kod START

        Context context = getApplicationContext();
        CharSequence text = "Powtórz w razie potrzeby";
        int duration = Toast.LENGTH_SHORT;

        toast = Toast.makeText(context, text, duration);

        setVolumeControlStream(AudioManager.STREAM_MUSIC); //pozwala na kontrolę głośności

        playSound();
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
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings2) {

            //"po co kalibracja?"
            Intent intentKalInfo = new Intent(Main2Activity.this,PopUp2.class);
            startActivity(intentKalInfo);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_start) {

            Intent intentLauncher = new Intent(Main2Activity.this,MainActivity.class);
            startActivity(intentLauncher);

        } else if (id == R.id.nav_kalibruj) {

            Intent intentKal = new Intent(Main2Activity.this,Main2Activity.class);
            startActivity(intentKal);

        } else if (id == R.id.nav_info) {

            Intent intentInfo = new Intent(Main2Activity.this,PopUp0.class);
            startActivity(intentInfo);

        } else if (id == R.id.nav_powrot) {
            finish();
            //System.exit(0);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
