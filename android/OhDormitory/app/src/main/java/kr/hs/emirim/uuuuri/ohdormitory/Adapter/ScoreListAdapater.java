package kr.hs.emirim.uuuuri.ohdormitory.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import kr.hs.emirim.uuuuri.ohdormitory.Model.Score;
import kr.hs.emirim.uuuuri.ohdormitory.Model.StudentScore;
import kr.hs.emirim.uuuuri.ohdormitory.Model.UserScore;
import kr.hs.emirim.uuuuri.ohdormitory.R;

/**
 * Created by 유리 on 2017-10-04.
 */

public class ScoreListAdapater extends RecyclerView.Adapter<ScoreListAdapater.ViewHolder> {
    ArrayList<UserScore> studentScoreList;
    HashMap<Integer, Score> scoreHashMap;
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView tvDate;
        TextView tvDetail;
        TextView tvScore;
        public ViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.date);
            tvDetail = view.findViewById(R.id.detail);
            tvScore = view.findViewById(R.id.score);
        }
    }
    public ScoreListAdapater(ArrayList<UserScore> studentScoreArrayList, HashMap<Integer, Score> scoreHashMap) {
        studentScoreList = studentScoreArrayList;
        this.scoreHashMap = scoreHashMap;
    }

    @Override
    public ScoreListAdapater.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //recycler view에 반복될 아이템 레이아웃 연결
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score,null);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvDate.setText(studentScoreList.get(position).getDate());
//            Log.e("이런", studentScoreList.get(position).getType()+"");

//        if(scoreHashMap.get(studentScoreList.get(position).getType()) == null)
//            Log.e("저런", "저런");

        holder.tvDetail.setText(scoreHashMap.get(studentScoreList.get(position).getScore_id()).getDetail());
        holder.tvScore.setText(scoreHashMap.get(studentScoreList.get(position).getScore_id()).getScore()+"점");
    }

    @Override
    public int getItemCount() {
        return studentScoreList.size();
    }

}
