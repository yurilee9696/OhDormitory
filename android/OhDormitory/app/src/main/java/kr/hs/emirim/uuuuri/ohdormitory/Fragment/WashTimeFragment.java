package kr.hs.emirim.uuuuri.ohdormitory.Fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import kr.hs.emirim.uuuuri.ohdormitory.Activity.MainActivity;
import kr.hs.emirim.uuuuri.ohdormitory.Activity.ModifyPwdActivity;
import kr.hs.emirim.uuuuri.ohdormitory.Adapter.Connector;
import kr.hs.emirim.uuuuri.ohdormitory.Adapter.NotificationAdapter;
import kr.hs.emirim.uuuuri.ohdormitory.Model.BasicNotice;
import kr.hs.emirim.uuuuri.ohdormitory.Model.CleanNotice;
import kr.hs.emirim.uuuuri.ohdormitory.Model.Day;
import kr.hs.emirim.uuuuri.ohdormitory.Model.NoticeKind;
import kr.hs.emirim.uuuuri.ohdormitory.Model.SleepoutNotice;
import kr.hs.emirim.uuuuri.ohdormitory.Model.User;
import kr.hs.emirim.uuuuri.ohdormitory.Model.WashUser;
import kr.hs.emirim.uuuuri.ohdormitory.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 유리 on 2017-10-01.
 */

// TODO: 2017-10-07 시간 4개 점으로 표현하기,,

public class WashTimeFragment extends Fragment implements Day{
    private final String TAG = "WASH_TIME_FRAGMENT";
    private final String USER_INFO_PREF = "User info";
    private final String OBJECT_USER = "Object user";

    private final String NOTIFICATION_MESSAGE_BEFORE = "빨래 할 시간이에요!";
    private final String NOTIFICATION_MESSAGE_AFTER = "빨래 꺼낼 시간이에요!";

    SimpleDateFormat dateTimeFormat;

    View view;
    private LinearLayout mApplyBtn;
    private TextView mApplyText;

    private NotificationAdapter notificationAdapter;

    private User mUser;
    private int mFloor;
    private String nowDate;

    private WashUser washer[][] = {
            {null, null, null},
            {null, null, null},
            {null, null, null}
    };

    private LinearLayout washerLinear[][] = {
            {null, null, null},
            {null, null, null},
            {null, null, null}
    };

    private int washTime;
    private int washerType;
    private boolean isPossibleTime = true;

    private ArrayList<String> mTimes;
    private Set<WashUser> mWashUserSet;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.wash_time_fragment, container, false);

        notificationAdapter = new NotificationAdapter(getActivity(), getContext());
        dateTimeFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());

        initLinear();

        getUserInfo(); // mUser 할당

        setWashingTime();
        setExistingUser();

        mApplyBtn =  view.findViewById(R.id.application_button);
        mApplyText = view.findViewById(R.id.application_text);

        if(isPossibleTime)
            mApplyBtn.setVisibility(View.VISIBLE);
        else
            mApplyBtn.setVisibility(View.INVISIBLE);

        //TODO REMOVE UNDER LINE - just Debugging;
//        mApplyBtn.setVisibility(View.VISIBLE);

        mApplyBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                applyDialog();
            }
        });

        return view;
    }

    // 리니어레이아웃 초기화
    private void initLinear(){
        for(int i = 0; i<washer.length; i++){
            for(int j = 0; j<washer[0].length; j++){

                String buttonID = "washer_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", this.getActivity().getPackageName());
                washerLinear[i][j] =  view.findViewById(resID);
                final int finalI = i;
                final int finalJ = j;
                washerLinear[i][j].setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        showUsingName(finalI, finalJ);
                    }
                });
            }
        }
    }

    // 현재 사용자 객체 get
    private void getUserInfo(){
        SharedPreferences prefs = this.getActivity().getSharedPreferences(USER_INFO_PREF, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(OBJECT_USER, "");
        mUser = gson.fromJson(json, User.class);
//        mFloor = mRoomNumber[mUser.getRoom_num()]/100;
    }

    // set the textview
    private void setWashingTime() {

        TextView firstTimeTv = view.findViewById(R.id.firstTime);
        TextView secondTimeTv = view.findViewById(R.id.secondTime);
        TextView thirdTimeTv = view.findViewById(R.id.thirdTime);

        mTimes = new ArrayList<>();

        mTimes.add("20:00~21:00");
        mTimes.add("21:00~22:00");
        mTimes.add("22:00~23:30");

        switch (getTimeType()){
            case 0:
                mTimes.clear();
                mTimes.add("06:00~07:00");
                mTimes.add("07:00~08:00");
                mTimes.add("08:00~09:00");
                break;
            case 1:
                mTimes.clear();
                mTimes.add("09:00~10:00");
                mTimes.add("10:00~11:00");
                mTimes.add("11:00~12:00");
                break;
            case 2:
                break;
            default:
                break;
        }

        firstTimeTv.setText(mTimes.get(0));
        secondTimeTv.setText(mTimes.get(1));
        thirdTimeTv.setText(mTimes.get(2));

    }

    // 주말 오전 check
    private int getTimeType(){
        Calendar cal = Calendar.getInstance();
        int week = cal.get(Calendar.DAY_OF_WEEK);
        nowDate = cal.get(Calendar.YEAR)+"-"+ (cal.get(Calendar.MONTH) + 1)+"-"+cal.get(Calendar.DAY_OF_MONTH);

        Date firstStartTime = null;
        Date firstEndTime = null;
        Date secondStartTime = null;
        Date secondEndTime = null;
        Date thirdStartTime = null;
        Date thirdEndTime = null;

        Date today = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 20);
        today =  calendar.getTime();
       // Log.e("TODAY", String.valueOf(today));
        try {
            firstStartTime = dateTimeFormat.parse(nowDate+" 05:30");
            firstEndTime = dateTimeFormat.parse(nowDate + " 08:00");
            secondStartTime = dateTimeFormat.parse(nowDate+" 08:00");
            secondEndTime = dateTimeFormat.parse(nowDate + " 11:30");
            thirdStartTime = dateTimeFormat.parse(nowDate+" 19:30");
            thirdEndTime = dateTimeFormat.parse(nowDate + " 23:00");

        } catch (ParseException e) {
            e.printStackTrace();
        }


        if(week == SUNDAY || week == SATURDAY){ // 1 ~ 7 일요일 ~ 토요일
            if(today.after(firstStartTime) && today.before(firstEndTime)){
                return 0;
            }
            else if(today.after(secondStartTime) && today.before(secondEndTime)){
                return 1;
            }else if(today.after(thirdStartTime) && today.before(thirdEndTime)){
                return 2;
            }else {
                isPossibleTime = false;
                return 2;
            }
        }else{

            if(today.after(thirdStartTime)&&today.before(thirdEndTime)){
                return 2;
            }else{
                isPossibleTime = false;
                return 2;
            }
        }
    }

    // 기존 예약자들 표시
    private void setExistingUser(){
        Receiver receiver = new Receiver();
        receiver.execute("http://54.203.113.95/getWashList.php");
    }



    // 신청 다이얼로그
    private void applyDialog() {
        final Dialog dialog = new Dialog(view.getContext(), R.style.MyDialog);

        // TODO REMOVE UNDER LINE - just DEBUGGING;
//        isPossibleTime = true;

        if(isPossibleTime){
            String address = null;
            washTime = -1;
            washerType = -1;

            searchPossibleWasher();
            if(washTime == -1 && washerType == -1){
                dialog.setContentView(R.layout.dialog_style2);
                ((TextView)dialog.findViewById(R.id.dialog_text)).setText("사용가능한 세탁기가 없습니다.");
                dialog.show();
                dialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }else if((address = checkApply())!=null){
                dialog.setContentView(R.layout.dialog_style3);
                ((TextView)dialog.findViewById(R.id.message)).setText("취소하시겠습니까?");

                dialog.show();

                final String finalAddress = address;
                dialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO delete..
                        Log.e(TAG, "DELETE");
                        //Activity mActivity, String urlAddress, int washer_num, int wash_time, String emirim_id, boolean isInsert
                        Sender s = new Sender(view.getContext(), "http://54.203.113.95/updateWashList.php", 0, 0,mUser.getEmirim_id(),false);
                        s.execute();

                        mApplyText.setText("신  청");
                        dialog.dismiss();
                    }
                });

                dialog.findViewById(R.id.dialog_button_no).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }else{
                dialog.setContentView(R.layout.dialog_style3);
                ((TextView)dialog.findViewById(R.id.message)).setText((washerType+1)+"번 세탁기 "
                        +mTimes.get(washTime)+"\n"+mUser.getRoom_num()+"호 "+ mUser.getName()+"\n\n"
                        +"세탁을 예약하시겠습니까?");
                dialog.show();

                final int finalWashTime = washTime;
                final int finalWasherType = washerType;
                dialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO CHANGE
                        washer[finalWashTime][finalWasherType] = new WashUser(-2, -2, finalWashTime, finalWasherType, mUser.getRoom_num()+"호 "+mUser.getName());

                        // insert
                        //Activity mActivity, String urlAddress, int washer_num, int wash_time, String emirim_id, boolean isInsert
                        int washer_num ;
                        if(mUser.getRoom_num()/100 == 4)
                            washer_num = finalWasherType;
                        else
                            washer_num = finalWasherType+3;

                        Log.e(TAG, "INSERT");

                        Sender s = new Sender(view.getContext(), "http://54.203.113.95/updateWashList.php", washer_num, getTimeType()*3+finalWasherType,mUser.getEmirim_id(),true);
                        s.execute();

                        mApplyText.setText("취  소");

                        setAlarm();
                        dialog.dismiss();

                    }
                });

                dialog.findViewById(R.id.dialog_button_no).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }
        }else{
            dialog.setContentView(R.layout.dialog_style2);
            ((TextView)dialog.findViewById(R.id.dialog_text)).setText("세탁기 사용시간이 아닙니다.");
            dialog.show();
            dialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }

    }

    private void setAlarm() {
        String alarmTime[] = mTimes.get(washTime).split("~");

        Date beforeTime = null;
        Date afterTime = null;
        try {
            beforeTime = dateTimeFormat.parse(nowDate+" "+alarmTime[0]);
            afterTime = dateTimeFormat.parse(nowDate+" "+alarmTime[1]);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        notificationAdapter.setNotification(NOTIFICATION_MESSAGE_BEFORE, beforeTime);
        notificationAdapter.setNotification(NOTIFICATION_MESSAGE_AFTER, afterTime);
    }

    // 이전에 신청했는지 check
    private String checkApply() {

        Iterator<WashUser> iterator = mWashUserSet.iterator();

        while(iterator.hasNext()){
            if(iterator.next().getUser().equals(mUser.getRoom_num()+"호 "+mUser.getName())){
                mApplyText.setText("취  소");
                return mUser.toString();
            }
        }

        return null;
    }

    // 가능한 세탁기 탐색
    private boolean searchPossibleWasher() {
        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.

        Date date = new Date(now);

        SimpleDateFormat sdfNow = new SimpleDateFormat("HH");
        int time = Integer.parseInt(sdfNow.format(new Date(System.currentTimeMillis())));

        int start = 0;
        if(time == 6 || time == 9 || time == 20)
            start = 1;
        else if(time == 7 || time == 10 || time == 21)
            start = 3;
        else if(time == 8 || time == 11 || time == 22)
            start = 3;


        for(int i = start; i<washer.length; i++){
            for(int j = 0; j<washer.length; j++){
                if(washer[j][i]==null){
                    washTime = i;
                    washerType = j;
                    return true;
                }
            }
        }
        washTime = -1;
        washerType = -1;
        return false;
    }

    // 사용자 show dialog
    private void showUsingName(int i , int j){
        if(washer[i][j] == null)
            return;

        final Dialog dialog = new Dialog(view.getContext(), R.style.MyDialog);
        dialog.setContentView(R.layout.dialog_style2);
        String str;


        str = washer[i][j].getUser();
        if(washer[i][j].getWash_id() == -1){
            str += "호에서 ";
        }else{
            str+=" 학생이 ";
        }

        ((TextView) dialog.findViewById(R.id.dialog_text)).setText(str + "사용중입니다.");

        dialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //convert dp to pixel
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    // 세탁일지를 받아옴
    class Receiver extends AsyncTask<String, Integer, String> {

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
                mWashUserSet = new HashSet<WashUser>();

                JSONArray existingUser = new JSONObject(str).getJSONArray("wash_existing_user");
                for(int i = 0; i<existingUser.length(); i++){
                    JSONObject job = existingUser.getJSONObject(i);
                    //int wash_id, int wash_day, int wash_time, int washer_num, String user
                    mWashUserSet.add(new WashUser(job.optInt("wash_id", -1), job.getInt("wash_day"), job.getInt("wash_time"), job.getInt("washer_num"), job.getString("using_room")));
                }

                JSONArray applyingUser = new JSONObject(str).getJSONArray("wash_applying_user");
                for(int i = 0; i<applyingUser.length(); i++){
                    JSONObject job = applyingUser.getJSONObject(i);
                    mWashUserSet.add(new WashUser(job.getInt("wash_id"), job.getInt("wash_day"), job.getInt("wash_time"), job.getInt("washer_num"), job.getString("emirim_id")));
                }


               // Log.e(TAG, mWashUserSet.toString());
            } catch (JSONException e) {

                Log.e(TAG, "에러 : "+e.toString());
            }
            appearWashUsers();
        }

    }

    // 화면에 예약된 사용자를 띄워줌
    private void appearWashUsers() {
        Iterator<WashUser> iterator = mWashUserSet.iterator();

        for(int i = 0; i<washer.length; i++){
            for(int j = 0; j<washer[i].length; j++){
                washer[i][j] = null;
            }
        }

        while(iterator.hasNext()){
            WashUser washUser = iterator.next();

            int floor = mUser.getRoom_num()/100;
            if((floor == 4 && washUser.getWasher_num()>2)||(floor==5 && washUser.getWasher_num()<3))
                continue;

            int timeType = getTimeType();
            // timeType : 0 (06:00~09:00 [0~2]) / 1 (09:00~12:00 [3~5]) / 2 (20:00~23:30 [6~8])
            if((timeType == 0 && washUser.getWash_time() > 2) || (timeType==1 && (washUser.getWash_time() <3 || washUser.getWash_time()>5)) || (timeType == 2 && washUser.getWash_time() < 6))
                continue;

//            washer[i][j] = timeType.child(childName + i).child(String.valueOf(j)).getValue(User2.class);
            washer[washUser.getWasher_num()%3][washUser.getWash_time()%3] = washUser;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) washerLinear[washUser.getWasher_num()%3][washUser.getWash_time()%3].getLayoutParams();
            params.height = dpToPx(7);
            washerLinear[washUser.getWasher_num()%3][washUser.getWash_time()%3].setLayoutParams(params);
            washerLinear[washUser.getWasher_num()%3][washUser.getWash_time()%3].setBackgroundColor(Color.parseColor("#9eaec5"));
        }

    }

    // 세탁 신청
    class Sender extends AsyncTask<Void,Void,String> {

        Context mActivity;

        String urlAddress;

        private int washer_num;
        private int wash_time;
        private String emirim_id;
        private boolean isInsert;
        /*
            1.OUR CONSTRUCTOR
            2.RECEIVE CONTEXT,URL ADDRESS AND EDITTEXTS FROM OUR MAINACTIVITY
        */

        public Sender(Context mActivity, String urlAddress, int washer_num, int wash_time, String emirim_id, boolean isInsert ) {
            this.mActivity = mActivity;
            this.urlAddress = urlAddress;
            this.washer_num = washer_num;
            this.wash_time = wash_time;
            this.emirim_id = emirim_id;
            this.isInsert = isInsert;
        }

        /*
   1.SHOW PROGRESS DIALOG WHILE DOWNLOADING DATA
    */

        /*
        1.WHERE WE SEND DATA TO NETWORK
        2.RETURNS FOR US A STRING
         */
        @Override
        protected String doInBackground(Void... params) {
            return this.send();
        }

        /*
      1. CALLED WHEN JOB IS OVER
      2. WE DISMISS OUR PD
      3.RECEIVE A STRING FROM DOINBACKGROUND
       */
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if(response != null)
            {
                //SUCCESS
                Toast.makeText(mActivity.getApplicationContext(),"요청이 성공적으로 처리되었습니다.",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mActivity, MainActivity.class);
                mActivity.startActivity(intent);

            }else
            {
                //NO SUCCESS
                Toast.makeText(mActivity.getApplicationContext(),"요청을 처리하는데 문제가 발생하였습니다.\n다시 시도해주세요.",Toast.LENGTH_LONG).show();
            }
        }

        /*
        SEND DATA OVER THE NETWORK
        RECEIVE AND RETURN A RESPONSE
         */
        private String send()
        {
            //CONNECT
            HttpURLConnection con= Connector.connect(urlAddress);

            if(con==null)
            {
                return null;
            }

            try
            {
                OutputStream os=con.getOutputStream();

                //WRITE
                BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));

                bw.write(new UserDataPacker(washer_num, wash_time, emirim_id, isInsert).packUserData());

                bw.flush();

                //RELEASE RES
                bw.close();
                os.close();

                //HAS IT BEEN SUCCESSFUL?
                int responseCode=con.getResponseCode();

                if(responseCode==con.HTTP_OK)
                {
                    //GET EXACT RESPONSE
                    BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuffer response=new StringBuffer();

                    String line;

                    //READ LINE BY LINE
                    while ((line=br.readLine()) != null)
                    {
                        response.append(line);
                    }

                    //RELEASE RES
                    br.close();

                    return response.toString();

                }else
                {

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    // 서버에 보낼 데이터를 가공
    class UserDataPacker {
        private int washer_num;
        private int wash_time;
        private String emirim_id;
        private boolean isInsert;


        public UserDataPacker(int washer_num, int wash_time, String emirim_id, boolean isInsert) {
            this.washer_num = washer_num;
            this.wash_time = wash_time;
            this.emirim_id = emirim_id;
            this.isInsert = isInsert;
        }

        public String packUserData(){
            JSONObject jo = new JSONObject();
            StringBuffer packedData = new StringBuffer();

            try{
                jo.put("washer_num", washer_num);
                jo.put("wash_time", wash_time);
                jo.put("emirim_id", emirim_id);
                jo.put("isInsert", isInsert);

                Boolean firstValue = true;

                Iterator it = jo.keys();

                do{
                    String key = it.next().toString();
                    String value = jo.get(key).toString();

                    if(firstValue){
                        firstValue = false;
                    }else{
                        packedData.append("&");
                    }

                    packedData.append(URLEncoder.encode(key, "UTF-8"));
                    packedData.append("=");
                    packedData.append(URLEncoder.encode(value, "UTF-8"));
                }while(it.hasNext());

                return packedData.toString();
            }catch(JSONException e){
                e.printStackTrace();
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }

            return null;
        }
    }

}

