package com.ilma.testing.ilmaapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ilma.testing.ilmaapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fazal on 8/23/2017.
 */

public class ImageAdapter extends BaseAdapter {

    private final ArrayList<String> images;
    private Activity context;
    List<String> selectedImageUriList = new ArrayList<>();
    String selected;

    /**
     * Instantiates a new image adapter.
     *
     * @param localContext
     *            the local context
     */
    public ImageAdapter(Activity localContext) {
        context = localContext;
        images = getAllShownImagesPath(context);
    }

    public int getCount() {
        return images.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView,
                        ViewGroup parent) {

        final GalleryHolder holder;
        if (convertView == null) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.gallery_item,null,false);
            holder = new GalleryHolder(convertView);
            convertView.setTag(holder);

        } else {

            holder = (GalleryHolder) convertView.getTag();
        }

        Glide.with(context).load(images.get(position))
                .into(holder.image);



        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {





                if(holder.check_img.getVisibility() == View.VISIBLE){
                    if(selectedImageUriList.size()>0)
                        selectedImageUriList.remove(images.get(position));
                    holder.check_img.setVisibility(View.GONE);

                }else{

                    selectedImageUriList.add(images.get(position));
                    holder.check_img.setVisibility(View.VISIBLE);
                }
            }
        });

        return convertView;
    }

    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }

    public class GalleryHolder{
        ImageView image;
        ImageView check_img;

        public GalleryHolder(View itemView) {
            image = itemView.findViewById(R.id.image_gallery);
            check_img = itemView.findViewById(R.id.check_image);
        }
    }

    public List<String> getSelectedImageList(){
        return selectedImageUriList;
    }

}
