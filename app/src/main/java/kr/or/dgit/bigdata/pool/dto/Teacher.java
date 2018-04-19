package kr.or.dgit.bigdata.pool.dto;

/**
 * Created by DGIT3-4 on 2018-04-19.
 */

public class Teacher {
    private int tno;
    private String name;
    private String tell;
    private String title;
    private String pw;
    private String id;
    private String img_path;
    private String info;

    public Teacher() {}

    public int getTno() {
        return tno;
    }

    public String getName() {
        return name;
    }

    public String getTell() {
        return tell;
    }

    public String getTitle() {
        return title;
    }

    public String getPw() {
        return pw;
    }

    public String getId() {
        return id;
    }

    public String getImg_path() {
        return img_path;
    }

    public String getInfo() {
        return info;
    }

    public void setTno(int tno) {
        this.tno = tno;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTell(String tell) {
        this.tell = tell;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "tno=" + tno +
                ", name='" + name + '\'' +
                ", tell='" + tell + '\'' +
                ", title='" + title + '\'' +
                ", pw='" + pw + '\'' +
                ", id='" + id + '\'' +
                ", img_path='" + img_path + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
