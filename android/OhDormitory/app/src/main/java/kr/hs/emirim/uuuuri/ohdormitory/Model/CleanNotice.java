package kr.hs.emirim.uuuuri.ohdormitory.Model;

import java.util.HashMap;
import java.util.Map;

public class CleanNotice extends Notice {
    private Map<Integer, Integer> cleanList;

    public CleanNotice(int notice_id, String title, String w_time, int type, Map<Integer, Integer> cleanList) {
        super(notice_id, title, w_time, type);
        this.cleanList = cleanList;
    }

    public Map<Integer, Integer> getCleanList() {
        return cleanList;
    }
}
