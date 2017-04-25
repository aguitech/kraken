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
import android.os.Bundle;
import android.os.Looper;

import com.thekrakensolutions.gestioncobranza.ConnectionScreen;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SendFileRecibo extends ConnectionScreen {

    private UIHelper helper = new UIHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testButton.setText("Send Test File");
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
            //String cpclConfigLabel =  "! U1 JOURNAL\r\n" + "! U1 SETLP 4 0 47\r\n" + "YOURCO RETAIL STORES\r\n" + "! U1 SETLP 7 0 24\r\n" + "14:40 PM      Thursday, 06/04/20\r\n" + "Quantity  Item         Unit    Total\r\n" + "1         Babelfish    $4.20   $4.20\r\n" + "Tax:            5%   $0.21\r\n" + "! U1 SETSP 5\r\n" + "Total:! U1 SETSP 0\r\n" + "$4.41\r\n" + "! U1 SETLP 5 2 46\r\n" + "THE KRAKEN SOLUTIONS\r\n" + "! U1 SETLP 7 0 24\r\n" + "Av Insurgentes Sur 533 Int 2 Col. Hipodromo Condesa C.P. 06170 Del. Cuauhtemoc\r\n" + "(401) 555-4CUT\r\n" + "FORM\r\n" + "PRINT\r\n";

            String cpclConfigLabel =  "! U1 JOURNAL\r\n" + "! U1 SETLP 4 0 47\r\n" + "YOURCO RETAIL STORES\r\n" + "! U1 SETLP 7 0 24\r\n" + "14:40 PM      Thursday, 06/04/20\r\n" + "Quantity  Item         Unit    Total\r\n" + "1         Babelfish    $4.20   $4.20\r\n" + "Tax:            5%   $0.21\r\n" + "! U1 SETSP 5\r\n" + "Total:! U1 SETSP 0\r\n" + "$4.41\r\n" + "! U1 SETLP 5 2 46\r\n" + "THE KRAKEN SOLUTIONS\r\n" + "! U1 SETLP 7 0 24\r\n" + "Av Insurgentes Sur 533 Int 2 Col. Hipodromo Condesa C.P. 06170 Del. Cuauhtemoc\r\n" + "(401) 555-4CUT\r\n" + "FORM\r\n" + "PRINT\r\n";






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
    }

}
