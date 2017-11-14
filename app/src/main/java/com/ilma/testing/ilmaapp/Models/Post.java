package com.ilma.testing.ilmaapp.Models;

import java.util.Map;

/**
 * Created by fazal on 8/17/2017.
 */

public class Post {
    String post_Owner;
    String duration;
    String userID;
    String postStatus;
    String postKey;
    String ImageEncode;
    Image listmagePath;
    long commentsCount;

    Map<String, Comment> comments;

    public Post() {
    }

    public Post(String Post_Owner, String duration, String userID, String PostStatus, String postKey) {
        this.post_Owner = Post_Owner;
        this.duration = duration;
        this.userID = userID;
        this.postStatus = PostStatus;
        this.postKey = postKey;
    }

    public Post(String Post_Owner, String duration, String userID, String PostStatus, String postKey, String ImageEncode) {
        this(Post_Owner,duration,userID, PostStatus, postKey);
        this.ImageEncode = ImageEncode;
    }



    public String getPost_Owner() {
        return post_Owner;
    }

    public String getDuration() {
        return duration;
    }

    public String getUserID() {
        return userID;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public String getPostKey() {
        return postKey;
    }

    public Image getListmagePath() {
        return listmagePath;
    }

    public void setListmagePath(Image listmagePath) {
        this.listmagePath = listmagePath;
    }

    public String getImageEncode() {
        return ImageEncode;
    }

    public void setImageEncode(String imageEncode) {
        ImageEncode = imageEncode;
    }

    public Map<String, Comment> getComments() {
        return comments;
    }

    public void setComments(Map<String, Comment> comments) {
        this.comments = comments;
    }

    public long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
