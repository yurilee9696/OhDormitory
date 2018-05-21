package kr.hs.emirim.uuuuri.ohdormitory.Model;

public class BasicNotice extends Notice {

    private String content;

    public BasicNotice(int notice_id, String title, String w_time, int type, String content) {
        super(notice_id, title, w_time, type);
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
