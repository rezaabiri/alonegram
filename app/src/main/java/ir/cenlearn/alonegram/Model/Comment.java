package ir.cenlearn.alonegram.Model;

public class Comment {
    private String username;
    private String comment;
    private String imageComment;
    private String cuntcomment;
    private String post_id;
    private String datetime;
    private String fullname;
    private String bluetick;
    private String TargetReplay;
    private String PreviewReplay;
    private String commentid;


    public String getPreviewReplay() {
        return PreviewReplay;
    }

    public void setPreviewReplay(String previewReplay) {
        PreviewReplay = previewReplay;
    }

    public String getTargetReplay() {
        return TargetReplay;
    }

    public void setTargetReplay(String targetReplay) {
        TargetReplay = targetReplay;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBluetick() {
        return bluetick;
    }

    public void setBluetick(String bluetick) {
        this.bluetick = bluetick;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getCuntcomment() {
        return cuntcomment;
    }

    public void setCuntcomment(String cuntcomment) {
        this.cuntcomment = cuntcomment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImageComment() {
        return imageComment;
    }

    public void setImageComment(String imageComment) {
        this.imageComment = imageComment;
    }
}
