package kr.hs.emirim.uuuuri.ohdormitory.Fragment;


import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kr.hs.emirim.uuuuri.ohdormitory.Adapter.NotificationAdapter;
import kr.hs.emirim.uuuuri.ohdormitory.Model.Day;
import kr.hs.emirim.uuuuri.ohdormitory.Model.User;
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
    private int mRoomNumber[];
    private String nowDate;

    private User washer[][] = {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.wash_time_fragment, container, false);

        notificationAdapter = new NotificationAdapter(getActivity(), getContext());
        dateTimeFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());

        mRoomNumber = new int[]{401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 418,
                501, 502, 503, 504, 505, 506, 507, 508, 509, 510, 511, 512, 513, 514, 515, 516, 517, 518, 519};

        initLinear();



        getUserInfo(); // mUser 할당

        setWashingTime();
        setWeekDayUser();
        setWasherUser();

        mApplyBtn =  view.findViewById(R.id.application_button);
        mApplyText = view.findViewById(R.id.application_text);

        if(isPossibleTime)
            mApplyBtn.setVisibility(View.VISIBLE);
        else
            mApplyBtn.setVisibility(View.INVISIBLE);


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

// TODO: 2017-10-22 : TEST CODE
//        Date today = new Date(System.currentTimeMillis());
        Date today = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 20);
        today =  calendar.getTime();
        Log.e("TODAY", String.valueOf(today));
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

        return 2;
        //// TODO: 2017-10-23  TEST [끝나고 주석 지우셈]
//        if(week == SUNDAY || week == SATURDAY){ // 1 ~ 7 일요일 ~ 토요일
//            if(today.after(firstStartTime) && today.before(firstEndTime)){
//                return 0;
//            }
//            else if(today.after(secondStartTime) && today.before(secondEndTime)){
//                return 1;
//            }else if(today.after(thirdStartTime) && today.before(thirdEndTime)){
//                return 2;
//            }else {
//                isPossibleTime = false;
//                return 2;
//            }
//        }else{
//
//            if(today.after(thirdStartTime)&&today.before(thirdEndTime)){
//                return 2;
//            }else{
//                isPossibleTime = false;
//                return 2;
//            }
//        }
    }

    //주중 사용자 set
    private void setWeekDayUser() {
//        Calendar cal = Calendar.getInstance();
//        int week = cal.get(Calendar.DAY_OF_WEEK);
//
//        DatabaseReference timeTypeRef = mDatabase.getReference().child("wash-time").child(nowDate).child("floor_"+mFloor)
//                .child("type_"+getTimeType());
//
//        if(mFloor == 4){
//            switch(week){
//                case MONDAY:
//                    timeTypeRef.child("washer_0").child("1").setValue(new User2(401));
//                    timeTypeRef.child("washer_0").child("2").setValue(new User2(404));
//
//                    timeTypeRef.child("washer_1").child("1").setValue(new User2(402));
//
//                    timeTypeRef.child("washer_2").child("1").setValue(new User2(403));
//                    timeTypeRef.child("washer_2").child("2").setValue(new User2(414));
//                    break;
//                case TUESDAY:
//                    timeTypeRef.child("washer_0").child("1").setValue(new User2(405));
//                    timeTypeRef.child("washer_0").child("2").setValue(new User2(408));
//
//                    timeTypeRef.child("washer_1").child("1").setValue(new User2(406));
//
//                    timeTypeRef.child("washer_2").child("1").setValue(new User2(407));
//                    timeTypeRef.child("washer_2").child("2").setValue(new User2(415));
//                    break;
//                case WEDNESDAY:
//                    timeTypeRef.child("washer_0").child("1").setValue(new User2(409));
//                    timeTypeRef.child("washer_0").child("2").setValue(new User2(412));
//
//                    timeTypeRef.child("washer_1").child("1").setValue(new User2(410));
//
//                    timeTypeRef.child("washer_2").child("1").setValue(new User2(411));
//                    timeTypeRef.child("washer_2").child("2").setValue(new User2(416));
//                    break;
//                case THURSDAY:
//                    timeTypeRef.child("washer_0").child("1").setValue(new User2(413));
//
//                    timeTypeRef.child("washer_1").child("2").setValue(new User2(417));
//
//                    timeTypeRef.child("washer_2").child("2").setValue(new User2(418));
//
//                    break;
//                case SATURDAY:
//                case SUNDAY:
//                case FRIDAY:
//                default:
//                        break;
//            }
//        }else if(mFloor == 5){
//            switch(week){
//                case MONDAY:
//                    timeTypeRef.child("washer_0").child("1").setValue(new User2(508));
//                    timeTypeRef.child("washer_0").child("2").setValue(new User2(501));
//
//                    timeTypeRef.child("washer_1").child("1").setValue(new User2(509));
//                    timeTypeRef.child("washer_1").child("2").setValue(new User2(510));
//
//                    timeTypeRef.child("washer_2").child("2").setValue(new User2(502));
//                    break;
//                case TUESDAY:
//                    timeTypeRef.child("washer_0").child("1").setValue(new User2(511));
//                    timeTypeRef.child("washer_0").child("2").setValue(new User2(503));
//
//                    timeTypeRef.child("washer_1").child("1").setValue(new User2(512));
//                    timeTypeRef.child("washer_1").child("2").setValue(new User2(513));
//
//                    timeTypeRef.child("washer_2").child("2").setValue(new User2(504));
//                    break;
//                case WEDNESDAY:
//                    timeTypeRef.child("washer_0").child("1").setValue(new User2(514));
//                    timeTypeRef.child("washer_0").child("2").setValue(new User2(505));
//
//                    timeTypeRef.child("washer_1").child("1").setValue(new User2(515));
//                    timeTypeRef.child("washer_1").child("2").setValue(new User2(516));
//
//                    timeTypeRef.child("washer_2").child("2").setValue(new User2(506));
//                    break;
//                case THURSDAY:
//                    timeTypeRef.child("washer_0").child("2").setValue(new User2(507));
//
//                    timeTypeRef.child("washer_1").child("1").setValue(new User2(517));
//                    timeTypeRef.child("washer_1").child("2").setValue(new User2(518));
//
//                    timeTypeRef.child("washer_2").child("2").setValue(new User2(519));
//                    break;
//                case FRIDAY:
//                case SATURDAY:
//                case SUNDAY:
//                default:
//                    break;
//            }
//        }
    }

    // 주중 사용자 외 사용자 set - firebase listener
    private void setWasherUser(){
//        Log.e(TAG, "wash-time/"+nowDate+"/floor_"+mFloor+"/type_"+getTimeType());
//        DatabaseReference mWasherRef = mDatabase.getReference("wash-time/" + nowDate + "/floor_" + mFloor + "/type_" + getTimeType());
//        ValueEventListener washListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot timeType) {
//                String childName = "washer_";
//
//                for(int i = 0; i<washer.length; i++)
//                    for (int j = 0; j < washer[i].length; j++) {
//                        if (timeType.child(childName + i).child(String.valueOf(j)).getValue(User.class) == null) {
//                            Log.e(TAG, "NULL?");
//                            washer[i][j] = null;
//                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) washerLinear[i][j].getLayoutParams();
//                            params.height = dpToPx(3);
//                            washerLinear[i][j].setLayoutParams(params);
//                            washerLinear[i][j].setBackgroundColor(Color.parseColor("#757575"));
//                        } else {
//                            washer[i][j] = timeType.child(childName + i).child(String.valueOf(j)).getValue(User2.class);
//                            if(washer[i][j].toString().equals(mUser.toString())&&isPossibleTime==true) {
//                                mApplyText.setText("취  소");
//                            }
//                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) washerLinear[i][j].getLayoutParams();
//                            params.height = dpToPx(7);
//                            washerLinear[i][j].setLayoutParams(params);
//                            washerLinear[i][j].setBackgroundColor(Color.parseColor("#9eaec5"));
//                        }
//                    }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        };
//
//        mWasherRef.addValueEventListener(washListener);
    }

    // 신청 다이얼로그
    private void applyDialog() {
//        final Dialog dialog = new Dialog(view.getContext(), R.style.MyDialog);
//        if(isPossibleTime){
//            String address = null;
//            washTime = -1;
//            washerType = -1;
//
//            searchPossibleWasher();
//            if(washTime == -1 && washerType == -1){
//                dialog.setContentView(R.layout.dialog_style2);
//                ((TextView)dialog.findViewById(R.id.dialog_text)).setText("사용가능한 세탁기가 없습니다.");
//                dialog.show();
//                dialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialog.dismiss();
//                    }
//                });
//
//            }else if((address = checkApply())!=null){
//                dialog.setContentView(R.layout.dialog_style3);
//                ((TextView)dialog.findViewById(R.id.message)).setText("취소하시겠습니까?");
//
//                dialog.show();
//
//                final String finalAddress = address;
//                dialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mCancelRef = mDatabase.getReference(finalAddress);
//                        mCancelRef.setValue(null);
//                        mApplyText.setText("신  청");
//                        dialog.dismiss();
//                    }
//                });
//
//                dialog.findViewById(R.id.dialog_button_no).setOnClickListener(new View.OnClickListener(){
//                    @Override
//                    public void onClick(View view) {
//                        dialog.dismiss();
//                    }
//                });
//            }else{
//                dialog.setContentView(R.layout.dialog_style3);
//                ((TextView)dialog.findViewById(R.id.message)).setText((washerType+1)+"번 세탁기 "
//                        +mTimes.get(washTime)+"\n"+mRoomNumber[mUser.getRoomNumber()]+"호 "+ mUser.getName()+"\n\n"
//                        +"세탁을 예약하시겠습니까?");
//                dialog.show();
//
//                final int finalWashTime = washTime;
//                final int finalWasherType = washerType;
//                dialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        washer[finalWashTime][finalWasherType] = mUser;
//
//                        mInputRef = mDatabase.getReference();
//                        mInputRef.child("wash-time").child(nowDate).child("floor_"+mFloor)
//                                .child("type_"+getTimeType()).child("washer_"+finalWasherType)
//                                .child(String.valueOf(finalWashTime)).setValue(mUser);
//
//                        setAlarm();
//                        dialog.dismiss();
//
//                    }
//                });
//
//                dialog.findViewById(R.id.dialog_button_no).setOnClickListener(new View.OnClickListener(){
//                    @Override
//                    public void onClick(View view) {
//                        dialog.dismiss();
//                    }
//                });
//
//            }
//        }else{
//            dialog.setContentView(R.layout.dialog_style2);
//            ((TextView)dialog.findViewById(R.id.dialog_text)).setText("세탁기 사용시간이 아닙니다.");
//            dialog.show();
//            dialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog.dismiss();
//                }
//            });
//        }
//
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

        String address = "wash-time/" + nowDate + "/floor_" + mFloor + "/type_" + getTimeType();
        String childName = "washer_";

        for(int i = 0; i<washer.length; i++){
            for(int j = 0; j<washer[0].length; j++){

                if (washer[i][j] != null && washer[i][j].toString().equals(mUser.toString())) {
                    Log.e(TAG, washer[i][j].equals(mUser)+"");
                    return address+"/"+childName+i+"/"+j;
                }
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
        Log.e(TAG, date.getTime()+"/"+Long.valueOf(date.getTime()).getClass().getName());
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
        if(washer[i][j].getRoom_num() > mRoomNumber.length)
            str = washer[i][j].getRoom_num() + "호";
        else
            str = mRoomNumber[washer[i][j].getRoom_num()] + "호";

        if (washer[i][j].getName() != null)
            str += " " + washer[i][j].getName() + " 학생이 ";
        else
            str += "에서 ";
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

}

