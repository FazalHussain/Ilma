package com.ilma.testing.ilmaapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ilma.testing.ilmaapp.Models.Comment;
import com.ilma.testing.ilmaapp.R;
import com.facebook.login.widget.ProfilePictureView;

import java.util.List;

/**
 * Created by fazal on 8/22/2017.
 */

public class CommentAdapter extends ArrayAdapter<Comment> {

    List<Comment> listComment;

    public CommentAdapter(Context context, int resource, List<Comment> objects) {
        super(context, resource, objects);
        listComment = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try{
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            CommentHolder commentHolder;
            if(convertView == null){
                convertView = inflater.inflate(R.layout.comment_row,null,false);
                commentHolder = new CommentHolder(convertView);
                convertView.setTag(commentHolder);
            }else {
                commentHolder = (CommentHolder) convertView.getTag();
            }

            Comment comment = listComment.get(position);
            commentHolder.profilePictureView.setProfileId(comment.getComment_UserID());
            commentHolder.username_tv.setText(comment.getComment_user());
            commentHolder.comment_content.setText(comment.getComment());
            try {
                String[] duration_array = comment.getDuration().split(" ");
                String[] date_array = duration_array[0].split("-");
                String[] time_array = duration_array[1].split(":");

                commentHolder.duration_tv.setText(date_array[1]+" " + date_array[2] + " at " + time_array[0]+":" + time_array[1]);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return convertView;
    }

    public class CommentHolder {
        TextView username_tv;
        TextView duration_tv;
        TextView comment_content;
        ProfilePictureView profilePictureView;

        public CommentHolder(View itemView) {
            username_tv = itemView.findViewById(R.id.Post_Owner);
            profilePictureView = itemView.findViewById(R.id.profilePic);
            duration_tv = itemView.findViewById(R.id.duration);
            comment_content = itemView.findViewById(R.id.comment);
        }
    }
}
