package com.ilma.testing.ilmaapp.Activities;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ilma.testing.ilmaapp.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProfileActivity extends AppCompatActivity {

    @InjectView(R.id.wv_profile)
    WebView webview_profile;

    @InjectView(R.id.progress_bar)
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.inject(this);
        show_profile();
    }



    private void show_profile() {
        try{
            WebSettings settings = webview_profile.getSettings();
            settings.setJavaScriptEnabled(true);


            //The default value is true for API level android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 and below,
            //and false for API level android.os.Build.VERSION_CODES.JELLY_BEAN and above.
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                settings.setAllowUniversalAccessFromFileURLs(true);

            settings.setBuiltInZoomControls(true);
            webview_profile.setWebChromeClient(new WebChromeClient());
            webview_profile.loadUrl(getIntent().getStringExtra("uri"));

            webview_profile.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                    view.loadUrl(urlNewString);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                    //SHOW LOADING IF IT ISNT ALREADY VISIBLE
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (pb != null && pb.getVisibility() == View.VISIBLE)
                        pb.setVisibility(View.GONE);

                }
            });
        }catch (Exception e){
            pb.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
