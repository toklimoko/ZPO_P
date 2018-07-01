package com.tomek.audiometr;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;

public class Main3Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener { //podwójna implementacja

    private Button btn_generuj;
    private Button btn_Play_bad;
    private Button btn_Stop_bad;
    private Button btn_rysuj;

    private Spinner spinner;

    private double frequency = 0.0;
    private double amplitude = 0.0;
    private boolean b = false;

    private Toast toast1;
    private Toast toast2;
    private Toast toast3;
    private Toast toast4;
    private Toast toast5;

    private ArrayList<Double> listaF; //lista czestotliwosci podczas 1 próby
    private ArrayList<Double> listaA; //lista amplitud podczas 1 próby
    public ArrayList<Double> listaX; //lista wartości X do wykresu (częstotliwości dla każdej z prób)
    public ArrayList<Double> listaY; //lista wartości Y do wykresu (amplitudy końcowe dla każdej z prób)

    private XYSeries series; //seria danych do wykresu

    private LinearLayout chartLayout;
    private GraphicalView chartView;



    //BT

    private Dialog dialog;
    private BluetoothChatService mBluetoothConnection;


    private ActivityCallback mActivityCallback = new ActivityCallback() {
        @Override
        public void setReceivedBytes(String incomingMessage) {
            readReceivedData(incomingMessage);
        }

        private void readReceivedData(final String data){

            // /////////CZYTANIE BT///////////////

            String dataSplit[] = data.split(","); //podziel data na event, f, a

                String event = dataSplit[0];
                final float x = Float.parseFloat(dataSplit[1]);
                final float y = Float.parseFloat(dataSplit[2]);


            switch (event) {
                case "pg":
                    //gdy pacjent wcisnie "start"
                    buttonGeneruj();
                    buttonStart();

                    break;
                case "ns":
                   //tu pacjent wysyla w petli ns, dopoki pacjent nie wcisnie "slysze"
                    buttonStart();
                    msgTrwaBadanie();

                    break;
                case "sl":

                    //gdy pacjent wcisnal "slysze"
                    buttonStop();
                    msgBadanieZakonczone();

                    break;



            }
       }

            ////WYSYŁANIE/////
//    action = "start," + x + "," + y + "," + mode;
//    byte[] bytesS = action.getBytes();
//                    if(mBluetoothConnection != null) {
//        mBluetoothConnection.write(bytesS);
//    }
        @Override
        public void setBluetoothConnectionInstance(BluetoothChatService instance) {
           mBluetoothConnection = instance;
        }

        @Override
        public void dismissConnectionDialog() {
            dialog.dismiss();
        }

        @Override
        public void setConnectionStatus(final BluetoothChatService.ConnectionStatus status) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    switch (status) {
                        case CONNECTED:
                            Toast.makeText(Main3Activity.this, "Connected",Toast.LENGTH_LONG).show();
                            break;
                        case DISCONNECTED:
                            Toast.makeText(Main3Activity.this, "Disconnected",Toast.LENGTH_LONG).show();
                            break;
                    }

                }
            });
        }
    };



    //deklaracja nazw itemów do spinnera
    private static final String[]paths = {"Próba 1", "Próba 2", "Próba 3", "Próba 4", "Próba 5",
            "Próba 6", "Próba 7", "Próba 8", "Próba 9", "Próba 10", "Próba 11", "Próba 12",
            "Próba 13", "Próba 14", "Próba 15", "Próba 16", "Próba 17", "Próba 18", "Próba 19",
            "Próba 20"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //obsluga lewego paska
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //deklaracja spinnera
        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Main3Activity.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //tworzę nowe listy x i y do wykresów
        listaX = new ArrayList<>();
        listaY = new ArrayList<>();

        buttonGeneruj();
        buttonStop();
        buttonStart();
        buttonRysuj();
        lekarzGotowy();

        btn_generuj.setVisibility(View.VISIBLE);
        btn_Play_bad.setVisibility(View.INVISIBLE);
        btn_Stop_bad.setVisibility(View.INVISIBLE);
        btn_rysuj.setVisibility(View.INVISIBLE);

        Context context = getApplicationContext();
        CharSequence text = "Załadowano dane. Można zacząć badanie.";
        CharSequence text2 = "Generowanie dźwięku";
        CharSequence text3 = "Zapisano pomiar. Wybierz nowy dźwięk.";
        CharSequence text4 = "TRWA BADANIE.";
        CharSequence text5 = "PACJENT SŁYSZY.";
        int duration = Toast.LENGTH_SHORT;

        toast1 = Toast.makeText(context, text, duration);
        toast2 = Toast.makeText(context,text2, duration);
        toast3 = Toast.makeText(context,text3, duration);
        toast4 = Toast.makeText(context,text4, duration);
        toast5 = Toast.makeText(context,text5, duration);


        if (series != null)
        series.clearSeriesValues();
    }

    public void lekarzGotowy(){

        String action = "lg," + 0 + "," + 0; //'lekarz gotowy'
        byte[] bytesS = action.getBytes();
        if (mBluetoothConnection != null) {
            mBluetoothConnection.write(bytesS);
        }
    }

    public void wyslijDaneSygnalu(){

        String action = "s," + frequency + "," + amplitude; //s,czestotliwosc,amplituda
        byte[] bytesS = action.getBytes();
        if (mBluetoothConnection != null) {
            mBluetoothConnection.write(bytesS);
        }
    }


    public void msgTrwaBadanie(){
        toast4.show();
    }

    public void msgBadanieZakonczone(){
        toast5.show();
    }

    public void buttonGeneruj(){
        btn_generuj= (Button) findViewById(R.id.btn_generuj);
        btn_generuj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (series != null)
                series.clearSeriesValues();


                listaF = new ArrayList<>();
                listaA = new ArrayList<>();

                //dodaj element 0 do list

                amplitude = 0;

                    listaF.add(frequency);
                    listaA.add(amplitude);

                btn_generuj.setVisibility(View.INVISIBLE);
                btn_Play_bad.setVisibility(View.VISIBLE);
                btn_Stop_bad.setVisibility(View.VISIBLE);
                btn_rysuj.setVisibility(View.INVISIBLE);

                    toast1.show();
            }
        });
    }



    public void buttonStart(){
        btn_Play_bad = (Button) findViewById(R.id.btn_glosniej);
        btn_Play_bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_generuj.setVisibility(View.INVISIBLE);
                btn_Play_bad.setVisibility(View.VISIBLE);
                btn_Stop_bad.setVisibility(View.VISIBLE);
                btn_rysuj.setVisibility(View.INVISIBLE);



                amplitude += 0.05;

                wyslijDaneSygnalu(); //wysylamy dane sygnalu do pacjenta

                listaF.add(frequency);
                listaA.add(amplitude);





//                //tyle razy ile wcisnieto przycisk tyle podglosnij i dodaj do list F i A
//
//                while (b = true){
//
//                    amplitude += 0.05;
//
//                    Play play = new Play(frequency, amplitude, 1);
//
//                    //toast2.show();
//
//                    listaF.add(frequency);
//                    listaA.add(amplitude);
//
//                    break;
//                }
            }
        });
    }



    public void buttonStop(){
        btn_Stop_bad = (Button) findViewById(R.id.btn_slysze);
        btn_Stop_bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                b = false;

                //pobierz te wartości częstotliwości i amplitudy, przy których użytkownik wcisnął "słyszę"
                frequency = listaF.get(listaF.size() - 1);
                amplitude = listaA.get(listaA.size() - 1);

                //dodaj nowe wartości do list wykorzystywanych do tworzenia wykresów
                listaX.add(frequency);
                listaY.add(amplitude);

                btn_generuj.setVisibility(View.VISIBLE);
                btn_Play_bad.setVisibility(View.INVISIBLE);
                btn_Stop_bad.setVisibility(View.INVISIBLE);
                btn_rysuj.setVisibility(View.VISIBLE);

                toast3.show();
            }
        });
    }


    public void buttonRysuj(){
        btn_rysuj= (Button) findViewById(R.id.btn_doWykresu);
        btn_rysuj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rysuj();
          }
        });
    }

    public void rysuj(){

        series = new XYSeries("Audiogram");

        //dodaj każdy punkt pomiarowy do serii danych do wykresu
        for (int i = 0; i<listaX.size();i++){
            series.add(listaX.get(i),listaY.get(i));

        }

        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(3);
        renderer.setColor(Color.BLUE);
        renderer.setPointStyle(PointStyle.CIRCLE);

        XYMultipleSeriesRenderer mrenderer = new XYMultipleSeriesRenderer();
        mrenderer.addSeriesRenderer(renderer);
        mrenderer.setYAxisMin(1.5);

        mrenderer.setYAxisMax(0);
        mrenderer.setXAxisMin(0);
        mrenderer.setXAxisMax(18000);
        mrenderer.setMarginsColor(Color.WHITE);
        mrenderer.setShowGrid(true);
        mrenderer.setMarginsColor(Color.WHITE);
        mrenderer.setGridColor(Color.LTGRAY);
        mrenderer.setAxesColor(Color.BLACK);
        mrenderer.setXLabelsColor(Color.BLACK);
        mrenderer.setYLabelsColor(0, Color.BLACK);
        mrenderer.setYLabelsAlign(Paint.Align.CENTER);
        mrenderer.setLabelsTextSize(30);
        mrenderer.setLegendTextSize(30);
        mrenderer.setFitLegend(true);
        mrenderer.setShowLegend(false);
        mrenderer.setPanEnabled(true,false);
        mrenderer.setZoomEnabled(true,false);
        //mrenderer.setZoomInLimitY(5);
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        chartLayout = (LinearLayout) findViewById(R.id.llv);
        chartView = ChartFactory.getLineChartView(this,dataset,mrenderer);
        chartLayout.addView(chartView);
        chartView.repaint();

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
        getMenuInflater().inflate(R.menu.main3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings3) {

            //"jak wykonać badanie?"
            Intent intentInfo = new Intent(Main3Activity.this,PopUp.class);
            startActivity(intentInfo);

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

            Intent intentLauncher = new Intent(Main3Activity.this, MainActivity.class);
            startActivity(intentLauncher);

        }else if (id == R.id.nav_bt) {

            dialog = new ConnectionDialog(Main3Activity.this, mActivityCallback);
            dialog.show();
        }

        else if (id == R.id.nav_kalibruj) {

            Intent intentKal = new Intent(Main3Activity.this,Main2Activity.class);
            startActivity(intentKal);

        } else if (id == R.id.nav_info) {

            Intent intentInfo = new Intent(Main3Activity.this,PopUp0.class);
            startActivity(intentInfo);

        } else if (id == R.id.nav_powrot) {
            finish();
            //System.exit(0); //drugi sposób zamykania aktywności

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        //załaduj częstotliwości - deklaracja wartości itemów do spinnera

        switch (position) {
            case 0:
                frequency = 1000;
                break;
            case 1:
                frequency = 8000;
                break;
            case 2:
                frequency = 150;
                break;
            case 3:
                frequency = 12000;
                break;
            case 4:
                frequency = 3000;
                break;
            case 5:
                frequency = 1500;
                break;
            case 6:
                frequency = 100;
                break;
            case 7:
                frequency = 4000;
                break;
            case 8:
                frequency = 15000;
                break;
            case 9:
                frequency = 2500;
                break;
            case 10:
                frequency = 500;
                break;
            case 11:
                frequency = 6000;
                break;
            case 12:
                frequency = 17000;
                break;
            case 13:
                frequency = 250;
                break;
            case 14:
                frequency = 14000;
                break;
            case 15:
                frequency = 125;
                break;
            case 16:
                frequency = 16000;
                break;
            case 17:
                frequency = 400;
                break;
            case 18:
                frequency = 700;
                break;
            case 19:
                frequency = 10000;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
