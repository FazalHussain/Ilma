package com.ilma.testing.ilmaapp.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.ilma.testing.ilmaapp.GCM.GcmRegistrationIntentService;
import com.ilma.testing.ilmaapp.R;
import com.ilma.testing.ilmaapp.fragments.EmployeeList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BroadcastReceiver broadcastreciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        isAdmin();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void replaceFragments(Fragment mfc) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction FT = fragmentManager.beginTransaction();
        Bundle b = new Bundle();
        //b.putString("list", json);
        //mfc.setArguments(b);
        FT.replace(R.id.content_admin, mfc);
        FT.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id==R.id.nav_employeelist){
            EmployeeList mfc = EmployeeList.newInstance();
            replaceFragments(mfc);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void isAdmin() {
        if(getIntent().getStringExtra("role")!=null &&
                getIntent().getStringExtra("role").equalsIgnoreCase("admin")){

            if(getIntent().getStringExtra("status")!= null &&
                    getIntent().getStringExtra("status").equalsIgnoreCase("popup")){
                new AlertDialog.Builder(AdminActivity.this)
                        .setTitle("Leave Request")
                        .setMessage(getIntent().getStringExtra("msg"))
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }

            broadcastreciever = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if(intent.getAction().endsWith(GcmRegistrationIntentService.registration_success)){
                        String token = intent.getStringExtra("token");
                    }
                }
            };

            int resultcode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
            if(ConnectionResult.SUCCESS!=resultcode){
                if(GooglePlayServicesUtil.isUserRecoverableError(resultcode)){
                    Log.d("GooglePlayError","Googel Play Service is not avaiable in this device!!!");
                }
            }else {
                Intent intent = new Intent(this, GcmRegistrationIntentService.class);
                startService(intent);
            }
        }
    }

    private void sendRequest() {

        final String msg = getIntent().getStringExtra("msg");
        final String start_date = getIntent().getStringExtra("start_date");
        final String end_date = getIntent().getStringExtra("end_date");
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
       /* RequestBody requestbody = new FormBody.Builder()
                .add("token",token)
                .add("Name","")
                .build();*/
                //Log.d("UserID",utils.getdata("Userid"));
                Request request = new Request.Builder()
                        .url("https://tippiest-reactors.000webhostapp.com/PKT/addAdminAction.php?" +
                                "LeaveReason=" + msg + "&title=Leave Request")
                        .build();

                try{
                    Response response = client.newCall(request).execute();
                    Log.d("token_send",response.body().string());

                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent().getStringExtra("role")!=null &&
                getIntent().getStringExtra("role").equalsIgnoreCase("admin")){

            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastreciever,
                    new IntentFilter(GcmRegistrationIntentService.registration_success));

            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastreciever,
                    new IntentFilter(GcmRegistrationIntentService.registrationerror));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(getIntent().getStringExtra("role")!=null &&
                getIntent().getStringExtra("role").equalsIgnoreCase("admin")) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastreciever);
        }
    }
}
