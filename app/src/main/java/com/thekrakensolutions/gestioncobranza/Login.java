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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;


public class Login extends AppCompatActivity {

    SharedPreferences sharedpreferences; //necesaria para guardar / leer datos globales / preferencias
    //public static final String MyPREFERENCES = "datosUsuario" ; //necesaria para guardar / leer datos globales / preferencias
    public static final String MyPREFERENCES = "MyPrefs" ; //necesaria para guardar / leer datos globales / preferencias

    EditText txtEmail;
    EditText txtPassword;

    CallbackManager callbackManager;
    private String _url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.agutiech.massiveapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        */

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        /*
        Button btnFb = (Button) findViewById(R.id.btnFacebook);
        btnFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedIn()){

                    Profile perfil = com.facebook.Profile.getCurrentProfile();
                    String nombre = perfil.getName();
                    String idu = perfil.getId();
                    String fn = perfil.getFirstName();
                    String ln = perfil.getLastName();
                    String mn = perfil.getMiddleName();

                    Log.e("face name", nombre);
                    Log.e("face idu", idu);
                    Log.e("face fn", fn);
                    Log.e("face ln", ln);
                    Log.e("face mn", mn);

                    _url = "http://hyperion.init-code.com/zungu/app/lgfacebook.php?fid=" + idu + "&apellido=" + ln + "&nombre=" + nombre;
                    jsonLoginFacebook(idu,ln,nombre);
                }else{

                    com.facebook.login.widget.LoginButton btn = new LoginButton(Login.this);

                    btn.setReadPermissions(Arrays.asList("email", "user_photos", "public_profile"));

                    btn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                        private ProfileTracker mProfileTracker;

                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.d("entro","si4");
                            if(Profile.getCurrentProfile() == null) {
                                mProfileTracker = new ProfileTracker() {
                                    @Override
                                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                        Log.v("facebook - profile", profile2.getFirstName());

                                        String nombre = profile2.getName();
                                        String idu = profile2.getId();
                                        String fn = profile2.getFirstName();
                                        String ln = profile2.getLastName();
                                        String mn = profile2.getMiddleName();

                                        _url = "http://hyperion.init-code.com/zungu/app/lgfacebook.php?fid=" + idu + "&apellido=" + ln + "&nombre=" + nombre;
                                        //new RetrieveFeedTask().execute();
                                        jsonLoginFacebook(idu,ln,nombre);

                                        mProfileTracker.stopTracking();
                                    }
                                };
                                mProfileTracker.startTracking();
                            }
                            else {
                                Profile profile = Profile.getCurrentProfile();
                                Log.v("facebook - profile", profile.getFirstName());
                            }

                            Context context = getApplicationContext();
                            CharSequence text = "Bienvenido";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                            Intent goNext = new Intent(Login.this, Home.class);
                            goNext.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(goNext);
                        }

                        @Override
                        public void onCancel() {
                            Log.d("entro","si5");
                            Context context = getApplicationContext();
                            CharSequence text = "Login con facebook cancelado";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Log.d("entro","si6");
                            Context context = getApplicationContext();
                            CharSequence text = "Se ha presentado un problema con facebook";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    });

                    btn.performClick();
                }
            }
        });

        */
        initControls();

        //inicializa el guardado o lectura de los datos globales del usuario
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        //inicializa el uso de peticiones get / post
        AndroidNetworking.initialize(getApplicationContext());
    }

    private void initControls(){
        txtEmail = (EditText)findViewById(R.id.txtEmail);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
    }

    public void goRegistro(View v){
        Intent i = new Intent(Login.this, Registro.class);
        startActivity(i);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public void doLogin(View v){
        if(txtEmail.getText().length() < 3 || txtPassword.getText().length() < 3){
            Toast.makeText(getApplicationContext(), R.string.error_validacion, Toast.LENGTH_LONG).show();
        } else {
            jsonLogin(txtEmail.getText().toString(), txtPassword.getText().toString());
        }
    }

    private void jsonLoginFacebook(String fid,String apellido, String nombre){
        String _url = "http://hyperion.init-code.com/zungu/app/lgfacebook.php";

        AndroidNetworking.post(_url)
                .addBodyParameter("fid", fid)
                .addBodyParameter("apellido", apellido)
                .addBodyParameter("nombre",nombre)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            int ID = response.getInt("id_usuario");
                            String text;

                            if(ID == 0){
                                text = "Usuario o password no válido.";
                                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                            } else {
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putInt("idu", ID);
                                editor.commit();

                                Log.i("IDU", Integer.toString(ID));

                                text = "Bienvenido a Zungu usuarios";
                                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(Login.this, Home.class);

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

    private void jsonLogin(String usuario, String password){
        String _url = "http://thekrakensolutions.com/cobradores/android_iniciar_sesion.php";
Log.d("url",_url);
        AndroidNetworking.post(_url)
                .addBodyParameter("correo", usuario)
                .addBodyParameter("pass", password)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Log.i("info",response.toString());
                            int ID = response.getInt("id_usuario");
                            String text;

                            if(ID == 0){
                                text = "Usuario o password no válido.";
                                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                            } else {
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putInt("idu", ID);
                                editor.commit();

                                Log.i("IDU", Integer.toString(ID));

                                text = "Bienvenido a The Kraken Solutions";
                                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(Login.this, Home.class);

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
