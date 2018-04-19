package kr.or.dgit.bigdata.pool.dto;

import java.util.Date;

/**
 * Created by DGIT3-10 on 2018-04-18.
 */

public class Member {
    private int mno;
    private String name;
    private int age;
    private String tell;
    private Date date;
    private String email;
    private String gender;
    private String pw;
    private String id;
    private String title;
    private boolean isleave;

    public int getMno() {
        return mno;
    }

    public void setMno(int mno) {
        this.mno = mno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getTell() {
        return tell;
    }

    public void setTell(String tell) {
        this.tell = tell;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
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

    public boolean isIsleave() {
        return isleave;
    }

    public void setIsleave(boolean isleave) {
        this.isleave = isleave;
    }

    @Override
    public String toString() {
        return "Member{" +
                "mno=" + mno +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", tell='" + tell + '\'' +
                ", date=" + date +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", pw='" + pw + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", isleave=" + isleave +
                '}';
    }
}
