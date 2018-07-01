package com.tomek.audiometr;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.widget.ImageView;

/**
 * Created by tokli on 30.01.2018.
 *
 * klssa obslugujaca popUp'a drugiego (dot. celu kalibracji)
 */

public class PopUp2 extends Activity {

    private ImageView mImageView;
    double screenFactor = 0.85;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pop_up_window2);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*1.1*screenFactor),(int) (height*0.9*screenFactor));


        mImageView = (ImageView) findViewById(R.id.img_manual);
        mImageView.setImageResource(R.drawable.manual_icon);


    }
}
