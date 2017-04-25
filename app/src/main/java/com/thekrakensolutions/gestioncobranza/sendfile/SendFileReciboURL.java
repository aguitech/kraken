/***********************************************
 * CONFIDENTIAL AND PROPRIETARY 
 * 
 * The source code and other information contained herein is the confidential and the exclusive property of
 * ZIH Corp. and is subject to the terms and conditions in your end user license agreement.
 * This source code, and any other information contained herein, shall not be copied, reproduced, published, 
 * displayed or distributed, in whole or in part, in any medium, by any means, for any purpose except as
 * expressly permitted under such license agreement.
 * 
 * Copyright ZIH Corp. 2012
 * 
 * ALL RIGHTS RESERVED
 ***********************************************/

package com.thekrakensolutions.gestioncobranza.sendfile;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.thekrakensolutions.gestioncobranza.ConnectionScreen;
import com.thekrakensolutions.gestioncobranza.Lista_contratos;
import com.thekrakensolutions.gestioncobranza.util.SettingsHelper;
import com.thekrakensolutions.gestioncobranza.util.UIHelper;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
*/

public class SendFileReciboURL extends ConnectionScreen {

    private UIHelper helper = new UIHelper(this);

    private String _urlGet;

    String cpclConfigLabel;

    String idString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testButton.setText("Send Test File");

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            idString= null;

        } else {
            idString= extras.getString("idcontrato");
            Log.d("id_vet", idString);

        }

        //_urlGet = "http://thekrakensolutions.com/cobradores/android_get_print.php";
        _urlGet = "http://thekrakensolutions.com/cobradores/android_get_print.php?id_contrato=" + idString;
        new SendFileReciboURL.RetrieveFeedTaskGet().execute();
    }

    @Override
    public void performTest() {
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                sendFile();
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();

    }

    private void sendFile() {
        Connection connection = null;
        if (isBluetoothSelected() == true) {
            connection = new BluetoothConnection(getMacAddressFieldText());
        } else {
            try {
                int port = Integer.parseInt(getTcpPortNumber());
                connection = new TcpConnection(getTcpAddress(), port);
            } catch (NumberFormatException e) {
                helper.showErrorDialogOnGuiThread("Port number is invalid");
                return;
            }
        }
        try {
            helper.showLoadingDialog("Sending file to printer ...");
            connection.open();
            ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
            testSendFile(printer);
            connection.close();
        } catch (ConnectionException e) {
            helper.showErrorDialogOnGuiThread(e.getMessage());
        } catch (ZebraPrinterLanguageUnknownException e) {
            helper.showErrorDialogOnGuiThread(e.getMessage());
        } finally {
            helper.dismissLoadingDialog();
        }
    }

    private void testSendFile(ZebraPrinter printer) {
        try {
            File filepath = getFileStreamPath("TEST.LBL");
            createDemoFile(printer, "TEST.LBL");
            printer.sendFileContents(filepath.getAbsolutePath());
            SettingsHelper.saveBluetoothAddress(this, getMacAddressFieldText());
            SettingsHelper.saveIp(this, getTcpAddress());
            SettingsHelper.savePort(this, getTcpPortNumber());

        } catch (ConnectionException e1) {
            helper.showErrorDialogOnGuiThread("Error sending file to printer");
        } catch (IOException e) {
            helper.showErrorDialogOnGuiThread("Error creating file");
        }
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
                /*
                TextView lblNombre = (TextView) findViewById(R.id.lblNombre);
                TextView lblDireccion = (TextView) findViewById(R.id.lblDireccion);
                ImageView foto = (ImageView) findViewById(R.id.imgFoto);

                TextView lblNombreVo = (TextView) findViewById(R.id.txtNombreA);
                TextView lblEmailVo = (TextView) findViewById(R.id.txtEmailA);
                TextView lblCelVo = (TextView) findViewById(R.id.txtCelA);
                //TextView lblCedVo = (TextView) findViewById(R.id.txtCedA);
                TextView txtDireccion = (TextView) findViewById(R.id.txtDireccion);

                final ImageView fotoVeterinario = (ImageView) findViewById(R.id.imgVeterinario);
                */



                try {

                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();


                    //String cpclConfigLabel =  "! U1 JOURNAL\r\n" + "! U1 SETLP 4 0 47\r\n" + "AGUITECH CORPORATION\r\n" + "! U1 SETLP 7 0 24\r\n" + "14:40 PM      Thursday, 06/04/20\r\n" + "Quantity  Item         Unit    Total\r\n" + "1         Babelfish    $4.20   $4.20\r\n" + "Tax:            5%   $0.21\r\n" + "! U1 SETSP 5\r\n" + "Total:! U1 SETSP 0\r\n" + "$4.41\r\n" + "! U1 SETLP 5 2 46\r\n" + "THE KRAKEN SOLUTIONS\r\n" + "! U1 SETLP 7 0 24\r\n" + "Av Insurgentes Sur 533 Int 2 Col. Hipodromo Condesa C.P. 06170 Del. Cuauhtemoc\r\n" + "(401) 555-4CUT\r\n" + "FORM\r\n" + "PRINT\r\n";
                    //cpclConfigLabel =  "! U1 JOURNAL\r\n" + "! U1 SETLP 4 0 47\r\n" + "AGUITECH CORPORATION\r\n" + "! U1 SETLP 7 0 24\r\n" + "14:40 PM      Thursday, 06/04/20\r\n" + "Quantity  Item         Unit    Total\r\n" + "1         Babelfish    $4.20   $4.20\r\n" + "Tax:            5%   $0.21\r\n" + "! U1 SETSP 5\r\n" + "Total:! U1 SETSP 0\r\n" + "$4.41\r\n" + "! U1 SETLP 5 2 46\r\n" + "THE KRAKEN SOLUTIONS\r\n" + "! U1 SETLP 7 0 24\r\n" + "Av Insurgentes Sur 533 Int 2 Col. Hipodromo Condesa C.P. 06170 Del. Cuauhtemoc\r\n" + "(401) 555-4CUT\r\n" + "FORM\r\n" + "PRINT\r\n";
                    cpclConfigLabel = object.getString("resultado");


                    /*
                    String _nombre_vo = object.getString("numero_cliente") + " - " + object.getString("nombre") + " " + object.getString("apaterno") + " " + object.getString("amaterno");

                    String _telefono_vo = object.getString("telefono_casa");
                    String _cedula_vo = object.getString("numero_cliente");
                    String _email_vo = object.getString("fecha_nacimiento");
                    String _imagen_vo = object.getString("sexo");

                    //String txtDireccion_ = object.getString("calle") + " " + object.getString("numero_exterior") + " " + object.getString("numero_interior")  + " , Colonia " + object.getString("colonia")  + " , Delegación/Municipio " + object.getString("delegacion_municipio")  + " , Estado " + object.getString("estado")  + " , C.P. " + object.getString("codigo_postal")  + " , País " + object.getString("pais")  + " , entre calle " + object.getString("entre_calle")  + " y calle " + object.getString("y_calle")  + " " + object.getString("amaterno")  + " " + object.getString("amaterno")  + " " + object.getString("amaterno")  + " " + object.getString("amaterno")  + " " + object.getString("amaterno")  + " " + object.getString("amaterno")  + " " + object.getString("amaterno")  + " " + object.getString("amaterno")  + " " + object.getString("amaterno")  + " " + object.getString("amaterno")  + " " + object.getString("amaterno")  + " " + object.getString("amaterno")  + " " + object.getString("amaterno")  + " " + object.getString("amaterno")  + " " + object.getString("amaterno")  + " " + object.getString("amaterno")  + " " + object.getString("amaterno");
                    String txtDireccion_ = object.getString("calle") + " " + object.getString("numero_exterior") + " " + object.getString("numero_interior")  + " , Colonia " + object.getString("colonia")  + " , Delegación/Municipio " + object.getString("delegacion_municipio")  + " , Estado " + object.getString("estado")  + " , C.P. " + object.getString("codigo_postal")  + " , País " + object.getString("pais")  + " , entre calle " + object.getString("entre_calle")  + " y calle " + object.getString("y_calle");


                    {"id_cliente":"1","cliente":"","numero_cliente":"0","fecha_nacimiento":"0000-00-00","sexo":"mkl","imagen":"",":"klmkl","":"mkl","":"mklm","":"klm","":"klmkl","telefono_casa":"","telefono_celular":"","telefono_oficina":"","":"","":"","ocupacion":"","direccion_trabajo":"","nombre_pareja":"","ocupacion_pareja":"","telefono_pareja":"","complexion":"","estatura":"","tez":"","edad_rango":"","cabello":"","color_cabello":"","tipo_identificacion":"","numero_identificacion":"","nombre_referencia_1":"","direccion_referencia_1":"","telefono_referencia_1":"","parentesco_referencia_1":"","anios_conocerce_referencia_1":"","nombre_referencia_2":"","direccion_referencia_2":"","telefono_referencia_2":"","parentesco_referencia_2":"","anios_conocerce_referencia_2":"","maps_localizacion":"","imagen_plano_localizacion":"","fachada_casa":"","a_lado_casa":"","enfrente_casa":"","autorizacion_contratos":"","id_creador":"0","id_empresa":"0"}

                    {"id_cliente":"1","cliente":"","numero_cliente":"0","nombre":"mklmklmklm","apaterno":"klmkl","amaterno":"mklm","fecha_nacimiento":"0000-00-00","sexo":"mkl","imagen":"","calle":"mklm","numero_exterior":"klm","numero_interior":"klmkl","colonia":"mkl","delegacion_municipio":"mkl","estado":"mklm","codigo_postal":"klm","pais":"klmkl","telefono_casa":"","telefono_celular":"","telefono_oficina":"","entre_calle":"","y_calle":"","ocupacion":"","direccion_trabajo":"","nombre_pareja":"","ocupacion_pareja":"","telefono_pareja":"","complexion":"","estatura":"","tez":"","edad_rango":"","cabello":"","color_cabello":"","tipo_identificacion":"","numero_identificacion":"","nombre_referencia_1":"","direccion_referencia_1":"","telefono_referencia_1":"","parentesco_referencia_1":"","anios_conocerce_referencia_1":"","nombre_referencia_2":"","direccion_referencia_2":"","telefono_referencia_2":"","parentesco_referencia_2":"","anios_conocerce_referencia_2":"","maps_localizacion":"","imagen_plano_localizacion":"","fachada_casa":"","a_lado_casa":"","enfrente_casa":"","autorizacion_contratos":"","id_creador":"0","id_empresa":"0"}

                    String _telefono_vo = object.getString("telefono_veterinario");
                    String _cedula_vo = object.getString("cedula_veterinario");
                    String _email_vo = object.getString("email_veterinario");
                    String _imagen_vo = object.getString("imagen_veterinario");


                    if(_nombre_vo.length() > 3)
                        lblNombreVo.setText(_nombre_vo);

                    if(_email_vo.length() > 3)
                        lblEmailVo.setText(_email_vo);

                    if(_telefono_vo.length() > 3)
                        lblCelVo.setText(_telefono_vo);

                    /*
                    if(_cedula_vo.length() > 3)
                        lblCedVo.setText(_cedula_vo);

                        if(txtDireccion_.length() > 3)
                        txtDireccion.setText(txtDireccion_);
                        */






                    /*
                    if(_imagen_vo.length() > 3){
                        String _urlFoto = "http://hyperion.init-code.com/zungu/imagen_establecimiento/" + _imagen_vo;
                        //Picasso.with(fotoVeterinario.getContext()).load(_urlFoto).fit().centerCrop().into(fotoVeterinario);

                        Picasso.with(fotoVeterinario.getContext()).load(_urlFoto)
                                .into(fotoVeterinario, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Bitmap imageBitmap = ((BitmapDrawable) fotoVeterinario.getDrawable()).getBitmap();
                                        RoundedBitmapDrawable circularBitmapDrawable =
                                                RoundedBitmapDrawableFactory.create(fotoVeterinario.getContext().getResources(), imageBitmap);
                                        circularBitmapDrawable.setCircular(true);
                                        fotoVeterinario.setImageDrawable(circularBitmapDrawable);
                                    }
                                    @Override
                                    public void onError() {

                                    }
                                });
                    }
                    Intent i = new Intent(Confirmar_pago.this, Confirmar_pago.class);
                    //i.putExtra("idcliente", _listaIdVeterinarios.get(i));
                    i.putExtra("idcliente", idString);
                    startActivity(i);

                    Intent i = new Intent(Sendf.this, Confirmar_pago.class);

                    Intent i = new Intent(.this, .class);
                    */
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            Log.i("INFO", response);
        }
    }

    private void createDemoFile(ZebraPrinter printer, String fileName) throws IOException {

        FileOutputStream os = this.openFileOutput(fileName, Context.MODE_PRIVATE);

        byte[] configLabel = null;

        PrinterLanguage pl = printer.getPrinterControlLanguage();
        if (pl == PrinterLanguage.ZPL) {
            configLabel = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDLIZ^FS^XZ".getBytes();
        } else if (pl == PrinterLanguage.CPCL) {
            //String cpclConfigLabel = "! 0 200 200 406 1\r\n" + "ON-FEED IGNORE\r\n" + "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 LIZ\r\n" + "PRINT\r\n";
            //String cpclConfigLabel = "! 0 200 200 406 1\r\n" + "ON-FEED IGNORE\r\n" + "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 LIZ\r\n" + "T 0 6 137 177 TE\r\n" + "T 0 6 137 177 AMO\r\n" + "PRINT\r\n";
            //String cpclConfigLabel = "! 0 200 200 406 1\r\n" + "ON-FEED IGNORE\r\n" + "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 LIZ\r\n" + "T 0 6 137 207 TE\r\n" + "T 0 6 137 237 AMO\r\n" + "PRINT\r\n";
            //String cpclConfigLabel = "! 0 200 200 406 1\r\n" + "ON-FEED IGNORE\r\n" + "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 LIZ\r\n" + "T 0 6 137 217 TE\r\n" + "T 0 6 137 257 AMO\r\n" + "PRINT\r\n";
            //String cpclConfigLabel = "! 0 200 200 406 1\r\n" + "ON-FEED IGNORE\r\n" + "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 ¿TU\r\n" + "T 0 6 137 217 ME\r\n" + "T 0 6 137 257 AMAS?\r\n" + "PRINT\r\n";
            //String cpclConfigLabel = "! 0 200 200 406 1\r\n" + "ON-FEED IGNORE\r\n" + "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 ¿TU\r\n" + "T 0 6 137 217 ME\r\n" + "T 0 6 137 257 AMAS?\r\n" + "PRINT\r\n";
            //String cpclConfigLabel = "! 0 200 200 300 1\r\n" + "ON-FEED IGNORE\r\n" + "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 ¿TU\r\n" + "T 0 6 137 217 ME\r\n" + "T 0 6 137 257 AMAS?\r\n" + "PRINT\r\n";
            //NO FUNCIONA
            //String cpclConfigLabel = "! 0 200 200 300 1\r\n" + "CENTER; Print using x and y scales of 10 points SCALE-TEXT PLL_LAT.CSF 10 10 0 10 10 POINT FONT ; Print using x scale of 20 points and y scale; of 10 points SCALE-TEXT PLL_LAT.CSF 20 10 0 80 WIDER FONT; Print using x scale of 10 points and y scale; of 20 points SCALE-TEXT PLL_LAT.CSF 10 20 0 150 TALLER FONT FORM PRINT";
            //NO FUNCIONA
            //String cpclConfigLabel = "! 0 200 200 210 1 CENTER CONCAT 0 20 4 1 0 2/ ST PLL_LAT.CSF 20 20 15 $ ST PLL_LAT.CSF 40 40 0 22 ST PLL_LAT.CSF 20 20 0 99 ENDCONCAT FORM PRINT";

            //String cpclConfigLabel1 = "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 ¿TU\r\n" + "T 0 6 137 217 ME\r\n" + "T 0 6 137 257 AMAS?\r\n" + "PRINT\r\n";
            //String cpclConfigLabel = "! 0 200 200 300 1\r\n" + "ON-FEED IGNORE\r\n" + "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 ¿TU\r\n" + "T 0 6 137 217 ME\r\n" + "T 0 6 137 257 AMAS?\r\n" + "PRINT\r\n" + cpclConfigLabel1;

            //SI FUNCIONA CON RECUADRO
            //String cpclConfigLabel = "! 0 200 200 300 1\r\n" + "ON-FEED IGNORE\r\n" + "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 ¿TU\r\n" + "T 0 6 137 217 ME\r\n" + "T 0 6 137 257 AMAS?\r\n" + "PRINT\r\n";
            //SI FUNCIONA SIN RECUADRO
            //String cpclConfigLabel = "! 0 200 200 300 1\r\n" + "ON-FEED IGNORE\r\n" + "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 ¿TU\r\n" + "T 0 6 137 217 ME\r\n" + "T 0 6 137 257 AMAS?\r\n" + "PRINT\r\n";

            //TEST QR CODE

            //String cpclConfigLabel = "! 0 200 200 300 1\r\n" + "ON-FEED IGNORE\r\n" + "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 ¿TU\r\n" + "T 0 6 137 217 ME\r\n" + "T 0 6 137 257 AMAS?\r\n" + "PRINT\r\n";
            //PRUEBA CODIGO QR
            //ON FEED IGNORE
            //String cpclConfigLabel = "! 0 200 200 300 1\r\n" + "ON-FEED IGNORE\r\n" + "B QR 10 100 M 2 U 10\r\n" + "MA,QR code ABC123\r\n" + "ENDQR\r\n" + "T 4 0 10 400 QR code ABC123\r\n" + "FORM\r\n" + "PRINT\r\n";

            //ON FEED REPRINT
            //String cpclConfigLabel = "! 0 200 200 300 1\r\n" + "ON-FEED REPRINT\r\n" + "B QR 10 100 M 2 U 10\r\n" + "MA,QR code ABC123\r\n" + "ENDQR\r\n" + "T 4 0 10 400 QR code ABC123\r\n" + "FORM\r\n" + "PRINT\r\n";
            //SI FUNCIONA
            //String cpclConfigLabel = "! U1 SETLP 5 2 46\r\n" + "AURORA’S FABRIC SHOP\r\n" + "! U1 SETLP 7 0 24\r\n" + "123 Castle Drive, Kingston, RI  02881\r\n" + "(401) 555-4CUT\r\n" + "FORM\r\n" + "PRINT\r\n";

            //SI FUNCIONA CON DIRECCION
            //String cpclConfigLabel = "! U1 SETLP 5 2 46\r\n" + "THE KRAKEN SOLUTIONS\r\n" + "! U1 SETLP 7 0 24\r\n" + "Av Insurgentes Sur 533 Int 2 Col. Hipodromo Condesa C.P. 06170 Del. Cuauhtemoc\r\n" + "(401) 555-4CUT\r\n" + "FORM\r\n" + "PRINT\r\n";

            //String cpclConfigLabel =  "\r\n" + "\r\n" + "\r\n" + "\r\n" + "\r\n" + "\r\n" + "\r\n" + "\r\n" + "\r\n" + "\r\n" + "\r\n" + "\r\n" + "\r\n" + "! U1 SETLP 5 2 46\r\n" + "THE KRAKEN SOLUTIONS\r\n" + "! U1 SETLP 7 0 24\r\n" + "Av Insurgentes Sur 533 Int 2 Col. Hipodromo Condesa C.P. 06170 Del. Cuauhtemoc\r\n" + "(401) 555-4CUT\r\n" + "FORM\r\n" + "PRINT\r\n";
            //String cpclConfigLabel =  "! U1 JOURNAL\r\n" + "! U1 SETLP 4 0 47\r\n" + "YOURCO RETAIL STORES\r\n" + "! U1 SETLP 7 0 24\r\n" + "14:40 PM      Thursday, 06/04/20\r\n" + "Quantity  Item         Unit    Total\r\n" + "1         Babelfish    $4.20   $4.20\r\n" + "Tax:            5%   $0.21\r\n" + "! U1 SETSP 5\r\n" + "Total:! U1 SETSP 0\r\n" + "$4.41\r\n" + "\r\n" + "\r\n" + "! U1 SETLP 5 2 46\r\n" + "THE KRAKEN SOLUTIONS\r\n" + "! U1 SETLP 7 0 24\r\n" + "Av Insurgentes Sur 533 Int 2 Col. Hipodromo Condesa C.P. 06170 Del. Cuauhtemoc\r\n" + "(401) 555-4CUT\r\n" + "FORM\r\n" + "PRINT\r\n";
            //String cpclConfigLabel =  "! U1 JOURNAL\r\n" + "! U1 SETLP 4 0 47\r\n" + "AGUITECH CORP\r\n" + "! U1 SETLP 7 0 24\r\n" + "14:40 PM      Thursday, 06/04/20\r\n" + "Quantity  Item         Unit    Total\r\n" + "1         Babelfish    $4.20   $4.20\r\n" + "Tax:            5%   $0.21\r\n" + "! U1 SETSP 5\r\n" + "Total:! U1 SETSP 0\r\n" + "$4.41\r\n" + "! U1 SETLP 5 2 46\r\n" + "THE KRAKEN SOLUTIONS\r\n" + "! U1 SETLP 7 0 24\r\n" + "Av Insurgentes Sur 533 Int 2 Col. Hipodromo Condesa C.P. 06170 Del. Cuauhtemoc\r\n" + "(401) 555-4CUT\r\n" + "FORM\r\n" + "PRINT\r\n";


            //_urlGet = "http://thekrakensolutions.com/cobradores/android_get_contrato.php?id_editar=" + idString + "&idv=" + valueID + "&accion=true";







            /*

















            ! 0 200 200 300 1
ON-FEED REPRINT
CENTER
JOURNAL
TEXT 4 1 0 20 PRESS FEED KEY
TEXT 4 1 0 100 TO REPRINT
TEXT 4 1 0 180 THIS TEXT
PRINT
                    */













            /*
            ! 0 200 200 210 1
TEXT 4 0 30 40 Hello World
FORM
PRINT
            * */
            configLabel = cpclConfigLabel.getBytes();
        }
        os.write(configLabel);
        os.flush();
        os.close();

        Intent i = new Intent(SendFileReciboURL.this, Lista_contratos.class);
        startActivity(i);
    }

}
