package com.ilma.testing.ilmaapp.Models;

/**
 * Created by fazal on 8/22/2017.
 */

public class Comment {
    String comment_user;
    String comment;
    String base64encode;
    String comment_UserID;
    String duration;
    String commentID;

    public Comment() {
    }

    public Comment(String comment_user, String comment, String comment_UserID, String duration, String commentID) {
        this.comment_user = comment_user;
        this.comment = comment;
        this.comment_UserID = comment_UserID;
        this.duration = duration;
        this.commentID = commentID;
    }

    public Comment(String comment_user, String comment, String base64encode, String comment_UserID, String duration, String commentID) {
        this.comment_user = comment_user;
        this.comment = comment;
        this.base64encode = base64encode;
        this.comment_UserID = comment_UserID;
        this.duration = duration;
        this.commentID = commentID;
    }

    public String getComment_user() {
        return comment_user;
    }

    public String getComment() {
        return comment;
    }

    public String getBase64encode() {
        return base64encode;
    }

    public String getComment_UserID() {
        return comment_UserID;
    }

    public String getDuration() {
        return duration;
    }

    public String getCommentID() {
        return commentID;
    }
}
