package com.ilma.testing.ilmaapp.GCM;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bbb on 11/12/2017.
 */

public class GcmRegistrationIntentService extends IntentService {

    public static final String registration_success = "success";
    public static final String registrationerror = "error";

    public GcmRegistrationIntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GcmRegisteration();
    }

    public void GcmRegisteration(){
        Intent registerComplet = null;
        String token = null;
        try {
            InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
            token = instanceID.getToken("116749129826", GoogleCloudMessaging.INSTANCE_ID_SCOPE,null);
            Log.d("Token",token);
            registerToken(token);
            registerComplet = new Intent(registration_success);
            registerComplet.putExtra("token",token);
        }catch (Exception e){
            Log.d("Error","RegistrationError");
            registerComplet = new Intent(registrationerror);
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(registerComplet);
    }

    private void registerToken(String token) {
        OkHttpClient client = new OkHttpClient();
       /* RequestBody requestbody = new FormBody.Builder()
                .add("token",token)
                .add("Name","")
                .build();*/
        //Log.d("UserID",utils.getdata("Userid"));

        Request request = new Request.Builder()
                .url("https://tippiest-reactors.000webhostapp.com/PKT/addToken.php?AdminToken=" +
                         token)
                .build();

        try{
            Response response = client.newCall(request).execute();
            Log.d("token_send",response.body().string());

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
