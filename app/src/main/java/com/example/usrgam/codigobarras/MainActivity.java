package com.example.usrgam.codigobarras;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    private static final int REQUESTCAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camID= Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // verificar permiso

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // control de permisos
            if (verificarPermisos()) {
                //mensaje
                Toast.makeText(getApplicationContext(), "permiso otorgado", Toast.LENGTH_LONG).show();
            } else {
                solicitarpermisos();
            }
        }
    }

    private void solicitarpermisos() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},REQUESTCAMERA);

    }

    private boolean verificarPermisos() {
        return ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;

    }

    @Override
    protected void onResume() {
        super.onResume();
        int apiVersion= Build.VERSION.SDK_INT;
        if (apiVersion>= Build.VERSION_CODES.M){
            if(verificarPermisos()){
                if(scannerView==null){
                    scannerView=new ZXingScannerView(this);
                    setContentView(scannerView);

                }
                //para que arranque la camara
                scannerView.setResultHandler(this);
                scannerView.startCamera();

            }else {
                solicitarpermisos();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUESTCAMERA:
                if(grantResults.length>0){
                    boolean aceptaPermiso = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(aceptaPermiso){
                        //mensaje
                    }else{
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                                requestPermissions(new String[]{Manifest.permission.CAMERA},REQUESTCAMERA);

                            }

                        }
                    }
                }
        }
    }

    @Override
    public void handleResult(Result result) {
       // AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        if(result.getText() != null) {
          //  AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            // alertDialog.setMessage("mensaje cualquiera");
           // registrarAsistencia(result.getText()+"");
          Intent intents = new Intent(getApplicationContext(),Registro.class);
            intents.putExtra("loging", result.getText()+"");
            startActivity(intents);

            //alertDialog.setMessage(result.getText());
            //alertDialog.show();
            //alertDialog.setMessage(result.getBarcodeFormat().toString());
            Log.e("resultadoBAR:", result.getBarcodeFormat().toString());


        }
        onResume();
    }

    public void registrarAsistencia(final String asistencia){
      //  final  AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                     AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogRed);
                        alertDialog.setMessage("Usuario Repetido");
                      //  alertDialog.setIcon(R.drawable.ic_launcher_background);
                        alertDialog.show();
                        Toast toast = Toast.makeText(MainActivity.this, "Usuario Repetido", Toast.LENGTH_SHORT);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        v.setTextColor(Color.RED);
                        toast.show();

                    } else {
                       registro(asistencia);
                      // alertDialog.setMessage("REgistrando...");
                       AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogGreen);
                       alertDialog.setMessage("Te has registrado correctamente :)");
                        alertDialog.show();
                        Toast toast = Toast.makeText(MainActivity.this,"Te has registrado correctamente :)",Toast.LENGTH_LONG);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        v.setTextColor(Color.GREEN);
                        toast.show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        AsistenciaRequest asistenciaRequest = new AsistenciaRequest(asistencia, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(asistenciaRequest);

    }

    public void registro(String asistencia){
        final  AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {

                    /*   alertDialog.setMessage("Te has registrado correctamente :)");
                        alertDialog.show();
                        Toast toast = Toast.makeText(MainActivity.this,"Te has registrado correctamente :)",Toast.LENGTH_LONG);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        v.setTextColor(Color.GREEN);
                        toast.show();*/

                    } else {

                    /*    alertDialog.setMessage("Error con el registro");
                        alertDialog.show();
                        Toast toast = Toast.makeText(MainActivity.this,"Error con el registro",Toast.LENGTH_LONG);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        v.setTextColor(Color.GREEN);
                        toast.show();*/

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        RegistroRequest registroRequest = new RegistroRequest(asistencia, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(registroRequest);

    }
}