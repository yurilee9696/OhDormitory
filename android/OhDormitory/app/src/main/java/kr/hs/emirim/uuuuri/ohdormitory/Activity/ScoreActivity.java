package kr.hs.emirim.uuuuri.ohdormitory.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import kr.hs.emirim.uuuuri.ohdormitory.Adapter.ScoreListAdapater;
import kr.hs.emirim.uuuuri.ohdormitory.Model.Score;
import kr.hs.emirim.uuuuri.ohdormitory.Model.UserScore;
import kr.hs.emirim.uuuuri.ohdormitory.R;

public class ScoreActivity extends BaseActivity {
    private final String TAG = "SCORE_ACTIVITY";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<UserScore> userScores;

    private double mMinusScore;
    private double mPlusScore;
    private double mTotalScore;

    private TextView mGuideText;
    private TextView mMinusText;
    private TextView mPlusText;
    private TextView mTotalText;

    private HashMap<Integer, Score> mScoreHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        initScoreMap(); // 벌점 목록 초기화

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);

        userScores = new ArrayList<UserScore>();
        mAdapter = new ScoreListAdapater(userScores, mScoreHashMap);
        mRecyclerView.setAdapter(mAdapter);

        mGuideText = findViewById(R.id.guideMessage);
        mPlusText = findViewById(R.id.plus);
        mMinusText = findViewById(R.id.minus);
        mTotalText = findViewById(R.id.total);

        readScore();
    }

    private void initScoreMap() {

        mScoreHashMap = new HashMap<>();

        String URL = "http://54.203.113.95/getScore.php";
        ScoreReceiver scoreReceiver = new ScoreReceiver();
        scoreReceiver.execute(URL);
    }

    private void readScore() {

        String URL = "http://54.203.113.95/getUserScore.php";
        UserScoreReceiver userScoreReceiver = new UserScoreReceiver();
        userScoreReceiver.execute(URL);
        mMinusScore = 0.0;
        mPlusScore = 0.0;
        mTotalScore = 0.0;
    }


    //get score list
    class ScoreReceiver extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                URL phpUrl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection)phpUrl.openConnection();

                if ( conn != null ) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    if ( conn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        while ( true ) {
                            String line = br.readLine();
                            if ( line == null )
                                break;
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {
            try {

                JSONObject jObject = new JSONObject(str);
                JSONArray results = jObject.getJSONArray("score");

                for ( int i = 0; i < results.length(); ++i ) {
                    JSONObject temp = results.getJSONObject(i);
                    Log.e(TAG, temp.getString("detail"));
                    Log.e(TAG, temp.getDouble("score")+"");
//                    String detail, Double score
                    mScoreHashMap.put(i, new Score(temp.getString("detail"), temp.getDouble("score")));
//                    Log.e(TAG, "["+i+"] detail : "+temp.getString("detail")+" / score : "+temp.getDouble("score"));
                }
                Log.e(TAG, mScoreHashMap.toString());
            } catch (JSONException e) {

                Log.e(TAG, "에러1 : "+e.toString());
            }

        }

    }

    class UserScoreReceiver extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                URL phpUrl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection)phpUrl.openConnection();

                if ( conn != null ) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    if ( conn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        while ( true ) {
                            String line = br.readLine();
                            if ( line == null )
                                break;
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {

            try {

                Log.e(TAG, "호잉?");
                JSONObject jObject = new JSONObject(str).getJSONObject("user_score");
                JSONArray results = jObject.getJSONArray("test");
                for ( int i = 0; i < results.length(); ++i ) {
                    JSONObject temp = results.getJSONObject(i);
                    //String date, int score_id
                    Double score = mScoreHashMap.get(temp.getInt("score_id")).getScore();

                    if(score < 0)
                        mMinusScore += score;
                    else mPlusScore += score;

                    userScores.add(new UserScore(temp.getString("date"), temp.getInt("score_id")));

                }
            } catch (JSONException e) {

                Log.e(TAG, "에러2 : "+e.toString());
            }

            setLayout();
            mAdapter.notifyDataSetChanged();
        }

    }

    private void setLayout() {
        mTotalScore = mMinusScore + mPlusScore;

        if(userScores.size() == 0){
            mGuideText.setVisibility(View.VISIBLE);
            mPlusText.setVisibility(View.INVISIBLE);
            mMinusText.setVisibility(View.INVISIBLE);
            mTotalText.setVisibility(View.INVISIBLE);

        }else{
            mGuideText.setVisibility(View.INVISIBLE);

            mPlusText.setVisibility(View.VISIBLE);
            mMinusText.setVisibility(View.VISIBLE);
            mTotalText.setVisibility(View.VISIBLE);
            mPlusText.setText("상점 : "+mPlusScore+" 점");
            mMinusText.setText("벌점 : "+mMinusScore+" 점");
            mTotalText.setText("총 "+ mTotalScore+" 점");
        }
    }
}
