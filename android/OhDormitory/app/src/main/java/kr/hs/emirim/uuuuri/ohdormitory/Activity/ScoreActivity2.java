//package kr.hs.emirim.uuuuri.ohdormitory.Activity;
//
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.widget.TextView;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//
//import kr.hs.emirim.uuuuri.ohdormitory.Adapter.ScoreListAdapater;
//import kr.hs.emirim.uuuuri.ohdormitory.Model.Score;
//import kr.hs.emirim.uuuuri.ohdormitory.Model.StudentScore;
//import kr.hs.emirim.uuuuri.ohdormitory.R;
//
//public class ScoreActivity2 extends BaseActivity {
//
//    private FirebaseDatabase mDatabase;
//
//    private RecyclerView mRecyclerView;
//    private RecyclerView.Adapter mAdapter;
//    private RecyclerView.LayoutManager mLayoutManager;
//    private ArrayList<StudentScore> studentScores;
//
//    private DatabaseReference mScoreRefer;
//    private ValueEventListener mScoreListener;
//
//    private double mMinusScore;
//    private double mPlusScore;
//    private double mTotalScore;
//
//    private TextView mGuideText;
//    private TextView mMinusText;
//    private TextView mPlusText;
//    private TextView mTotalText;
//
//    private HashMap<Integer, Score> mScoreHashMap;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_score);
//        initScoreMap();
//        mDatabase = FirebaseDatabase.getInstance();
//
//        mRecyclerView = (RecyclerView)findViewById(R.id.recycler);
//
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
//        mRecyclerView.setHasFixedSize(true);
//
//        // use a linear layout manager
//        mLayoutManager = new LinearLayoutManager(this);
//
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        studentScores = new ArrayList<StudentScore>();
//        mAdapter = new ScoreListAdapater(studentScores, mScoreHashMap);
//        mRecyclerView.setAdapter(mAdapter);
//
//        mGuideText = findViewById(R.id.guideMessage);
//        mPlusText = findViewById(R.id.plus);
//        mMinusText = findViewById(R.id.minus);
//        mTotalText = findViewById(R.id.total);
//
//        readScore();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        if (mScoreListener != null) {
//            mScoreRefer.removeEventListener(mScoreListener);
//        }
//    }
//
//    private void initScoreMap(){
//        mScoreHashMap = new HashMap<>();
//        mScoreHashMap.put(0, new Score("타호실 무단 취침", -5.0));
//        mScoreHashMap.put(1, new Score("취침시간 소란", -5.0));
//        mScoreHashMap.put(2, new Score("무단외출", -5.0));
//        mScoreHashMap.put(3, new Score("무단 입실", -5.0));
//        mScoreHashMap.put(4, new Score("무단 외박", -5.0));
//        mScoreHashMap.put(5, new Score("외부인 무단 출입", -5.0));
//        mScoreHashMap.put(6, new Score("사감통제 불이행", -5.0));
//        mScoreHashMap.put(7, new Score("태도 불손 및 불량", -5.0));
//        mScoreHashMap.put(8, new Score("언어폭력 또는 따돌림", -5.0));
//        mScoreHashMap.put(9, new Score("무단 지각", -3.0 ));
//        mScoreHashMap.put(10, new Score("기숙사내 각종 소란 행위", -3.0));
//        mScoreHashMap.put(11, new Score("허가되지 않은 전열⸱전자기기 사용", -3.0));
//        mScoreHashMap.put(12, new Score("불건전 물품 반입", -3.0));
//        mScoreHashMap.put(13, new Score("시설물 고의 파손", -3.0));
//        mScoreHashMap.put(14, new Score("타호실 무단 출입", -2.0));
//        mScoreHashMap.put(15, new Score("당번 활동 불량", -2.0));
//        mScoreHashMap.put(16, new Score("외부 음식물 반입", -2.0));
//        mScoreHashMap.put(17, new Score("호실원에게 피해", -2.0));
//        mScoreHashMap.put(18, new Score("인사예절 불량", -2.0));
//        mScoreHashMap.put(19, new Score("선⸱후배 질서 문란", -2.0));
//        mScoreHashMap.put(20, new Score("복도⸱호실에서 쓰레기 투기", -2.0));
//        mScoreHashMap.put(21, new Score("창밖으로 쓰레기 투척", -2.0));
//        mScoreHashMap.put(22, new Score("개인쓰레기 공동구역에 무단 투기", -2.0));
//        mScoreHashMap.put(23, new Score("중앙통로 침입", -2.0));
//        mScoreHashMap.put(24, new Score("점호 불참 및 점호 시 태도 불량", -1.0));
//        mScoreHashMap.put(25, new Score("취침시간 소등위반", -1.0));
//        mScoreHashMap.put(26, new Score("공동구역 사용시간 위반", -1.0));
//        mScoreHashMap.put(27, new Score("기숙사내 실내화 미착용", -1.0));
//        mScoreHashMap.put(28, new Score("복장 및 두발 불량", -1.0));
//        mScoreHashMap.put(29, new Score("교문 밖 출입 시 용의 불량", -1.0));
//        mScoreHashMap.put(30, new Score("개인위생 불결", -1.0));
//        mScoreHashMap.put(31, new Score("개인 세탁물 및 소지품 관리 불량", -1.0));
//        mScoreHashMap.put(32, new Score("호실 청소상태 불량", -1.0));
//        mScoreHashMap.put(33, new Score("외박증 미인증", -1.0));
//        mScoreHashMap.put(34, new Score("퇴실시간 위반", -1.0));
//        mScoreHashMap.put(35, new Score("아침점호 후 재입실", -1.0));
//        mScoreHashMap.put(36, new Score("공동구역 모범 청소", 0.5));
//        mScoreHashMap.put(37, new Score("호실청소", 0.5));
//        mScoreHashMap.put(38, new Score("기타 가산점", 0.5));
//        mScoreHashMap.put(39, new Score("층장 활동", 1.0));
//        mScoreHashMap.put(40, new Score("기숙사 행사 도움", 1.0));
//
//    }
//    private void readScore(){
//        showProgressDialog();
//
//        mScoreRefer = mDatabase.getReference("score/"+getUid()); // get database reference
//
//        mScoreListener = mScoreRefer.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot uid) {
//                studentScores.clear();
//                mPlusScore = 0;
//                mMinusScore = 0;
//                mTotalScore = 0;
//                Iterator<DataSnapshot> uidchildIterator = uid.getChildren().iterator();
//                while(uidchildIterator.hasNext()){
//                    DataSnapshot timeStamp = uidchildIterator.next();
//                    String date = timeStamp.child("date").getValue(String.class);
//                    int type = timeStamp.child("score").getValue(Integer.class);
//
//                    studentScores.add(new StudentScore(date, type));
//                    double score = mScoreHashMap.get(type).getScore();
//                    if(score < 0)
//                        mMinusScore += score;
//                    else
//                        mPlusScore += score;
//
//                }
//                mTotalScore = mMinusScore + mPlusScore;
//
//                if(studentScores.size() == 0){
//                    mGuideText.setVisibility(View.VISIBLE);
//                    mPlusText.setVisibility(View.INVISIBLE);
//                    mMinusText.setVisibility(View.INVISIBLE);
//                    mTotalText.setVisibility(View.INVISIBLE);
//
//                }else{
//                    mGuideText.setVisibility(View.INVISIBLE);
//
//                    mPlusText.setVisibility(View.VISIBLE);
//                    mMinusText.setVisibility(View.VISIBLE);
//                    mTotalText.setVisibility(View.VISIBLE);
//                    mPlusText.setText("상점 : "+mPlusScore+" 점");
//                    mMinusText.setText("벌점 : "+mMinusScore+" 점");
//                    mTotalText.setText("총 "+ mTotalScore+" 점");
//                }
//
//
//                mAdapter.notifyDataSetChanged();
//                hideProgressDialog();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    /**
// *
// *  Firebase ref = new Firebase("<my-firebase-app>/names"):
// String[] names = {"John","Tim","Sam","Ben"};
// List nameList = new ArrayList<String>(Arrays.asList(names));
// // Now set value with new nameList
// ref.setValue(nameList);
// */
//
//}
