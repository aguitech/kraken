package com.thekrakensolutions.gestioncobranza;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.thekrakensolutions.gestioncobranza.adapters.ClientesAdapter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by hectoraguilar on 07/03/17.
 */

public class Lista_clientes extends AppCompatActivity {
    ListView lv;
    EditText buscador;
    Context context;
    private String _url;

    public static final String idu = "idu";
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    private int valueID = 0;

    public static ArrayList<String> listaNombreVeterinarios = new ArrayList<String>();
    public static ArrayList<String> listaImagenVeterinarios = new ArrayList<String>();
    public static ArrayList<String> listaIdVeterinario = new ArrayList<String>();

    public Lista_clientes mActivity = this;
    public ClientesAdapter _mascotasAdapter;

    private String _urlNotificaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_clientes);

        lv = (ListView) findViewById(R.id.list_veterinarios);
        buscador = (EditText) findViewById(R.id.txtBuscador);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        valueID = sharedpreferences.getInt("idu", 0);

        /*
        _urlNotificaciones = "http://hyperion.init-code.com/zungu/app/vt_get_numero_notificaciones.php?idv=" + valueID;
        new Lista_clientes.RetrieveFeedTaskNotificaciones().execute();
        */

        buscador.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= ((buscador.getWidth() - buscador.getTotalPaddingRight()))) {
                        String param = buscador.getText().toString();

                        try {

                            _url = "http://hyperion.init-code.com/zungu/app/vt_get_veterinarios.php?idp=" + Integer.toString(valueID) + "&search=" + URLEncoder.encode(param, "UTF-8");

                            lv.setAdapter(null);
                            new Lista_clientes.RetrieveFeedTask().execute();
                            return true;
                            /*
                            _url = "http://hyperion.init-code.com/zungu/app/vt_get_veterinarios.php?idp=" + Integer.toString(valueID) + "&search=" + URLEncoder.encode(param, "UTF-8");

                            lv.setAdapter(null);
                            new Lista_veterinarios.RetrieveFeedTask().execute();
                            return true;
                            */
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    }
                }
                return false;
            }
        });

        //_url = "http://hyperion.init-code.com/zungu/app/vt_get_veterinarios.php?idp=" + Integer.toString(valueID);
        _url = "http://thekrakensolutions.com/cobradores/android_get_clientes.php?id=" + Integer.toString(valueID);
        Log.d("url_veterinarios", _url);
        new Lista_clientes.RetrieveFeedTask().execute();
    }

    public void goMenu(View v){
        Intent i = new Intent(Lista_clientes.this, Menu.class);
        startActivity(i);
    }
    /*
    public void goMenu(View v){
        Intent i = new Intent(Lista_veterinarios.this, Menu.class);
        startActivity(i);
    }
    public void goNotificaciones(View v){
        Intent i = new Intent(Lista_veterinarios.this, Notificaciones.class);
        startActivity(i);
    }

    public void goBack(View v){
        finish();
    }

    public void showMiCuenta(View v){
        Intent i = new Intent(Lista_veterinarios.this, MiCuentaSuscripciones.class);
        startActivity(i);
    }
    */

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            try {
                Log.i("INFO url: ", _url);
                URL url = new URL(_url);
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
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            } else {
                try {
                    JSONTokener tokener = new JSONTokener(response);
                    JSONArray arr = new JSONArray(tokener);

                    listaNombreVeterinarios.clear();
                    listaImagenVeterinarios.clear();
                    listaIdVeterinario.clear();


                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject jsonobject = arr.getJSONObject(i);

                        /*
                        listaNombreVeterinarios.add(jsonobject.getString("nombre"));
                        listaImagenVeterinarios.add(jsonobject.getString("foto"));
                        listaIdVeterinario.add(jsonobject.getString("id_veterinario"));
                        */

                        listaNombreVeterinarios.add(jsonobject.getString("numero_cliente") + " " + jsonobject.getString("nombre") + " " + jsonobject.getString("apaterno"));

                        //listaImagenVeterinarios.add(jsonobject.getString("foto"));
                        listaImagenVeterinarios.add(jsonobject.getString("imagen"));
                        listaIdVeterinario.add(jsonobject.getString("id_cliente"));

                    }

                    _mascotasAdapter = new ClientesAdapter(valueID, mActivity, listaNombreVeterinarios, listaImagenVeterinarios, listaIdVeterinario);
                    lv.setAdapter(_mascotasAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.i("INFO", response);
        }
    }
    /*
    class RetrieveFeedTaskNotificaciones extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            try {
                Log.i("INFO url: ", _urlNotificaciones);
                URL url = new URL(_urlNotificaciones);
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
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            } else {
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;

                try {
                    JSONTokener tokener = new JSONTokener(response);
                    JSONArray arr = new JSONArray(tokener);

                    TextView numeroNotificaciones = (TextView) findViewById(R.id.numeroNotificaciones);

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject jsonobject = arr.getJSONObject(i);
                        //Log.d("nombre",jsonobject.getString("nombre"));

                        //"":"45","":"9","":"0","fecha":"2016-11-19 15:59:00","":"","acepto":"0","":"","":"1","id_pago":"0","id_veterinaria":"0"},

                        if(jsonobject.getString("numero_notificaciones").equals("")){
                            numeroNotificaciones.setVisibility(View.GONE);
                        }else{
                            numeroNotificaciones.setVisibility(View.VISIBLE);
                            numeroNotificaciones.setText(jsonobject.getString("numero_notificaciones"));
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.i("INFO", response);
        }
    }
    */
}
