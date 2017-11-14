package com.ilma.testing.ilmaapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.facebook.login.Login;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.ilma.testing.ilmaapp.Models.User;
import com.ilma.testing.ilmaapp.R;
import com.ilma.testing.ilmaapp.data.LocalStorage;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends Activity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;

    @InjectView(R.id.progress_bar)
    ProgressBar progressBar;
    private ProfileTracker mProfileTracker;

    @InjectView(R.id.image_view)
    ImageView imageView;

    @InjectView(R.id.admin_name)
    EditText admin_name;

    @InjectView(R.id.admin_pass)
    EditText admin_pass;

    @InjectView(R.id.login_admin)
    Button admin_login;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static String CONSUMER_KEY = "yIFbUkw1A5h2cf30fc1mu5P8l";
    public static String CONSUMER_SECRET = "lQMXgraB16prZ6L9fG9joiODGNVwF694cXpU7UN6nRFiApnNad";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //GenerateKeyHashes();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        /*TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(CONSUMER_KEY, CONSUMER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);*/



        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        loginButton =  (LoginButton) findViewById(R.id.facebook_btn);

        //SetUpVideo();
        //Glide.with(this).load(R.drawable.ilmalogo).into(imageView);
        FacebookSignIn();

        AdminLogin();

    }

    private void AdminLogin() {
        admin_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmpty()){
                    return;
                }

                if(admin_name.getText().toString().equalsIgnoreCase("Admin") &&
                        admin_pass.getText().toString().equalsIgnoreCase("Admin")){
                    Intent i = new Intent(LoginActivity.this,AdminActivity.class);
                    i.putExtra("role", "admin");
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }
        });


    }

    private boolean isEmpty() {
        if (admin_name.getText().length() > 0 && admin_pass.getText().length() > 0) {
            return false;
        }

        return true;
    }

    private void GenerateKeyHashes() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.testing.friendsappdemo",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {

        }
    }



    private void SetUpVideo() {
        try {
            /*VideoView videoHolder = findViewById(R.id.);
            Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.cds);
            videoHolder.setVideoURI(video);

            videoHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    // jump();
                }
            });
            videoHolder.start();
            videoHolder.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setVolume(0, 0);
                    mediaPlayer.seekTo(10000);
                    mediaPlayer.setLooping(true);
                }
            });*/
        } catch (Exception ex) {

        }
    }

    private void FacebookSignIn() {
        boolean isLogedIn = getFaceBookStatus();
        if(isLogedIn) return;
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("user_work_history", "user_education_history");
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressBar.setVisibility(View.GONE);

                if(Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            // profile2 is the new profile
                            Log.v("facebook - profile", profile2.getFirstName());
                            mProfileTracker.stopTracking();
                        }
                    };
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                }
                getUserInfoFromResult(loginResult);

                //LoginManager.getInstance().logOut();
            }

            @Override
            public void onCancel() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(FacebookException error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getUserInfoFromResult(LoginResult loginResult){

        User user = null;
        final GraphRequest request=GraphRequest.newMeRequest(loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try{
                    LocalStorage.setDefaults("FbJson",response.getJSONObject().toString(),
                            getApplicationContext());

                    JSONObject jsonObject = response.getJSONObject();
                    String name = jsonObject.getString("name");
                    String email = jsonObject.getString("email");
                    String gender = jsonObject.getString("gender");
                    String UserID = jsonObject.getString("id");
                    AddUserMaster(new User(name, email, gender, "", UserID));

                    progressBar.setVisibility(View.GONE);

                    /*JSONObject userObject=response.getJSONObject();

                    JSONArray education=userObject.getJSONArray("education");
                    JSONArray work=userObject.getJSONArray("work");*/
                }
                catch (Exception e){

                }
            }
        });

        Bundle parameters=new Bundle();
        parameters.putString("fields","id,name,email,gender, age_range,link,picture,work,education");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void AddUserMaster(final User user) {
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {



                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://tippiest-reactors.000webhostapp.com/PKT/addUserMaster.php?FullName=" +
                                user.getFullname() + "&Email=" + user.getEmail() + "&Gender="
                                +user.getGender() + "&Birthday=b&UserID=" + user.getUserID())
                        .build();

                try{
                    Response response = client.newCall(request).execute();
                    Log.d("addeduser",response.body().string());

                    Intent i = new Intent(LoginActivity.this,HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);

                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

    }

    public void verifyCalenderPermissions(Activity activity) {
        // Check if we have write permission
        int permissionCheck = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );


        }else {
            loginButton.performClick();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_EXTERNAL_STORAGE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                loginButton.performClick();
            }
        }
    }

    private boolean getFaceBookStatus() {
        if(isFacebookLoggedIn()){
            Intent i = new Intent(LoginActivity.this,HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

            return true;
        }

        return false;
    }

    public boolean isFacebookLoggedIn(){
        return AccessToken.getCurrentAccessToken() != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void CustomLoginClick(View view){
        progressBar.setVisibility(View.VISIBLE);
        verifyCalenderPermissions(this);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //jump();
        return true;
    }


}
