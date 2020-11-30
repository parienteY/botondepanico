package com.example.mibotondepanico;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import com.example.mibotondepanico.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import model.Usuario;

public class SignUp extends AppCompatActivity {

    private Button btn_ir_inicioSesion, crear_cuenta;
    private TextView nombreRegistro, correoRegistro, contrasenaRegistro, confimarContrasena;
    private TextView nombreContacto1, numeroContacto1, nombreContacto2, numeroContacto2, nombreContacto3, numeroContacto3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nombreRegistro = findViewById(R.id.et_nombre);
        correoRegistro = findViewById(R.id.et_correo);
        contrasenaRegistro = findViewById(R.id.et_contrasena);
        confimarContrasena = findViewById(R.id.et_Confimarcontrasena);
        nombreContacto1 = findViewById(R.id.et_nombre_contacto1);
        numeroContacto1 = findViewById(R.id.et_numero_contacto1);
        nombreContacto2 = findViewById(R.id.et_nombre_contacto2);
        numeroContacto2 = findViewById(R.id.et_numero_contacto2);
        nombreContacto3 = findViewById(R.id.et_nombre_contacto3);
        numeroContacto3 = findViewById(R.id.et_numero_contacto3);


        btn_ir_inicioSesion = findViewById(R.id.btn_ir_inicioSesion);
        crear_cuenta = findViewById(R.id.crear_cuenta);


        btn_ir_inicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();//Va a Inicio de Sesión
            }
        });

        crear_cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerControl();
            }
        });
    }

    private void registerControl(){
        if(!nombreRegistro.getText().toString().isEmpty() && !correoRegistro.getText().toString().isEmpty() && !contrasenaRegistro.getText().toString().isEmpty()
                && !confimarContrasena.getText().toString().isEmpty() && !nombreContacto1.getText().toString().isEmpty() && !numeroContacto1.getText().toString().isEmpty()
                && !nombreContacto2.getText().toString().isEmpty() && !numeroContacto2.getText().toString().isEmpty() && !nombreContacto3.getText().toString().isEmpty() && !numeroContacto3.getText().toString().isEmpty()){
            if(contrasenaRegistro.getText().toString().length() >= 6){
                    userRegister();
            }else{
                Toast.makeText(SignUp.this, "Contraseña debe tener 6 o más caracteres", Toast.LENGTH_SHORT ).show();
            }
        }else{
            Toast.makeText(SignUp.this, "Llene todos los campos", Toast.LENGTH_SHORT).show();

        }
    }

    private void userRegister(){
        if(contrasenaRegistro.getText().toString().equals(confimarContrasena.getText().toString())) {
            Usuario.getmAuth().createUserWithEmailAndPassword(correoRegistro.getText().toString(), contrasenaRegistro.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", nombreRegistro.getText().toString());
                        map.put("email", correoRegistro.getText().toString());
                        map.put("password", contrasenaRegistro.getText().toString());
                        map.put("contact1Name", nombreContacto1.getText().toString());
                        map.put("contact2Name", nombreContacto2.getText().toString());
                        map.put("contact3Name", nombreContacto3.getText().toString());
                        map.put("contact1Number", numeroContacto1.getText().toString());
                        map.put("contact2Number", numeroContacto2.getText().toString());
                        map.put("contact3Number", numeroContacto3.getText().toString());
                        map.put("message", "Ayuda estoy en peligro");
                        map.put("lat", 0);
                        map.put("lon", 0);
                        map.put("connectedState", false);
                        map.put("helpState", false);

                        String id = Usuario.getmAuth().getCurrentUser().getUid();
                        Usuario.getmDatabase().child("Users").child(id).setValue(map);
                        actualizarEstadoUsuario(id);
                        guardarRegistro(correoRegistro.getText().toString(), contrasenaRegistro.getText().toString());
                        Toast.makeText(SignUp.this, "Cuenta creada, por favor espere", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUp.this, MapsActivity.class));
                        finish();

                    } else {
                        Toast.makeText(SignUp.this, "No puede usar ese correo", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(SignUp.this, "Ambas contraseñas deben ser iguales", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarEstadoUsuario(String id){
        new Usuario(id);
        Usuario.getmDatabase().child("Users").child(Usuario.getId()).child("connectedState").setValue(true);
    }

    private void guardarRegistro(String usuario, String contrasenia){
        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", SignUp.this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("autoLoginUsuario", usuario);
        editor.putString("autoLoginPass", contrasenia);
        editor.commit();
    }
}