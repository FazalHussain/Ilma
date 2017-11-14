package com.ilma.testing.ilmaapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ilma.testing.ilmaapp.Models.Image;
import com.ilma.testing.ilmaapp.Models.Post;
import com.ilma.testing.ilmaapp.R;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by fazal on 8/24/2017.
 */

public class PostPhtoGridAdapter extends ArrayAdapter<Post> {

    Context context;
    List<Image> postImgList;

    /*OnGridListener onGridListener;

    public interface OnGridListener{
        void onGridChnaged();
    }*/

    public PostPhtoGridAdapter(Context context, int resource, List<Post> objects, List<Image> post) {
        super(context, resource, objects);
        this.context = context;
        this.postImgList = post;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GalleryHolder holder;
        if (convertView == null) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.post_images,null,false);
            holder = new GalleryHolder(convertView);
            convertView.setTag(holder);

        } else {

            holder = (GalleryHolder) convertView.getTag();
        }

        if(postImgList!=null) {
            try {
                if (postImgList.get(position) != null) {
                    byte[] decodedString = Base64.decode(postImgList.get(position).getBase64Img(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    decodedByte.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Glide.with(context).load(stream.toByteArray())
                            .into(holder.image);

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        return convertView;
    }

    public class GalleryHolder{
        ImageView image;

        public GalleryHolder(View itemView) {
            image = itemView.findViewById(R.id.image_gallery);
        }
    }
}
