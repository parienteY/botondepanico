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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import static android.widget.Toast.LENGTH_SHORT;
import model.Usuario;

public class Login extends AppCompatActivity {
    private Button btnCrearCuenta, iniciar_sesion;
    private TextView correo, contrasena;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        new Usuario();
        btnCrearCuenta = findViewById(R.id.btn_ir_crearCuenta);
        iniciar_sesion = findViewById(R.id.iniciar_sesion);
        correo = findViewById(R.id.et_correo);
        contrasena = findViewById(R.id.et_contrasena);

        autoLogin();
        
        btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, SignUp.class));
            }
        });

        iniciar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInControl(correo.getText().toString(), contrasena.getText().toString());
            }
        });
    }

    private void logInControl(String usuario, String contrasenia){
        if(!usuario.equals("") && !contrasenia.equals("")){
            if(correo.length() >= 6){
                userlogIn(usuario, contrasenia);
            }else{
                Toast.makeText(this, "Contraseña incorrecta", LENGTH_SHORT ).show();
            }
        }else{
            Toast.makeText(this, "Llene todos los campos", LENGTH_SHORT).show();

        }
    }

    private void userlogIn(final String usuario, final String contrasenia) {

        Usuario.getmAuth().signInWithEmailAndPassword(usuario, contrasenia).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(Login.this, "Ingreso exitoso", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = Usuario.getmAuth().getCurrentUser();

                    guardarLogin(usuario, contrasenia);
                    Toast.makeText(com.example.mibotondepanico.Login.this, "Bienvenido!!!", Toast.LENGTH_SHORT).show();
                    actualizarEstadoUsuario(Usuario.getmAuth().getCurrentUser().getUid());

                    startActivity(new Intent(Login.this, MapsActivity.class));
                    finish();
                } else {
                    Toast.makeText(Login.this, "Usuario o contraseña incorrecto", LENGTH_SHORT).show();
                }

            }
        });

    }

    private void actualizarEstadoUsuario(String id){
        new Usuario(id);
        Usuario.getmDatabase().child("Users").child(Usuario.getId()).child("connectedState").setValue(true);
    }

    private void autoLogin(){//LEER archivo
        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", Login.this.MODE_PRIVATE);
        String usuario = sharedPreferences.getString("autoLoginUsuario", "");
        String contrasenia = sharedPreferences.getString("autoLoginPass", "");
        if(!usuario.equals("") && !contrasenia.equals("")){
            userlogIn(usuario, contrasenia);
            Toast.makeText(this, "Autologin", LENGTH_SHORT ).show();
        }
    }
    private void guardarLogin(String usuario, String contrasenia){//GUARDAR archivo
        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", Login.this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("autoLoginUsuario", usuario);
        editor.putString("autoLoginPass", contrasenia);
        editor.commit();
    }

}