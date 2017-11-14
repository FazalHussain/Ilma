package com.ilma.testing.ilmaapp.Adapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ilma.testing.ilmaapp.Models.Post;
import com.ilma.testing.ilmaapp.R;
import com.facebook.login.widget.ProfilePictureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

/**
 * Created by fazal on 8/17/2017.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    List<Post> itemList;
    PostPhtoGridAdapter adapter;

    public OnItemClickListener onItemClickListener;
    private Context context;

    public PostAdapter(List<Post> itemList) {
        this.itemList = itemList;
    }

    public PostAdapter(List<Post> itemList, OnItemClickListener onItemClickListener) {
        this.itemList = itemList;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClicked(int pos, View view);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            context = parent.getContext();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.post_row, null, false);
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder (ViewHolder holder,int position){
            Post post = itemList.get(position);
            holder.username_tv.setText(post.getPost_Owner());
            holder.profilePictureView.setProfileId(post.getUserID());
            holder.duration_tv.setText(post.getDuration());
            holder.post_content.setText(post.getPostStatus());

            holder.comment_count.setText(post.getCommentsCount() + " Comments");

            File dir = new File(Environment.getExternalStorageDirectory(), "Office Management");
            if(!dir.exists()){
                dir.mkdirs();
            }

            if(post.getImageEncode()!=null &&
                    !post.getImageEncode().equalsIgnoreCase("NA")) {

                //DeleteFolder(dir);
                holder.postImageView.setVisibility(View.VISIBLE);


                try {

                    byte[] decodedString = Base64.decode(post.getImageEncode(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    InsertImages(decodedByte,post);
                    //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //decodedByte.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    //Glide.with(context).load(stream.toByteArray()).into(holder.postImageView);
                    //Uri uri = getImageUri(context, decodedByte);
                    Uri uri = Uri.fromFile(new File(Environment.
                            getExternalStorageDirectory(), "Office Management/OM_" +
                            post.getPostKey()+".jpg"));
                    Glide.with(context).load(uri)
                            .into(holder.postImageView);
/*
                    ContentResolver contentResolver = context.getContentResolver();
                    contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            MediaStore.Images.ImageColumns.DATA + "=?" , new String[]{ getPath(uri) });*/



                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                holder.postImageView.setVisibility(View.GONE);
            }


        }

    private void InsertImages(Bitmap bitmap, Post post) {
        OutputStream fOut = null;
        File file = new File(Environment.getExternalStorageDirectory(), "Office Management/OM_" +
                post.getPostKey() + ".jpg");
        if(file.exists()){
            file.delete();
        }
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "OM");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Office Management Images");
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
        values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.
                toString().toLowerCase(Locale.US).hashCode());
        values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.
                getName().toLowerCase(Locale.US));
        values.put("_data", file.getAbsolutePath());

        ContentResolver cr = context.getContentResolver();
        cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private void DeleteFolder(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteFolder(child);

        fileOrDirectory.delete();

    }


    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        try {

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
            return Uri.parse(path);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

        @Override
        public int getItemCount () {
            return itemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener {
            ImageView comment_icon;
            ImageView postImageView;
            TextView username_tv;
            TextView duration_tv;
            TextView post_content;
            ProfilePictureView profilePictureView;
            TextView like;
            TextView comment;
            TextView comment_count;
            ImageView action_edit;



            public ViewHolder(View itemView) {
                super(itemView);
                username_tv = itemView.findViewById(R.id.Post_Owner);
                profilePictureView = itemView.findViewById(R.id.profilePic);
                duration_tv = itemView.findViewById(R.id.duration);
                post_content = itemView.findViewById(R.id.post);
                like = itemView.findViewById(R.id.like_btn);
                comment = itemView.findViewById(R.id.comment);
                comment_icon = itemView.findViewById(R.id.icon_comment);
                postImageView = (ImageView) itemView.findViewById(R.id.postImageView);
                comment_count = itemView.findViewById(R.id.comment_count);
                action_edit = itemView.findViewById(R.id.action_edit);

                Typeface font = Typeface.createFromAsset( context.getAssets(), "fontawesome_webfont.ttf" );
                like.setTypeface(font);

                comment.setOnClickListener(this);
                comment_icon.setOnClickListener(this);
                comment_count.setOnClickListener(this);
                action_edit.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int clickedPosition = getAdapterPosition();
                onItemClickListener.onItemClicked(clickedPosition,view);
            }


        }
}
