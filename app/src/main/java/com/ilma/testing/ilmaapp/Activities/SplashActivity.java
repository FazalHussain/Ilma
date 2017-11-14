package com.ilma.testing.ilmaapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.ilma.testing.ilmaapp.R;

import butterknife.ButterKnife;
import com.crashlytics.android.Crashlytics;

import butterknife.InjectView;
import io.fabric.sdk.android.Fabric;

public class SplashActivity extends Activity {

    @InjectView(R.id.image_view)
    ImageView imageView;

    @InjectView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);

        //SetUpVideo();
        Glide.with(this).load(R.drawable.ilmalogo).into(imageView);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                progressBar.setVisibility(View.GONE);
                startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                finish();
            }
        }, 5800);
    }

    private void SetUpVideo() {
        try {
            /*VideoView videoHolder = findViewById(R.id.videoview);
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
                    mediaPlayer.seekTo(62000);
                    mediaPlayer.setLooping(true);
                }
            });*/
        } catch (Exception ex) {

        }
    }
}
