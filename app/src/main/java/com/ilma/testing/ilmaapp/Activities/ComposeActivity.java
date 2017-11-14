package com.ilma.testing.ilmaapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ilma.testing.ilmaapp.Models.Comment;
import com.ilma.testing.ilmaapp.Models.Post;
import com.ilma.testing.ilmaapp.R;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ComposeActivity extends AppCompatActivity {

    private static final int REQUEST_GALERRY = 1001;
    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().getRoot();

    @InjectView(R.id.profilePic)
    ProfilePictureView profilePictureView;

    @InjectView(R.id.Post_Owner)
    TextView username_tv;

    @InjectView(R.id.thumbanil_img)
    ImageView thumb_image;

    @InjectView(R.id.post_et)
    EditText post_et;
    private ArrayList<String> listImagePath;
    private boolean updated;
    private Bitmap captureImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.inject(this);

        getSupportActionBar().setTitle(getString(R.string.post_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Map<String,Object> mMap = new HashMap<>();
        mMap.put("Posts","");
        mDatabaseRef.updateChildren(mMap);*/

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Posts");


        setUpProfile();
    }

    private List<String> convertToBase64(List<String> PathList) {
        Bitmap bitmap = null;
        List<String> encodedImage_list = new ArrayList<>();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for(String path: PathList){

            bitmap = BitmapFactory.decodeFile(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            encodedImage_list.add(encodedImage);
        }


        return encodedImage_list;
    }

    private List<String> convertToBase64(Bitmap bitmap) {

        List<String> encodedImage_list = new ArrayList<>();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            encodedImage_list.add(encodedImage);


        return encodedImage_list;
    }

    private void setUpProfile() {
        if(getIntent().getStringExtra("UserID")!=null){
            profilePictureView.setDrawingCacheEnabled(true);
            profilePictureView.setProfileId(getIntent().getStringExtra("UserID"));
            username_tv.setText(getIntent().getStringExtra("Username"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                if(updated){
                    Intent i = new Intent(ComposeActivity.this,HomeActivity.class);
                    setResult(RESULT_OK,i);
                    finish();
                }else
                finish();
                break;
            }

            case R.id.post:{
                Post();
                break;
            }
        }

        return true;
    }

    private void Post() {
        try {
            if(post_et.getText().length()==0) return;

            String postKey = mDatabaseRef.push().getKey();
            DatabaseReference mDatabaseRef_posts = mDatabaseRef.child(postKey);

            Map<String,Object> map = new HashMap<>();
            map.put("PostStatus",post_et.getText().toString());

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


            map.put("duration", df.format(new Date()));
            map.put("PostKey",postKey);
            map.put("UserID",getIntent().getStringExtra("UserID"));
            map.put("Post_Owner",getIntent().getStringExtra("Username"));
            //map.put("Comments",new Comment("NA","NA","NA","NA","NA"));
            //map.put("Images",new Image("",""));

            map.put("ImageEncode","NA");

            Map<String,Comment> map_comment = new HashMap<>();

            Comment comment = new Comment("NA","NA","NA","NA","NA");
            map_comment.put("Comments",comment);

            Post post = new Post(getIntent().getStringExtra("Username"),df.format(new Date()),
                    getIntent().getStringExtra("UserID"),
                    post_et.getText().toString(),postKey,"NA");

            post.setComments(map_comment);
            post.setCommentsCount(0);
        /*Post item = new Post(getIntent().getStringExtra("Username"),df.format(new Date()),
                getIntent().getStringExtra("UserID"),post_et.getText().toString(),
                mDatabaseRef.push().getKey(),new Comment());
        */

           // mDatabaseRef_posts.setValue(map);




            if(listImagePath!=null && listImagePath.size()>0){



                List<String> encodedImageList = convertToBase64(listImagePath);

                for(String imgPath : encodedImageList) {
                    //map.put("ImageEncode",imgPath);
                    post.setImageEncode(imgPath);
                    /*Image image = new Image(ImageKey,imgPath);

                    mDatabaseRef_images_key.setValue(image);*/
                }

            }else if(captureImage!=null){



                List<String> encodedImageList = convertToBase64(captureImage);

                for(String imgPath : encodedImageList) {
                    //map.put("ImageEncode",imgPath);

                    post.setImageEncode(imgPath);
                }
            }
            mDatabaseRef_posts.setValue(post);
            //DatabaseReference comment_ref = mDatabaseRef_posts.child("Comments");
            //String commentKey = comment_ref.push().getKey();
            //comment = new Comment("NA","NA","NA","NA","NA");
            //comment_ref.child(commentKey).setValue(comment);
            updated = true;
            listImagePath=null;
            thumb_image.setImageResource(0);
            post_et.getText().clear();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose_activity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void GalleryClick(View view){
        Intent i = new Intent(this,GalleryActivity.class);
        startActivityForResult(i,REQUEST_GALERRY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_GALERRY && resultCode==RESULT_OK){
            if(data.getStringArrayListExtra("listImagePath")!=null)
            listImagePath = data.getStringArrayListExtra("listImagePath");
            if(listImagePath!=null){
                Bitmap bitmap = BitmapFactory.decodeFile(listImagePath.get(0));
                thumb_image.setImageBitmap(bitmap);
            }
            if(data.getParcelableExtra("CaptureImage")!=null){

                captureImage = (Bitmap) data.getParcelableExtra("CaptureImage");
                thumb_image.setImageBitmap(captureImage);
            }

        }
    }
}
