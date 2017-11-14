package com.ilma.testing.ilmaapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.GridView;

import com.ilma.testing.ilmaapp.Adapter.ImageAdapter;
import com.ilma.testing.ilmaapp.R;

import java.io.Serializable;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private GridView gallery_listView;
    private ImageAdapter adapter;

    static final int REQUEST_IMAGE_CAPTURE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getSupportActionBar().setTitle(getString(R.string.gallery_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bindUI();
    }

    private void bindUI() {
        gallery_listView = (GridView) findViewById(R.id.galleryGridView);

        // initialize your items array
        adapter = new ImageAdapter(this);

        gallery_listView.setAdapter(adapter);
        gallery_listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }

            case R.id.upload:{
                uploadImage();
                break;
            }

            case R.id.camera_capture:{
                dispatchTakePictureIntent();
                break;
            }
        }
        return super.onOptionsItemSelected(item);

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Intent i = new Intent(this, ComposeActivity.class);
            i.putExtra("CaptureImage", imageBitmap);
            setResult(RESULT_OK, i);
            finish();
        }
    }

    private void uploadImage() {
        List<String> listImagePath = adapter.getSelectedImageList();
        if(listImagePath.size()>1){
            Snackbar.make(getWindow().getDecorView(), "Single selection is allowed.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        if(listImagePath.size() == 0){
            Snackbar.make(getWindow().getDecorView(), "Select image from gallery to proceed further", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }else {
            Intent i = new Intent(this, ComposeActivity.class);
            i.putExtra("listImagePath", (Serializable) listImagePath);
            setResult(RESULT_OK, i);
            finish();
        }

    }
}
