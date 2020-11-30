package com.example.mibotondepanico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import model.Usuario;



public class MainActivity extends AppCompatActivity  {
    private EditText nomP,mensajeP,correoP,nameC1,numC1,nameC2,numC2,nameC3,numC3;
    private Button guardar;
    private Button cerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nomP = findViewById(R.id.txt_nombrePersona);
        mensajeP= findViewById(R.id.txt_mensajePersona);
        correoP= findViewById((R.id.txt_correoPersona));
        nameC1= findViewById((R.id.txt_contacto1));
        numC1= findViewById((R.id.txt_numcontacto1));
        nameC2= findViewById((R.id.txt_contacto2));
        numC2= findViewById((R.id.txt_numcontacto2));
        nameC3= findViewById((R.id.txt_contacto3));
        numC3= findViewById((R.id.txt_numcontacto3));
        guardar=findViewById(R.id.guardar);
        cerrarSesion=findViewById(R.id.cerrarSesion);
        inicializarFirebase();
        obtenerDatos();


        guardar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final  String nombre = nomP.getText().toString();
                final String mensaje = mensajeP.getText().toString();
                final String correo = correoP.getText().toString();
                final String nom1=nameC1.getText().toString();
                final String num1=numC1.getText().toString();
                final String nom2=nameC2.getText().toString();
                final String num2=numC2.getText().toString();
                final String nom3=nameC3.getText().toString();
                final String num3=numC3.getText().toString();
                if(validacion()==false){
                    Toast.makeText( MainActivity.this, "Error Verifique los campos", Toast.LENGTH_SHORT).show();
                }else{

                    Usuario.getmDatabase().child("Users").child(Usuario.getId()).child("name").setValue(nombre);
                    Usuario.getmDatabase().child("Users").child(Usuario.getId()).child("message").setValue(mensaje);
                    Usuario.getmDatabase().child("Users").child(Usuario.getId()).child("email").setValue(correo);
                    Usuario.getmDatabase().child("Users").child(Usuario.getId()).child("contact1Name").setValue(nom1);
                    Usuario.getmDatabase().child("Users").child(Usuario.getId()).child("contact2Name").setValue(nom2);
                    Usuario.getmDatabase().child("Users").child(Usuario.getId()).child("contact3Name").setValue(nom3);
                    Usuario.getmDatabase().child("Users").child(Usuario.getId()).child("contact1Number").setValue(num1);
                    Usuario.getmDatabase().child("Users").child(Usuario.getId()).child("contact2Number").setValue(num2);
                    Usuario.getmDatabase().child("Users").child(Usuario.getId()).child("contact3Number").setValue(num3);
                    Toast.makeText( MainActivity.this, "Agregado", Toast.LENGTH_SHORT).show();
                }

            }
        });


        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarLogin();
            }
        });
    }

    private void obtenerDatos() {
        Usuario.getmDatabase().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    String id= objSnaptshot.getKey();
                    if(id==Usuario.getId()){
                        Usuario p = objSnaptshot.getValue(Usuario.class);
                        nomP.setText(p.getName());
                        mensajeP.setText(p.getMessage());
                        correoP.setText(p.getEmail());
                        nameC1.setText(p.getContact1Name());
                        numC1.setText(p.getContact1Number());
                        nameC2.setText(p.getContact2Name());
                        numC2.setText(p.getContact2Number());
                        nameC3.setText(p.getContact3Name());
                        numC3.setText(p.getContact3Number());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarFirebase() {
        //FirebaseApp.initializeApp(this);
        new Usuario();
    }



    private boolean validacion() {
        boolean res=true;
        String nombre = nomP.getText().toString();
        String mensaje = mensajeP.getText().toString();
        String correo = correoP.getText().toString();
        String nomb1=nameC1.getText().toString();
        String nume1=numC1.getText().toString();
        String nomb2=nameC2.getText().toString();
        String nume2=numC2.getText().toString();
        String nomb3=nameC3.getText().toString();
        String nume3=numC3.getText().toString();

        if (nombre.equals("")) {
            nomP.setError("Required");
            res=false;
        } else if (mensaje.equals("")){
            mensajeP.setError("Required");
            res=false;
        } else if (correo.equals("")){
            correoP.setError("Required");
            res=false;
        }else if (nume1.equals("")){
            numC1.setError("Required");
            res=false;
        }else if (nomb1.equals("")){
            nameC1.setError("Required");
            res=false;
        }else if (nume2.equals("")){
            numC2.setError("Required");
            res=false;
        }else if (nomb2.equals("")){
            nameC2.setError("Required");
            res=false;
        }else if (nume3.equals("")){
            numC3.setError("Required");
            res=false;
        }else if (nomb3.equals("")){
            nameC3.setError("Required");
            res=false;
        }else if (!isNumeric(nume1)){
            numC1.setError("Debe ser un valor numérico");
            res=false;
        }else if (!isNumeric(nume2)){
            numC2.setError("Debe ser un valor numérico");
            res=false;
        }else if (!isNumeric(nume3)){
            numC3.setError("Debe ser un valor numérico");
            res=false;
        }else if (isNumeric(nomb1)){
            nameC1.setError("No se aceptan valores numéricos");
            res=false;
        }else if (isNumeric(nomb2)){
            nameC2.setError("No se aceptan valores numéricos");
            res=false;
        }else if (isNumeric(nomb3)){
            nameC3.setError("No se aceptan valores numéricos");
            res=false;
        }
        return res;
    }
    public static boolean isNumeric(String cadena) {
        boolean resultado;
        try {
            Integer.parseInt(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }

        return resultado;
    }

    private void borrarLogin(){
        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", MainActivity.this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("autoLoginUsuario", "");
        editor.putString("autoLoginPass", "");
        editor.commit();
        Toast.makeText( MainActivity.this, "AutoLogin desactivado", Toast.LENGTH_SHORT).show();
    }
}