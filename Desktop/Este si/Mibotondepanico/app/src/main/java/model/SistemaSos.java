package model;

import android.content.Context;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
public class SistemaSos {


    public void llamarAtodos(String direccion,double latitud,double longitud, Context context) {
        RequestQueue myrequest = Volley.newRequestQueue(context);
        JSONObject json = new JSONObject();
        try {

            json.put("to","/topics/"+"enviaratodos");
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo","SOS");
            notificacion.put("detalle","Ayudame estoy en peligro!. Mi ubicacion es "+direccion);
            Log.e("tag","estoy en"+latitud+" "+longitud);
            json.put("data",notificacion);

            String URL= "https://fcm.googleapis.com/fcm/send";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL,json,null,null){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> header= new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAYb6xLqw:APA91bFndtTwbayFrGEvnxlsc6U4dJqkNV_Mhq5PES5HJfyBwGFlW2ayRRIbOWYBIfAtewVNISN_B9ocKeNSga-sdN4I2BvQ7ZiVFvjc-3vQUCfhUscv1jzD_fGqNOGIRYXwi97XYDbh");

                    return header;
                }
            };
            myrequest.add(request);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void contadorDeTiempo(final LottieAnimationView animationView){
        new CountDownTimer(600000,1000){
            public void onTick(long millisUntilFinished){

            }
            public void onFinish(){
                Usuario.getmDatabase().child("Users").child(Usuario.getId()).child("helpState").setValue(false);
                animationView.setClickable(true);
            }
        }.start();
    }
    public void enviarMensaje(String numero, String mensaje, Context context){
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(numero,null,mensaje,null,null);
            Toast.makeText(context, "Mensaje Enviado.", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(context, "SMS no enviado, datos incorrectos.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
