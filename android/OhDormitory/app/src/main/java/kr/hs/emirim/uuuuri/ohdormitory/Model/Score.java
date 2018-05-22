package kr.hs.emirim.uuuuri.ohdormitory.Model;

/**
 * Created by 유리 on 2017-10-04.
 */

public class Score {
    private String detail;
    private Double score;

    public String getDetail() {
        return detail;
    }

    public Double getScore() {
        return score;
    }

    public Score(String detail, Double score) {
        this.detail = detail;
        this.score = score;
    }

    @Override
    public String toString() {
        return "Score{" +
                "detail='" + detail + '\'' +
                ", score=" + score +
                '}';
    }
}
