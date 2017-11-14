package com.ilma.testing.ilmaapp;

import android.app.Application;

import com.ilma.testing.ilmaapp.Utility.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fazal on 10/3/2017.
 */

public class IlmaManagementApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils utils = new Utils(getApplicationContext());
        if(utils.getdata("InstalledDate").equalsIgnoreCase("")) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            utils.savedata("InstalledDate", df.format(new Date()));
        }

    }
}
