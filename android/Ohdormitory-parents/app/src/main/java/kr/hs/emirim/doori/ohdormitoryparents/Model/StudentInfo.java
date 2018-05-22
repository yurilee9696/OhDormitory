package kr.hs.emirim.doori.ohdormitoryparents.Model;

import java.io.Serializable;

public class StudentInfo implements Serializable{
    private String name;
    private int notice_id;
    private String emirim_id;
    private String sleep_type;
    private String sleep_w_time;
    private String sleep_d_time;
    private boolean recognize;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNotice_id() {
        return notice_id;
    }

    public void setNotice_id(int notice_id) {
        this.notice_id = notice_id;
    }

    public String getEmirim_id() {
        return emirim_id;
    }

    public void setEmirim_id(String emirim_id) {
        this.emirim_id = emirim_id;
    }

    public String getSleep_type() {
        return sleep_type;
    }

    public void setSleep_type(String sleep_type) {
        this.sleep_type = sleep_type;
    }

    public String getSleep_w_time() {
        return sleep_w_time;
    }

    public void setSleep_w_time(String sleep_w_time) {
        this.sleep_w_time = sleep_w_time;
    }

    public String getSleep_d_time() {
        return sleep_d_time;
    }

    public void setSleep_d_time(String sleep_d_time) {
        this.sleep_d_time = sleep_d_time;
    }

    public boolean isRecognize() {
        return recognize;
    }

    public void setRecognize(boolean recognize) {
        this.recognize = recognize;
    }

    public StudentInfo(String name, int notice_id, String emirim_id, String sleep_type, String sleep_w_time, String sleep_d_time, boolean recognize) {
        this.name = name;
        this.notice_id = notice_id;
        this.emirim_id = emirim_id;
        this.sleep_type = sleep_type;
        this.sleep_w_time = sleep_w_time;
        this.sleep_d_time = sleep_d_time;
        this.recognize = recognize;
    }
}
