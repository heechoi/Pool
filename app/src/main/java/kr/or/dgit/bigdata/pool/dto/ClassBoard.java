package kr.or.dgit.bigdata.pool.dto;

import android.graphics.Bitmap;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by DGIT3-7 on 2018-04-19.
 */

public class ClassBoard implements Serializable{
    private int bno;
    private int cno;
    private String id;
    private String title;
    private String content;
    private Date regdate;
    private Date updatedate;
    private String imgpath;
    private int readcnt;
    private Bitmap rotateImage;
    private LinearLayout replyList;

    public LinearLayout getReplyList() {
        return replyList;
    }

    public void setReplyList(LinearLayout replyList) {
        this.replyList = replyList;
    }

    public ClassBoard() {}

    public int getBno() {
        return bno;
    }

    public void setBno(int bno) {
        this.bno = bno;
    }

    public int getCno() {
        return cno;
    }

    public void setCno(int cno) {
        this.cno = cno;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }

    public Date getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(Date updatedate) {
        this.updatedate = updatedate;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public int getReadcnt() {
        return readcnt;
    }

    public void setReadcnt(int readcnt) {
        this.readcnt = readcnt;
    }

    public Bitmap getRotateImage() {
        return rotateImage;
    }

    public void setRotateImage(Bitmap rotateImage) {
        this.rotateImage = rotateImage;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ClassBoard{");
        sb.append("bno=").append(bno);
        sb.append(", cno=").append(cno);
        sb.append(", id='").append(id).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append(", regdate=").append(regdate);
        sb.append(", updatedate=").append(updatedate);
        sb.append(", imgpath='").append(imgpath).append('\'');
        sb.append(", readcnt=").append(readcnt);
        sb.append('}');
        return sb.toString();
    }
}
