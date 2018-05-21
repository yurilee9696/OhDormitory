package kr.hs.emirim.uuuuri.ohdormitory.Model;

import java.util.ArrayList;

public class UserScore {
    private String date;
    private int score_id;

    public UserScore(String date, int score_id) {
        this.date = date;
        this.score_id = score_id;
    }

    public String getDate() {
        return date;
    }

    public int getScore_id() {
        return score_id;
    }

    @Override
    public String toString() {
        return "UserScore{" +
                "date='" + date + '\'' +
                ", score_id=" + score_id +
                '}';
    }
}
