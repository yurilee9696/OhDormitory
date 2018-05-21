package kr.hs.emirim.uuuuri.ohdormitory.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kr.hs.emirim.uuuuri.ohdormitory.Activity.SignInActivity;
import kr.hs.emirim.uuuuri.ohdormitory.Adapter.NoticeListAdapter;
import kr.hs.emirim.uuuuri.ohdormitory.Model.BasicNotice;
import kr.hs.emirim.uuuuri.ohdormitory.Model.CleanNotice;
import kr.hs.emirim.uuuuri.ohdormitory.Model.Notice;
import kr.hs.emirim.uuuuri.ohdormitory.Model.Notice2;
import kr.hs.emirim.uuuuri.ohdormitory.Model.NoticeKind;
import kr.hs.emirim.uuuuri.ohdormitory.Model.SleepoutNotice;
import kr.hs.emirim.uuuuri.ohdormitory.Model.User;
import kr.hs.emirim.uuuuri.ohdormitory.R;

/**
 * Created by 유리 on 2017-10-01.
 */

public class NoticeFragment extends Fragment {
    private final String TAG = "NOTICE_FRAGMENT";

    private final String USER_INFO_PREF = "User info";
    private final String OBJECT_USER = "Object user";


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Notice> noticeDataset;

    private Sender sender;
    private final String URL = "http://54.203.113.95/getNotice.php";

    private User mUser;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.notice_fragment, container, false);
        mContext = view.getContext();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_notice);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);


        noticeDataset = new ArrayList<>();
        mAdapter = new NoticeListAdapter(noticeDataset, getUserInfo());
        mRecyclerView.setAdapter(mAdapter);


        sender = new Sender();
        sender.execute(URL);


        return view;
    }

    class Sender extends AsyncTask<String, Integer, String> {

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
//            Log.e(TAG, "가져온 결과는 ? ? " + jsonHtml.toString());
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {
            try {
                JSONObject jObject = new JSONObject(str).getJSONObject("notice");
                Iterator<String> iterator = jObject.keys();

                while(iterator.hasNext()){
                    String key = iterator.next();
                    Log.e(TAG, key);
                    JSONObject innerJObject = jObject.getJSONObject(key);
                    JSONObject detailJson;

                    switch(innerJObject.getInt("type")){
                        case NoticeKind.BASIC_NOTICE: // 일반 공지사항
                            //String notice_id, String title, String w_time, int type, String content
                            detailJson = innerJObject.getJSONObject("detail");
                            noticeDataset.add(
                                    new BasicNotice(innerJObject.getInt("notice_id"),
                                            innerJObject.getString("title"),
                                            innerJObject.getString("w_time"),
                                            innerJObject.getInt("type"),
                                            detailJson.getString("content"))
                            );
                            break;
                        case NoticeKind.CLEAN_NOTICE:
                            //int notice_id, String title, String w_time, int type, Map<Integer, Integer> cleanList
                            Map<Integer, Integer> map = new HashMap<>() ;
                            JSONArray detailArray = innerJObject.getJSONArray("detail");
                            for(int i = 0; i<detailArray.length(); i++){
                                map.put(i, detailArray.getInt(i));
                            }
                            noticeDataset.add(
                                    new CleanNotice(innerJObject.getInt("notice_id"),
                                            innerJObject.getString("title"),
                                            innerJObject.getString("w_time"),
                                            innerJObject.getInt("type"),
                                            map)
                            );
                            break;
                        case NoticeKind.SLEEP_OUT_NOTICE:
                            detailJson = innerJObject.getJSONObject("detail");
                            //int notice_id, String title, String w_time, int type, String application_deadline, String sleep_w_time, String sleep_d_time, int send
                            noticeDataset.add(
                                    new SleepoutNotice(innerJObject.getInt("notice_id"),
                                            innerJObject.getString("title"),
                                            innerJObject.getString("w_time"),
                                            innerJObject.getInt("type"),
                                            detailJson.getString("application_deadline"),
                                            detailJson.getString("sleep_w_time"),
                                            detailJson.getString("sleep_d_time"),
                                            detailJson.getInt("send"))
                            );
                            break;
                    }

                }

            } catch (JSONException e) {

                Log.e(TAG, "에러 : "+e.toString());
            }
            mAdapter.notifyDataSetChanged();
        }

    }

    // 현재 사용자 객체 get
    private User getUserInfo(){
        SharedPreferences prefs = mContext.getSharedPreferences(USER_INFO_PREF, mContext.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(OBJECT_USER, "");
        return gson.fromJson(json, User.class);
    }


}

