package kr.or.dgit.bigdata.pool.dto;

import java.util.Date;

/**
 * Created by DGIT3-7 on 2018-04-25.
 */

public class ClassboardReply {
    private int rno;
    private int bno;
    private String id;
    private String replytext;
    private Date regdate;

    public ClassboardReply() {
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ClassboardReply{");
        sb.append("rno=").append(rno);
        sb.append(", bno=").append(bno);
        sb.append(", id='").append(id).append('\'');
        sb.append(", replytext='").append(replytext).append('\'');
        sb.append(", regdate=").append(regdate);
        sb.append('}');
        return sb.toString();
    }

    public int getRno() {
        return rno;
    }

    public void setRno(int rno) {
        this.rno = rno;
    }

    public int getBno() {
        return bno;
    }

    public void setBno(int bno) {
        this.bno = bno;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReplytext() {
        return replytext;
    }

    public void setReplytext(String replytext) {
        this.replytext = replytext;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }
}
