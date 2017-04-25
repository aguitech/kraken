package com.thekrakensolutions.gestioncobranza;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                PrefManager prefManager = new PrefManager(getApplicationContext());

                // make first time launch TRUE
                prefManager.setFirstTimeLaunch(true);

                //Intent i = new Intent(MainActivity.this, WelcomeActivity.class);
                //NO ES PARA CPCL
                //Intent i = new Intent(MainActivity.this, ReceiptDemo.class);
                //SI FUNCIONA
                //Intent i = new Intent(MainActivity.this, ConnectivityDemo.class);
                //SI FUNCIONA
                //Intent i = new Intent(MainActivity.this, ImagePrintDemo.class);
                //NO FUNCIONA
                //Intent i = new Intent(MainActivity.this, ListFormatsDemo.class);
                //SI FUNCIONA ESCRIBE TEST
                //Intent i = new Intent(MainActivity.this, SendFileDemo.class);
                //NO FUNCIONA
                //Intent i = new Intent(MainActivity.this, PrinterStatusScreen.class);
                //NO FUNCIONA
                //Intent i = new Intent(MainActivity.this, SignatureArea.class);
                //SI FUNCIONA
                //Intent i = new Intent(MainActivity.this, SigCaptureDemo.class);
                //Intent i = new Intent(MainActivity.this, SendFileDemo.class);
                //El recibo
                //Intent i = new Intent(MainActivity.this, SendFileReciboURL.class);
                //Intent i = new Intent(MainActivity.this, SendFileReciboURL.class);
                Intent i = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(i);

                finish();
            }
        }, SPLASH_TIME_OUT);
    }
    @Override
    public void onBackPressed(){

    }
}
