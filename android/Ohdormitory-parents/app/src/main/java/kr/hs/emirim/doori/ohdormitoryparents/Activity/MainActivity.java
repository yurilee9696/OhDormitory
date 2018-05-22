package kr.hs.emirim.doori.ohdormitoryparents.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import kr.hs.emirim.doori.ohdormitoryparents.Adapter.RecyclerAdapter;
import kr.hs.emirim.doori.ohdormitoryparents.FCM.FirebaseInstanceIDService;
import kr.hs.emirim.doori.ohdormitoryparents.Manifest;
import kr.hs.emirim.doori.ohdormitoryparents.Model.StudentInfo;
import kr.hs.emirim.doori.ohdormitoryparents.R;

public class MainActivity extends BaseActivity {

    private static String TAG = "MainActivity";

    private static String TAG_JSON_C = "return_code";
    private static String TAG_JSON_N = "notice";
    private static String TAG_JSON_S = "student";

    private TextView textView_message;

    String mJsonString;

    private FirebaseDatabase mDatabase;
    String myNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView_message=(TextView)findViewById(R.id.textview_message);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            //접근 승낙 상태 일때
            checkCallPermission();
        } else{
            //Manifest.permission.READ_CALENDAR이 접근 거절 상태 일때
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_PHONE_STATE)){
                //사용자가 다시 보지 않기에 체크를 하지 않고, 권한 설정을 거절한 이력이 있는 경우
            } else{
                //사용자가 다시 보지 않기에 체크하고, 권한 설정을 거절한 이력이 있는 경우
            }

            //사용자에게 접근권한 설정을 요구하는 다이얼로그를 띄운다.
            //만약 사용자가 다시 보지 않기에 체크를 했을 경우엔 권한 설정 다이얼로그가 뜨지 않고,
            //곧바로 OnRequestPermissionResult가 실행된다.
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_PHONE_STATE},0);

        }



//        //이부분 지우기
//

//
//        GetData task = new GetData();
//        task.execute("http://54.203.113.95/getSleepoutQRInfo.php?parent_phone="+myNumber);
//        //여기까지
//
//        //주석풀기
//        // checkCallPermission();
//
//        //FirebaseMessaging.getInstance().subscribeToTopic("parentNotice");
//

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult){
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        //위 예시에서 requestPermission 메서드를 썼을시 , 마지막 매개변수에 0을 넣어 줬으므로, 매칭
        if(requestCode == 0){
            // requestPermission의 두번째 매개변수는 배열이므로 아이템이 여러개 있을 수 있기 때문에 결과를 배열로 받는다.
            // 해당 예시는 요청 퍼미션이 한개 이므로 i=0 만 호출한다.
            if(grantResult[0] == 0){
                //해당 권한이 승낙된 경우.
                checkCallPermission();
            }else{
                //해당 권한이 거절된 경우.
            }
        }
    }




    public void checkCallPermission(){
        try {
            final TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            myNumber = mgr.getLine1Number();
            if(myNumber == null)
                myNumber = "01050502026"; //test용... 공기계일 떄
            else {
                myNumber = myNumber.replaceAll("+82", "0");
                myNumber = myNumber.replaceAll(" ", "");
                myNumber = myNumber.replaceAll("-", "");
            }
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("phone_number", myNumber);
            editor.commit();

                Log.e("내 전화번호", myNumber);

            //push 알람 토큰  database에
            FirebaseMessaging.getInstance().subscribeToTopic("studentNotice");
            FirebaseInstanceIDService f=new FirebaseInstanceIDService(myNumber);
            f.sendRegistrationToServer();

            GetData task = new GetData();
            task.execute("http://54.203.113.95/getSleepoutQRInfo.php?parent_phone=" + myNumber);
//
//                    FirebaseInstanceIDService fcmIDservice=new FirebaseInstanceIDService();
//                    fcmIDservice.sendRegistrationToServer(myNumber);
//
//                    getSleepOutInfo(myNumber);

        }catch (SecurityException e){

        }

    }






//    public void getSleepOutInfo(final String myNumber){
//
//        mDatabase = FirebaseDatabase.getInstance();
//
//        final DatabaseReference sleepOutRef = mDatabase.getReference("sleep-out");
//
//        showProgressDialog();
//        ValueEventListener sleepOutListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot sleepOutData) {
//                Iterator<DataSnapshot> sleepOutIterator = sleepOutData.getChildren().iterator();
//                //users의 모든 자식들의 key값과 value 값들을 iterator로 참조
//                //TODO :외박날짜 최근꺼 가져와서..
//                while(sleepOutIterator.hasNext()) {
//                    DataSnapshot sleepOut = sleepOutIterator.next();
//                    String sleepOutDate = sleepOut.getKey();
//                    checkDate(sleepOutDate);
//                }
//
//                String send = sleepOutData.child(maxFBdate).child("send").getValue(String.class);
//                    if(send==null) send="false";
//
//                    Iterator<DataSnapshot> sleepOutStudentIterator = sleepOutData.child(maxFBdate).getChildren().iterator();
//                    String qrcodeContent=null;
//                    while (sleepOutStudentIterator.hasNext()) {
//                        DataSnapshot sleepOutStudent = sleepOutStudentIterator.next();
//                        String studentKey = sleepOutStudent.getKey();
//
//                        if(!studentKey.equals("send") && send.equals("true")) {
//                            String parentNumber = sleepOutStudent.child("parentNumber").getValue(String.class);
//                            String  recognize= sleepOutStudent.child("recognize").getValue(String.class);
//                            parentNumber = parentNumber.replace("-", "");
//
//                                Log.e("부모님 번호", parentNumber);
//                                if (myNumber.equals(parentNumber)) {//기기 번호와 부모번호가 같으면
//                                    Log.e("기기번호와 부모번호가 같으면", "큐얼코드 생성");
//                                    if (recognize.equals("true")) {
//                                        Log.e("qr", "이미 인증하셨습니다");
//                                        textView.setVisibility(View.VISIBLE);
//                                        qrImage.setVisibility(View.GONE);
//                                        textView.setText("이미 인증하셨습니다.");
//                                        alreadyRecognize=true;
//                                    } else {
//
//                                        qrcodeContent = maxFBdate + "/" + studentKey;
//
//
//                                        qrImage.setVisibility(View.VISIBLE);
//                                        textView.setVisibility(View.GONE);
//                                        Log.e("qr", qrcodeContent);
//                                        generateQRCode(qrcodeContent);
//
//                                        String[] dates = maxFBdate.split("-");
//                                        ((TextView) findViewById(R.id.guideText)).setText(dates[0] + "." + dates[1] + "." + dates[2] + ". - " + dates[3] + "." + dates[4] + "." + dates[5] + "\n\n외박인증 큐알코드입니다.");
//                                    }
//                                }
//                            }
//
//                    }
//                    if(qrcodeContent==null && !alreadyRecognize){
//                        Log.e("TAG", "HERE >3<");
//                        textView.setVisibility(View.VISIBLE);
//                        textView.setText("자녀의 외박 신청이 없습니다.");
//                        qrImage.setVisibility(View.GONE);
//                        ((TextView)findViewById(R.id.guideText)).setText("");
//                    }
//
//                hideProgressDialog();
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        };
//        sleepOutRef.addValueEventListener(sleepOutListener);
//    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
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

                if(return_code.equals("3") || return_code.equals("4")) {//외박 신청했을 시
                    textView_message.setText("");


                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(layoutManager);
                    List<StudentInfo> studentInfoList = new ArrayList<>();


                    JSONArray noticeJson = jsonObject.getJSONArray(TAG_JSON_N);
                    JSONObject noticeItem = noticeJson.getJSONObject(0);

                    int notice_id = noticeItem.getInt("notice_id");
                    String sleep_w_time = noticeItem.getString("sleep_w_time");
                    String sleep_d_time = noticeItem.getString("sleep_d_time");



                    JSONArray recordJson = jsonObject.getJSONArray(TAG_JSON_S);

                    StudentInfo[] studentInfos = new StudentInfo[recordJson.length()];

                    for(int j=0;j<recordJson.length();j++) {
                        JSONObject recordItem = recordJson.getJSONObject(j);
                        String emirim_id = recordItem.getString("emirim_id");
                        String name = recordItem.getString("name");
                        String sleep_type = recordItem.getString("sleep_type");
                        boolean recognize = recordItem.getInt("recognize")==0?false:true;

                        studentInfos[j] = new StudentInfo(name,notice_id,emirim_id,sleep_type,sleep_w_time,sleep_d_time,recognize);
                    }

                    for (int j = 0; j < recordJson.length(); j++) {
                        studentInfoList.add(studentInfos[j]);
                    }

            recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(), studentInfoList, R.layout.activity_main));


                    }else{

                        textView_message.setText(return_message);



                }
            }



        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

}
