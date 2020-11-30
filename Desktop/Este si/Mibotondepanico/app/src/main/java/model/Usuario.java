package model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Usuario {
    private static String id;
    private static FirebaseAuth mAuth;
    private static DatabaseReference mDatabase;
    private double lat;
    private double lon;

    private String email;
    private String uid;
    private String name;
    private String password;
    private String message;
    private boolean connectedState;
    private boolean helpState;
    private String contact1Number;
    private String contact1Name;
    private String contact2Number;
    private String contact2Name;
    private String contact3Number;
    private String contact3Name;


    public Usuario(){
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    public Usuario(String id){
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static String getId() {
        return id;
    }
    public static FirebaseAuth getmAuth(){
        return mAuth;
    }
    public static DatabaseReference getmDatabase(){
        return mDatabase;
    }

    public String getCelular() {
        return Celular;
    }

    public void setCelular(String celular) {
        Celular = celular;
    }

    private String Celular;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContact1Number() {
        return contact1Number;
    }

    public void setContact1Number(String contact1Number) {
        this.contact1Number = contact1Number;
    }

    public String getContact1Name() {
        return contact1Name;
    }

    public void setContact1Name(String contact1Name) {
        this.contact1Name = contact1Name;
    }

    public String getContact2Number() {
        return contact2Number;
    }

    public void setContact2Number(String contact2Number) {
        this.contact2Number = contact2Number;
    }

    public String getContact2Name() {
        return contact2Name;
    }

    public void setContact2Name(String contact2Name) {
        this.contact2Name = contact2Name;
    }

    public String getContact3Number() {
        return contact3Number;
    }

    public void setContact3Number(String contact3Number) {
        this.contact3Number = contact3Number;
    }

    public String getContact3Name() {
        return contact3Name;
    }

    public void setContact3Name(String contact3Name) {
        this.contact3Name = contact3Name;
    }


    public boolean isConnectedState() {
        return connectedState;
    }

    public void setConnectedState(boolean connectedState) {
        this.connectedState = connectedState;
    }

    public boolean isHelpState() {
        return helpState;
    }

    public void setHelpState(boolean helpState) {
        this.helpState = helpState;
    }


    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }




}
