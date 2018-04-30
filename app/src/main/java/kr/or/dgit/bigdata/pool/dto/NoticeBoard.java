package kr.or.dgit.bigdata.pool.dto;

import android.graphics.Bitmap;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by DGIT3-7 on 2018-04-19.
 */

public class NoticeBoard{
    private int nno;
    private String title;
    private String content;
    private Date regdate;
    private Date updatedate;
    private String imgpath;
    private int readcnt;

    public int getNno() {
        return nno;
    }

    public void setNno(int nno) {
        this.nno = nno;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getReadcnt() {
        return readcnt;
    }

    public void setReadcnt(int readcnt) {
        this.readcnt = readcnt;
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

    @Override
    public String toString() {
        return "NoticeBoardVO [nno=" + nno + ", title=" + title + ", content=" + content + ", regdate=" + regdate
                + ", updatedate=" + updatedate + ", imgpath=" + imgpath + "]";
    }

}
