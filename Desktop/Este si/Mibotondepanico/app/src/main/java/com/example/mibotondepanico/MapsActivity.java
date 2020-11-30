package com.example.mibotondepanico;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import model.SistemaSos;
import model.Usuario;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private Button configuracion;
    private ArrayList<Marker> tmpRealTimeMarker = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();
    private GoogleMap mMap;String contacto1;
    private String contacto2;
    private String contacto3;
    private String mensaje;
    private double latitud,longitud;
    private String direccion;

    private int contadorSacudida = 0;
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;
    SistemaSos sistemaSos=new SistemaSos();
    LottieAnimationView animationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        configuracion=findViewById(R.id.config);
        animationView= (LottieAnimationView) findViewById(R.id.sos);
        if(ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED&& ActivityCompat.checkSelfPermission(
                MapsActivity.this,Manifest
                        .permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MapsActivity.this,new String[]
                    { Manifest.permission.SEND_SMS,},1000);
        }else{
        };
        new Usuario();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }
        animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationView.playAnimation();
                sistemaSos.enviarMensaje(contacto1,mensaje+" "+direccion,getApplicationContext());
                sistemaSos.enviarMensaje(contacto2,mensaje+" "+direccion,getApplicationContext());
                sistemaSos.enviarMensaje(contacto3,mensaje+" "+direccion,getApplicationContext());
                Usuario.getmDatabase().child("Users").child(Usuario.getId()).child("helpState").setValue(true); // ACA FALTA OBTENER EL ID DEL USUARIO ACTUAL
                sistemaSos.llamarAtodos(direccion,latitud,longitud,getApplicationContext());
                sistemaSos.contadorDeTiempo(animationView);
                animationView.setClickable(false);
            }
        });
        configuracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        FirebaseMessaging.getInstance().subscribeToTopic("enviaratodos").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MapsActivity.this,"Registrado para recibir notificaciones",Toast.LENGTH_SHORT).show();
            }
        });

        iniciarSensor();

    }

    public void iniciarSensor(){
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            if(sensor==null){
                Toast.makeText(MapsActivity.this,"Imposible iniciar SOS automático",Toast.LENGTH_SHORT).show();
            }
        sensorEventListener = new SensorEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                iniciarRegistroSensor();

                if(((x>=10 || x<=-10) || (y>=10 || y<=-10) || (z>=10 || z<=-10)) && contadorSacudida==0){
                    contadorSacudida++;
                }else if((x<=1 || y<=1 || z<=1) && contadorSacudida==1){
                    contadorSacudida++;
                }

                if(contadorSacudida==2){
                    pararRegistroSensor();
                    contadorSacudida=0;
                    Intent i = new Intent(MapsActivity.this, AlertActivity.class);
                    i.putExtra("Dlatitud",latitud);
                    i.putExtra("Dlongitud",longitud);
                    i.putExtra("Ddireccion",direccion);
                    i.putExtra("Dcontacto1",contacto1);
                    i.putExtra("Dcontacto2",contacto2);
                    i.putExtra("Dcontacto3",contacto3);
                    i.putExtra("Dmensaje",mensaje);
                    startActivity(i);

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        iniciarRegistroSensor();
    }
    private void iniciarRegistroSensor(){
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    private void pararRegistroSensor(){
        sensorManager.unregisterListener(sensorEventListener);
    }
    @Override
    protected void onPause() {
        pararRegistroSensor();
        super.onPause();
    }
    @Override
    protected void onResume() {
        iniciarRegistroSensor();
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        updateCameraSos();
        Usuario.getmDatabase().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//METODO DONDE OCURRE TODA LA OBTENCION DE LOS DATOS
                for(Marker marker:realTimeMarkers){
                    marker.remove();
                }
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){//RECORRE TODOS LOS HIJOS DE UBICACION Y VA RECUPERANDO SUS HIJOS
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    boolean ayuda = usuario.isHelpState();
                    boolean activo = usuario.isConnectedState();
                    String id= dataSnapshot.getKey();;
                    if(id==Usuario.getId()){
                        contacto1=usuario.getContact1Number();
                        contacto2=usuario.getContact2Number();
                        contacto3=usuario.getContact3Number();
                        mensaje=usuario.getMessage();
                    }
                    if(ayuda==true && activo==true){
                        double latitud = usuario.getLat();
                        double longitud = usuario.getLon();
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(latitud,longitud));
                        markerOptions.title(usuario.getName());
                        tmpRealTimeMarker.add(mMap.addMarker(markerOptions));
                    }
                }
                realTimeMarkers.clear();
                realTimeMarkers.addAll(tmpRealTimeMarker);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);

    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {//PERMISOS
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }

    }
    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    direccion = DirCalle.getAddressLine(0);
                    //direccion.setText(DirCalle.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        MapsActivity mainActivity;
        LatLng miUbicacion;
        public MapsActivity getMainActivity() {
            return mainActivity;
        }
        public void setMainActivity(MapsActivity mainActivity) {
            this.mainActivity = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc) {
            miUbicacion = new LatLng(loc.getLatitude(), loc.getLongitude());
            longitud=loc.getLongitude();
            latitud=loc.getLatitude();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(miUbicacion)
                    .zoom(20)
                    .bearing(0)
                    .tilt(45)
                    .build();
            updatePosition(miUbicacion.latitude, miUbicacion.longitude);
            setLocation(loc);
        }
        @Override
        public void onProviderDisabled(String provider) {

        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            // latitud.setText("GPS Activado");
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }

    }

    private void updatePosition(double latitude,  double longitude){
        Usuario.getmDatabase().child("Users").child(Usuario.getId()).child("lat").setValue(latitude);
        Usuario.getmDatabase().child("Users").child(Usuario.getId()).child("lon").setValue(longitude);
    }
    public void updateCameraSos(){

        LatLng sosubicacion = new LatLng(-17.414358382589857, -66.1666251882861);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(sosubicacion)
                .zoom(10)
                .bearing(0)
                .tilt(45)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        Usuario.getmDatabase().child("Users").child(Usuario.getId()).child("helpState").setValue(false);
        Usuario.getmDatabase().child("Users").child(Usuario.getId()).child("connectedState").setValue(false);
        super.onDestroy();
    }
}