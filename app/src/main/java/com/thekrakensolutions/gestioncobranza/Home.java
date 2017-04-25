package com.thekrakensolutions.gestioncobranza;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Home extends AppCompatActivity {

    private ViewPager viewPager;
    private Home.MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private PrefManager prefManager;

    String _url1;
    String _url2;
    String _url3;
    String _url4;

    public static final String idu = "idu";
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    private int valueID = 0;



    String _urlGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        Intent para la siguiente version
        */
        Intent i = new Intent(Home.this, Lista_contratos.class);
        startActivity(i);

        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        _urlGet = "http://hyperion.init-code.com/zungu/app/vt_get_home.php?tipo=2";
        new Home.RetrieveFeedTaskGet().execute();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        valueID = sharedpreferences.getInt("idu", 0);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        //btnSkip = (Button) findViewById(R.id.btn_skip);
        //btnNext = (Button) findViewById(R.id.btn_next);


        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.home_slide1,
                R.layout.home_slide2,
                R.layout.home_slide3,
                R.layout.home_slide4};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new Home.MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);



    }

    public void goMenu(View v){
        Intent i = new Intent(Home.this, Menu.class);
        startActivity(i);
    }
    public void goNotificaciones(View v){
        /*
        Intent i = new Intent(Home.this, Notificaciones.class);
        startActivity(i);
        */
    }

    public void goPerfil(View v){
        /*
        //Intent i = new Intent(Home.this, Cuenta.class);
        Intent i = new Intent(Home.this, Promocionar_producto.class);
        startActivity(i);
        */
    }

    public void goCitas(View v){
        /*
        Intent i = new Intent(Home.this, Citas.class);
        startActivity(i);
        */
    }

    public void goPoliticas(View v){
        /*
        Intent i = new Intent(Home.this, Politicas_de_privacidad.class);
        startActivity(i);
        */
    }

    class RetrieveFeedTaskGet extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            try {
                Log.i("INFO url: ", _urlGet);
                URL url = new URL(_urlGet);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";
            } else {

                //TextView textoHome = (TextView) findViewById(R.id.textHome);
                ImageView imagenHomeUno = (ImageView) findViewById(R.id.imagenHomeUno);
                ImageView imagenHomeDos = (ImageView) findViewById(R.id.imagenHomeDos);
                ImageView imagenHomeTres = (ImageView) findViewById(R.id.imagenHomeTres);
                ImageView imagenHomeCuatro = (ImageView) findViewById(R.id.imagenHomeCuatro);

                try {

                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    //textoHome.setText(object.getString("texto"));

                    String foto = object.getString("imagen");
                    String _url = "http://hyperion.init-code.com/zungu/imagen_promocion/" + foto;

                    _url1 = "http://hyperion.init-code.com/zungu/imagen_promocion/" + foto;
                    _url2 = "http://hyperion.init-code.com/zungu/imagen_promocion/" + foto;
                    _url3 = "http://hyperion.init-code.com/zungu/imagen_promocion/" + foto;
                    _url4 = "http://hyperion.init-code.com/zungu/imagen_promocion/" + foto;

                    if(foto.length() > 3){
                        Log.d("foto", _url);
                        //Picasso.with(imagenHomeUno.getContext()).load(_url).fit().centerCrop().into(imagenHomeUno);
                        //Picasso.with(imagenHomeDos.getContext()).load(_url).fit().centerCrop().into(imagenHomeDos);
                        //Picasso.with(imagenHomeTres.getContext()).load(_url).fit().centerCrop().into(imagenHomeTres);
                        //Picasso.with(imagenHomeCuatro.getContext()).load(_url).fit().centerCrop().into(imagenHomeCuatro);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            Log.i("INFO", response);
        }

    }
    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(Home.this, MainActivity.class));
        finish();
    }

    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            Log.d("Posicion", Integer.toString(position));

            ImageView imagenHomeUno = (ImageView) findViewById(R.id.imagenHomeUno);
            ImageView imagenHomeDos = (ImageView) findViewById(R.id.imagenHomeDos);
            ImageView imagenHomeTres = (ImageView) findViewById(R.id.imagenHomeTres);
            ImageView imagenHomeCuatro = (ImageView) findViewById(R.id.imagenHomeCuatro);

            if(position == 0){
                Picasso.with(imagenHomeUno.getContext()).load(_url3).fit().centerCrop().into(imagenHomeUno);
            }
            if(position == 1){
                Picasso.with(imagenHomeDos.getContext()).load(_url3).fit().centerCrop().into(imagenHomeDos);
            }
            if(position == 2){
                Picasso.with(imagenHomeTres.getContext()).load(_url3).fit().centerCrop().into(imagenHomeTres);
            }
            if(position == 3){
                Picasso.with(imagenHomeCuatro.getContext()).load(_url3).fit().centerCrop().into(imagenHomeCuatro);
            }



        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
