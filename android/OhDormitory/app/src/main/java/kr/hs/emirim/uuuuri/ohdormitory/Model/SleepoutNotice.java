package kr.hs.emirim.uuuuri.ohdormitory.Model;

public class SleepoutNotice extends Notice {
    private String application_deadline;
    private String sleep_w_time;
    private String sleep_d_time;
    private int send;

    public SleepoutNotice(int notice_id, String title, String w_time, int type, String application_deadline, String sleep_w_time, String sleep_d_time, int send) {
        super(notice_id, title, w_time, type);
        this.application_deadline = application_deadline;
        this.sleep_w_time = sleep_w_time;
        this.sleep_d_time = sleep_d_time;
        this.send = send;
    }

    public String getApplication_deadline() {
        return application_deadline;
    }

    public String getSleep_w_time() {
        return sleep_w_time;
    }

    public String getSleep_d_time() {
        return sleep_d_time;
    }

    public int getSend() {
        return send;
    }
}
