package kr.hs.emirim.uuuuri.ohdormitory.Model;

import java.io.Serializable;

public class Notice implements Serializable {
    private int notice_id;
    private String title;
    private String w_time;
    private int type;

    protected  Notice(int notice_id, String title, String w_time, int type) {
        this.notice_id = notice_id;
        this.title = title;
        this.w_time = w_time;
        this.type = type;
    }

    public int getNotice_id() {
        return notice_id;
    }

    public String getTitle() {
        return title;
    }

    public String getW_time() {
        return w_time;
    }

    public int getType() {
        return type;
    }
}
