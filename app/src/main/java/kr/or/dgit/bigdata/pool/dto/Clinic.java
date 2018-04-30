package kr.or.dgit.bigdata.pool.dto;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by DGIT3-7 on 2018-04-30.
 */

public class Clinic implements Serializable {
    private String clinic_title;
    private String clinic_content;
    private String clinic_path;
    private Bitmap rotate;

    public Bitmap getRotate() {
        return rotate;
    }

    public void setRotate(Bitmap rotate) {
        this.rotate = rotate;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Clinic{");
        sb.append("clinic_title='").append(clinic_title).append('\'');
        sb.append(", clinic_content='").append(clinic_content).append('\'');
        sb.append(", clinic_path='").append(clinic_path).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public Clinic() {
    }

    public String getClinic_title() {

        return clinic_title;
    }

    public void setClinic_title(String clinic_title) {
        this.clinic_title = clinic_title;
    }

    public String getClinic_content() {
        return clinic_content;
    }

    public void setClinic_content(String clinic_content) {
        this.clinic_content = clinic_content;
    }

    public String getClinic_path() {
        return clinic_path;
    }

    public void setClinic_path(String clinic_path) {
        this.clinic_path = clinic_path;
    }
}
