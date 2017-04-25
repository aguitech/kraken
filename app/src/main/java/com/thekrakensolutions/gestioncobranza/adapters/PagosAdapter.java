package com.thekrakensolutions.gestioncobranza.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.thekrakensolutions.gestioncobranza.Detalle_contrato;
import com.thekrakensolutions.gestioncobranza.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hectoraguilar on 07/03/17.
 */

public class PagosAdapter extends BaseAdapter {

    ArrayList<String> _listaNombreVeterinarios;
    ArrayList<String> _listaImagenVeterinarios;
    ArrayList<String> _listaIdVeterinarios;
    Context context;
    public String _url;
    public String _urlGo;
    public int _valueID;

    private static LayoutInflater inflater=null;

    public PagosAdapter(int valueID, Detalle_contrato mainActivity, ArrayList<String> listaNombreVeterinarios, ArrayList<String> listaImagenVeterinarios, ArrayList<String> listaIdVeterinarios){
        _listaIdVeterinarios = listaIdVeterinarios;
        _listaImagenVeterinarios = listaImagenVeterinarios;
        _listaNombreVeterinarios = listaNombreVeterinarios;
        _valueID = valueID;

        context = mainActivity;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return _listaIdVeterinarios.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class Holder{
        TextView nombreVeterinario;
        ImageView imagenVeterinario;
        Button agregarVeterinario;
        ImageView detalleVeterinario;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final Holder holder = new Holder();
        final View rowView;
        rowView = inflater.inflate(R.layout.list_contratos, null);
        final int pos = i;

        holder.nombreVeterinario = (TextView) rowView.findViewById(R.id.txtNombreVeterinario);
        holder.imagenVeterinario = (ImageView) rowView.findViewById(R.id.imgVeterinario);
        //holder.agregarVeterinario = (Button) rowView.findViewById(R.id.buttonAgregar);
        holder.detalleVeterinario = (ImageView) rowView.findViewById(R.id.imgDetalle);

        /*

        holder.agregarVeterinario.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                _urlGo = "http://hyperion.init-code.com/zungu/app/vt_agregar_id_veterinario.php?idu=" + Integer.toString(_valueID) + "&idv=" + _listaIdVeterinarios.get(pos);
                Log.d("urlgo",_urlGo);
                new AgregarVeterinariosAdapter.RetrieveFeedTask().execute();
                Toast.makeText(holder.nombreVeterinario.getContext(), "Veterinario agregado con éxito.", Toast.LENGTH_LONG).show();
            }
        });
        */

        holder.detalleVeterinario.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //_urlGo = "http://hyperion.init-code.com/zungu/app/vt_agregar_id_veterinario.php?idu=" + Integer.toString(_valueID) + "&idv=" + _listaIdVeterinarios.get(pos);
                //Log.d("urlgo",_urlGo);
                //new AgregarVeterinariosAdapter.RetrieveFeedTask().execute();

                //Toast.makeText(holder.nombreVeterinario.getContext(), "Veterinario agregado con éxito.", Toast.LENGTH_LONG).show();
                //Intent i = new Intent(context, Detalle_veterinario.class);
                //context.startActivity(i);


                //Intent intent = new Intent(context, Detalle_veterinario.class);
                //intent.putExtra("idveterinario", i);
                //context.startActivity(intent);

                Log.d("click", String.valueOf(i));
                Log.d("click", _listaIdVeterinarios.get(i));

                //Intent intent = new Intent(context, Detalle_cliente.class);
                Intent intent = new Intent(context, Detalle_contrato.class);
                //intent.putExtra("idveterinario", _listaIdVeterinarios.get(i));
                //intent.putExtra("idcliente", _listaIdVeterinarios.get(i));
                intent.putExtra("idcontrato", _listaIdVeterinarios.get(i));
                context.startActivity(intent);

            }
        });

        holder.nombreVeterinario.setText(_listaNombreVeterinarios.get(i));
        _url = "http://hyperion.init-code.com/zungu/imagen_establecimiento/" + _listaImagenVeterinarios.get(i);
        Log.d("url", _url);
        Log.d("entro", "sii");

        Picasso.with(holder.imagenVeterinario.getContext()).load(_url)
                .into(holder.imagenVeterinario, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) holder.imagenVeterinario.getDrawable()).getBitmap();
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), imageBitmap);
                        circularBitmapDrawable.setCircular(true);
                        holder.imagenVeterinario.setImageDrawable(circularBitmapDrawable);
                    }
                    @Override
                    public void onError() {

                    }
                });

        return rowView;
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            try {
                Log.i("INFO url: ", _urlGo);
                URL url = new URL(_urlGo);
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
                /*
                DESCOMENTAR
                Intent i = new Intent(context, Principal.class);
                context.startActivity(i);
                */
            }
            Log.i("INFO", response);
        }
    }
}
