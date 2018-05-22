package kr.hs.emirim.uuuuri.ohdormitory.Model;

public class WashUser {

    private int wash_id; // -1 보다 작을 경우 existing user
    private int wash_day;
    private int wash_time;
    private int washer_num;
    private String user; // emirim_id or using_room

    public WashUser(int wash_id, int wash_day, int wash_time, int washer_num, String user) {
        this.wash_id = wash_id;
        this.wash_day = wash_day;
        this.wash_time = wash_time;
        this.washer_num = washer_num;
        this.user = user;
    }

    public int getWash_id() {
        return wash_id;
    }

    public int getWash_day() {
        return wash_day;
    }

    public int getWash_time() {
        return wash_time;
    }

    public int getWasher_num() {
        return washer_num;
    }

    public String getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "WashUser{" +
                "wash_id=" + wash_id +
                ", wash_day=" + wash_day +
                ", wash_time=" + wash_time +
                ", washer_num=" + washer_num +
                ", user='" + user + '\'' +
                '}';
    }
}
