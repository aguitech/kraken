package com.thekrakensolutions.gestioncobranza;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class Registro extends AppCompatActivity {

    SharedPreferences sharedpreferences; //necesaria para guardar / leer datos globales / preferencias
    public static final String MyPREFERENCES = "datosUsuario" ; //necesaria para guardar / leer datos globales / preferencias

    EditText txtEmail;
    EditText txtPassword;
    EditText txtNombre;
    EditText txtApellido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //inicializa el guardado o lectura de los datos globales del usuario
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        //inicializa el uso de peticiones get / post
        AndroidNetworking.initialize(getApplicationContext());

        initControls();
    }

    private void initControls(){
        txtEmail = (EditText)findViewById(R.id.txtEmail);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        txtNombre = (EditText)findViewById(R.id.txtNombre);
        txtApellido = (EditText)findViewById(R.id.txtApellido);
    }

    public void goLogin(View v){
        Intent i = new Intent(Registro.this, Login.class);
        startActivity(i);
    }

    public void doRegistro(View v){
        String email = txtEmail.getText().toString();
        String pass = txtPassword.getText().toString();
        String nombre = txtNombre.getText().toString();
        String apellido = txtApellido.getText().toString();

        if(email.length() < 3 || pass.length() < 3 || nombre.length() < 3 || apellido.length() < 3){
            Toast.makeText(getApplicationContext(), R.string.error_validacion, Toast.LENGTH_LONG).show();
        } else {
            jsonRegistro(email, pass, nombre, apellido);
        }
    }

    private void jsonRegistro(String usuario, String password, String nombre, String apellido){
        String _url = "http://hyperion.init-code.com/zungu/app/registro.php";

        AndroidNetworking.post(_url)
                .addBodyParameter("correo", usuario)
                .addBodyParameter("pass", password)
                .addBodyParameter("nombre", nombre)
                .addBodyParameter("apellido", apellido)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            int ID = response.getInt("id_usuario");
                            String text;

                            if(ID == 0){
                                text = "El correo ya éxiste";
                                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                            } else {
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putInt("idu", ID);
                                editor.commit();

                                Log.i("IDU", Integer.toString(ID));

                                text = "Bienvenido a Zungu usuarios";
                                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

                                Intent i = new Intent(Registro.this, Home.class);
                                startActivity(i);
                                finish();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {

                    }
                });
    }
}
