package com.example.mibotondepanico;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import model.SistemaSos;
import model.Usuario;

public class AlertActivity extends AppCompatActivity {
    private Button sos;
    private Button cancel;

    private double latitud;
    private double longitud;
    private String direccion;
    private String contacto1;
    private String contacto2;
    private String contacto3;
    private String mensaje;
    private SistemaSos sistemaSos=new SistemaSos();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        sos = findViewById(R.id.button);
        cancel = findViewById(R.id.btnNo);

        cuentaRegresivaParaSenial();
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hacerSeñalSOS();
            }
        });

    }

    private void cuentaRegresivaParaSenial() {
        final TextView textView = findViewById(R.id.counter);
        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                long segPend = millisUntilFinished / 1000;
                textView.setText((int) segPend + "");

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancel();
                        onBackPressed();
                    }
                });
            }
            public void onFinish() {
                textView.setText("0");
                startActivity(new Intent(AlertActivity.this, MapsActivity.class));
                hacerSeñalSOS();
            }
        }.start();
    }
    private void hacerSeñalSOS(){
        recibirDatos();
        sistemaSos.enviarMensaje(contacto1,mensaje+" "+direccion,getApplicationContext());
        sistemaSos.enviarMensaje(contacto2,mensaje+" "+direccion,getApplicationContext());
        sistemaSos.enviarMensaje(contacto3,mensaje+" "+direccion,getApplicationContext());
        Usuario.getmDatabase().child("Users").child(Usuario.getId()).child("helpState").setValue(true);
        sistemaSos.llamarAtodos(direccion,latitud,longitud,getApplicationContext());
        onBackPressed();
        Toast.makeText(getApplicationContext(), "Señal SOS enviada", Toast.LENGTH_LONG).show();
    }

    private void recibirDatos(){
        Bundle extras = getIntent().getExtras();
        latitud = extras.getDouble("Dlatitud");
        longitud = extras.getDouble("Dlongitud");
        direccion = extras.getString("Ddireccion");
        contacto1 = extras.getString("Dcontacto1");
        contacto2 = extras.getString("Dcontacto2");
        contacto3 = extras.getString("Dcontacto3");
        mensaje = extras.getString("Dmensaje");
    }





}