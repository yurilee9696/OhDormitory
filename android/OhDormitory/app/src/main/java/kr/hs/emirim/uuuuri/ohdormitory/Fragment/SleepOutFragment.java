package kr.hs.emirim.uuuuri.ohdormitory.Fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import kr.hs.emirim.uuuuri.ohdormitory.Activity.QRCamActivity;
import kr.hs.emirim.uuuuri.ohdormitory.Model.User;
import kr.hs.emirim.uuuuri.ohdormitory.R;

import static kr.hs.emirim.uuuuri.ohdormitory.R.id.sleep_out_date;


/**
 * Created by 유리 on 2017-10-01.
 */

// TODO: 2017-10-21  외박일지 신청안함 메세지 고정
public class SleepOutFragment extends Fragment {

    private static String TAG = "sleepout_fragment";

    private static final String TAG_JSON_C="return_code";
    private static final String TAG_JSON_N="notice";
    private static final String TAG_JSON_R="record";


    private FirebaseDatabase mDatabase;

    Button mCameraBtn;
    TextView mTextDate;
    TextView mTextMessage;
    TextView mTextParentCall;
    TextView mTextRecognize;
    View mView;



    String mJsonString;

    private final String USER_INFO_PREF = "User info";
    private final String OBJECT_USER = "Object user";

    private User mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView=inflater.inflate(R.layout.sleep_out_fragment, container, false);

        mTextDate =mView.findViewById(sleep_out_date);
        mTextMessage =mView.findViewById(R.id.sleep_out_message);
        mTextParentCall =mView.findViewById(R.id.parent_call);
        mTextRecognize =mView.findViewById(R.id.sleep_out_recognize);
        mCameraBtn=mView.findViewById(R.id.camera);
        mCameraBtn.setVisibility(View.GONE);
        mUser = getUserInfo();
//        checkRecognize(view);
        GetData task = new GetData();
        task.execute("http://54.203.113.95:8080/getSleepoutRecord.php?userID="+mUser.getEmirim_id());


        return mView;
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(mView.getContext(),
                    "잠시만 기다려주세요", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            mTextViewResult.setText(result);
//            Log.d(TAG, "response  - " + result);

            if (result == null){

//                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON_C);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String return_code = item.getString("return_code");
                String return_message = item.getString("return_message");
                Log.e(TAG,"return_code : "+return_code);
                Log.e(TAG,"return_message : "+return_message);
                if(return_code.equals("2")){//외박 신청했을 시
                    JSONArray noticeJson = jsonObject.getJSONArray(TAG_JSON_N);
                    JSONObject noticeItem = noticeJson.getJSONObject(0);

                    JSONArray recordJson = jsonObject.getJSONArray(TAG_JSON_R);
                    JSONObject recordItem = recordJson.getJSONObject(0);
                    Log.e(TAG,"recognize : "+recordItem.getString("recognize"));

                    mTextDate.setText(noticeItem.getString("sleep_w_time") + " ~ " + noticeItem.getString("sleep_d_time"));

                    mTextMessage.setText("인증 연락처 : ");
                    mTextParentCall.setText(mUser.getParent_phone());

                    if(recordItem.getString("recognize").equals("0")) {//아직 인증 안받음

                        mTextRecognize.setText("미인증");
                        mCameraBtn.setVisibility(View.VISIBLE);
                        mCameraBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(view.getContext(), QRCamActivity.class);
                                startActivity(intent);
                            }
                        });
                    }else{
                        mTextRecognize.setText("이미 인증받았습니다.");
                        mCameraBtn.setVisibility(View.GONE);


                    }

                }else {//외박증 인증 필요 x
                    Log.e(TAG,"인증 받을 필요 없다.");
                    mTextDate.setText("");
                    mTextMessage.setText(return_message);
                    mTextParentCall.setText("");
                    mTextRecognize.setText("");
                    mCameraBtn.setVisibility(View.GONE);

                }
            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
    // 현재 사용자 객체 get
    private User getUserInfo(){
        SharedPreferences prefs = mView.getContext().getSharedPreferences(USER_INFO_PREF, mView.getContext().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(OBJECT_USER, "");
        return gson.fromJson(json, User.class);
    }

}
