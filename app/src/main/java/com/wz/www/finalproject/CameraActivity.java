package com.wz.www.finalproject;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;


public class CameraActivity extends AppCompatActivity implements LocationListener {

    private static final int CAMERA_REQUEST = 1888;
    private static final int READ_CONTACTS_PERMISSION = 100;

    ImageView mimageView;
    private Spinner spinner;

    private Criteria criteria;
    private Location location;
    private LocationManager locationManager;
    private String provider;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mimageView = (ImageView) this.findViewById(R.id.image_from_camera);
        Button button = (Button) this.findViewById(R.id.take_image_from_camera);
        ImageButton sendInfoToServer = (ImageButton) findViewById(R.id.sendInfoToServer);
        sendInfoToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Add conection to server and get position or network
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Data values = getInformaction(locationManager.getLastKnownLocation(provider));
                Toast.makeText(getApplicationContext(), "GOOD " + values, Toast.LENGTH_LONG).show();

                BufferedReader in = null;
                try
                {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI("https://cityclear.herokuapp.com/report"));
                    HttpResponse response = client.execute(request);
                    in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    String NL = System.getProperty("line.separator");
                    while ((line = in.readLine()) != null)
                    {
                        sb.append(line + NL);
                    }
                    in.close();
                    return sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } finally
                {
                    if (in != null)
                    {
                        try
                        {
                            in.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }


                Log.d("New Location", "/////////////////////////////");
            }
        });
        spinner = (Spinner) findViewById(R.id.spinner1);
        //spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        setLocationManager();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setLocationManager() {

        int verificarPermisoReadContacts = ContextCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        //Verificamos si el permiso no existe
        if(verificarPermisoReadContacts != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //Si a rechazado el permiso anteriormente muestro un mensaje
                mostrarExplicacion();
            } else {
                //De lo contrario carga la ventana para autorizar el permiso
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, READ_CONTACTS_PERMISSION);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean enabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!enabled) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }

            //Se establese un criterio para selecionar gps o network
            criteria = new Criteria();
            //provider = locationManager.getBestProvider(criteria, false);
            provider = "GPS";
            Toast.makeText(this, "Check your " + provider.toString(), Toast.LENGTH_SHORT).show();
            location = locationManager.getLastKnownLocation(provider);
            if (location == null) {
                Toast.makeText(this, "Check your provider", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("New Location", "lat: " + location.getLatitude());
                Log.d("New Location", "lng: " + location.getLongitude());
            }

            if (location != null) {
                onLocationChanged(location);
            } else {
                Toast.makeText(this, "FALLO", Toast.LENGTH_LONG).show();
            }

        }
    }

    /*
     *  Esta funcion actualiza los valores cuando la localizacion cambia.
     */
    @Override
    public void onLocationChanged(Location location) {
        try {
            Data values = getInformaction(location);
            Toast.makeText(this, "location inside" + "" + values, Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(this, "Error en onlocationChaange", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    private void mostrarExplicacion() {
        new AlertDialog.Builder(this)
                .setTitle("Autorización")
                .setMessage("Necesito permiso para acceder a los contactos de tu dispositivo.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, READ_CONTACTS_PERMISSION);
                        }

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Mensaje acción cancelada
                        mensajeAccionCancelada();
                    }
                })
                .show();
    }

    public void mensajeAccionCancelada(){
        Toast.makeText(getApplicationContext(),
                "Haz rechazado la petición, por favor considere en aceptarla.",
                Toast.LENGTH_SHORT).show();
    }

    /*
     *  Esta funcion obtiene la informaion requerida y la guarda en una clase especial DATA.
     */
    public Data getInformaction(Location location){

        try {
            Double lat = location.getLatitude();
            Double lng = location.getLongitude();
            Double alt = location.getAltitude();
            Float spe = location.getSpeed();
            return new Data(lat.toString(), lng.toString(), alt.toString(), spe.toString());
        }catch (Exception e){
            return new Data();
        }
    }

    public void takeImageFromCamera(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap mphoto = (Bitmap) data.getExtras().get("data");
            mimageView.setImageBitmap(mphoto);
        }
    }
}
