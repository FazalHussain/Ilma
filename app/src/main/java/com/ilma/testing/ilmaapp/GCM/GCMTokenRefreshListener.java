package com.ilma.testing.ilmaapp.GCM;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by bbb on 11/12/2017.
 */

public class GCMTokenRefreshListener extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent gcmregister = new Intent(this,GCMTokenRefreshListener.class);
        startService(gcmregister);

    }
}
