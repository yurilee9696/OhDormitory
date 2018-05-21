//package kr.hs.emirim.uuuuri.ohdormitory.Adapter;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.support.v7.widget.CardView;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.util.ArrayList;
//import java.util.StringTokenizer;
//
//import kr.hs.emirim.uuuuri.ohdormitory.Activity.NoticeCleanDetailActivity;
//import kr.hs.emirim.uuuuri.ohdormitory.Activity.NoticeDetailActivity;
//import kr.hs.emirim.uuuuri.ohdormitory.Model.Notice2;
//import kr.hs.emirim.uuuuri.ohdormitory.Model.SleepOut;
//import kr.hs.emirim.uuuuri.ohdormitory.R;
//
///**
// * Created by doori on 2017-10-01.
// */
//
//public class NoticeListAdapter2 extends RecyclerView.Adapter<NoticeListAdapter2.ViewHolder> {
//    private final String NOTICE_PREFERENCE = "NOTICE PREFERENCE";
//    private final String PUT_EXTRA_NOTICE = "NOTICE_ITEM";
//    private final String BEFORE_PHONE_NUMBER = "BEFORE PHONE NUMBER";
//    private final String BEFORE_SLEEP_TYPE = "BEFORE SLEEP TYPE";
//
//    private final String SLEEP_OUT_ARRAY[] = {"잔류", "금요외박", "토요외박"};
//    private final String FRONT_NUMBER_ARRAY[] = {"010", "02", "070"};
//
//    private ArrayList<Notice2> mDataset;
//
//    private String mSleepOut;
//    private int mSleepOutPosition;
//    private String mFrontNumber;
//
//    private FirebaseDatabase mDatabase;
//    private DatabaseReference mInputUserRefer;
//
//    Spinner mSleepOutSpinner;
//    Spinner mFrontNumberSpinner;
//    private EditText mMidNumberEt;
//    private EditText mRearNumberEt;
//
//    SharedPreferences sharedPreferences;
//    SharedPreferences.Editor editor;
//
//
//    // Provide a reference to the views for each data item
//    // Complex data items may need more than one view per item, and
//    // you provide access to all the views for a data item in a view holder
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        // each data item is just a string in this case
//        TextView mTextView_content;
//        TextView mTextView_time;
//        LinearLayout cardTypeColor;
//        CardView mMyCardView;
//        public ViewHolder(View view) {
//            super(view);
//            mTextView_content = (TextView)view.findViewById(R.id.textview_content);
//            mTextView_time= (TextView)view.findViewById(R.id.textview_time);
//            cardTypeColor = (LinearLayout)view.findViewById(R.id.card_type);
//            mMyCardView = (CardView)view.findViewById(R.id.myCardView);
//        }
//    }
//
//    // Provide a suitable constructor (depends on the kind of dataset)
//    public NoticeListAdapter2(ArrayList<Notice2> noticeCardViewDataset) {
//        mDataset = noticeCardViewDataset;
//    }
//
//    // Create new views (invoked by the layout manager)
//    @Override
//    public NoticeListAdapter2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        //recycler view에 반복될 아이템 레이아웃 연결
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice,null);
//        return new ViewHolder(v);
//
//    }
//
//    // Replace the contents of a view (invoked by the layout manager)
//    @Override
//    public void onBindViewHolder(ViewHolder holder, final int position) {
//        // - get element from your dataset at this position
//        // - replace the contents of the view with that element
//        final String kind = mDataset.get(position).getNotice_kind();
//        if (kind.equals("공지사항")){
//            holder.cardTypeColor.setBackgroundColor(0xff9cb1c2);
//        }else if(kind.equals("청소구역")){
//            holder.cardTypeColor.setBackgroundColor(0xff7bc792);
//        }else if(kind.equals("외박일지")){
//            holder.cardTypeColor.setBackgroundColor(0xffe36363);
//        }
//
//        Log.e("TAG", mDataset.get(position).getW_time()+"/"+mDataset.get(position).getD_time());
//
//        holder.mTextView_content.setText(mDataset.get(position).getNotice_title());
//        if(mDataset.get(position).getW_time().equals("0") &&mDataset.get(position).getD_time().equals("0") )
//            holder.mTextView_time.setText("항시공지");
//        else if(mDataset.get(position).getW_time().equals(mDataset.get(position).getD_time()))
//            holder.mTextView_time.setText(mDataset.get(position).getW_time());
//        else
//            holder.mTextView_time.setText(mDataset.get(position).getW_time()+" - "+mDataset.get(position).getD_time());
//
//        holder.mMyCardView.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//
//                if(kind.equals("공지사항")) {
//                    Intent intent = new Intent(view.getContext(), NoticeDetailActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable(PUT_EXTRA_NOTICE, mDataset.get(position));
//                    intent.putExtras(bundle);
//                    view.getContext().startActivity(intent);
//                }else if(kind.equals("청소구역")){
//                    Intent intent = new Intent(view.getContext(), NoticeCleanDetailActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable(PUT_EXTRA_NOTICE, mDataset.get(position));
//                    intent.putExtras(bundle);
//                    view.getContext().startActivity(intent);
//                }
//                else if(kind.equals("외박일지")){
//                    showDialog(view, mDataset.get(position).getSleep_w_time(),mDataset.get(position).getSleep_d_time());
//                }
//
//            }
//        });
//    }
//
//    // Return the size of your dataset (invoked by the layout manager)
//    @Override
//    public int getItemCount() {
//        return mDataset.size();
//    }
//
//    private void showDialog(View view, final String wTime, final String dTime){
//
//        mDatabase = FirebaseDatabase.getInstance();
//
//        final Dialog mDialog = new Dialog(view.getContext(), R.style.MyDialog);
//        mDialog.setContentView(R.layout.application_form);
//
//        sharedPreferences = view.getContext().getSharedPreferences(NOTICE_PREFERENCE, Context.MODE_PRIVATE);
//
//        TextView sleepWTime = mDialog.findViewById(R.id.sleep_w_time);
//        TextView sleepDTime = mDialog.findViewById(R.id.sleep_d_time);
//
//        sleepWTime.setText(wTime);
//        sleepDTime.setText(dTime);
//
//        mSleepOutSpinner = mDialog.findViewById(R.id.sleep_out);
//        mSleepOutSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                mSleepOut = SLEEP_OUT_ARRAY[position];
//                mSleepOutPosition = position;
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                mSleepOut = SLEEP_OUT_ARRAY[0];
//            }
//        });
//
//        mFrontNumberSpinner = mDialog.findViewById(R.id.frontNumber);
//        mFrontNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                mFrontNumber = FRONT_NUMBER_ARRAY[position];
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                mFrontNumber = FRONT_NUMBER_ARRAY[0];
//            }
//        });
//
//        mMidNumberEt = mDialog.findViewById(R.id.midNumber);
//        mRearNumberEt = mDialog.findViewById(R.id.rearNumber);
//
//        setBeforeNumber();
//
//        mDialog.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                if (!validateForm())
//                    return;
//
//                final Dialog mCheckDialog = new Dialog(view.getContext(), R.style.MyDialog);
//                mCheckDialog.setContentView(R.layout.dialog_style3);
//                ((TextView)mCheckDialog.findViewById(R.id.message)).setText("외박일지를 제출하시겠습니까?");
//                mCheckDialog.show();
//                mCheckDialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        String phoneNumber = mFrontNumber +"-"+ mMidNumberEt.getText().toString()+"-"+ mRearNumberEt.getText().toString();
//                        // phoneNumber, dtime, wtime, sleep type
//                        SleepOut sleepOut = new SleepOut(phoneNumber, mSleepOut,"false");
//
//                        mInputUserRefer = mDatabase.getReference();
//                        Log.e("기간",wTime+"-"+dTime);
//                        String w_Time= wTime.replace(".","-");
//                        String d_Time=dTime.replace(".","-");
//                        mInputUserRefer.child("sleep-out").child(w_Time+"-"+d_Time)
//                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(sleepOut); // update the firebase database
//                        editor = sharedPreferences.edit();
//                        Log.e("TAG", phoneNumber);
//                        if(!phoneNumber.equals("010--"))
//                            editor.putString(BEFORE_PHONE_NUMBER, phoneNumber); //First라는 key값으로 infoFirst 데이터를 저장한다.
//                        editor.putInt(BEFORE_SLEEP_TYPE, mSleepOutPosition);
//                        editor.putString("recognize", "false");
//                        editor.commit(); //완료한다.
//
//
//                        mCheckDialog.dismiss();
//                        mDialog.dismiss();
//                    }
//                });
//
//                mCheckDialog.findViewById(R.id.dialog_button_no).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mCheckDialog.dismiss();
//                    }
//                });
//                return;
//            }
//        });
//        mDialog.show();
//    }
//
//    private boolean validateForm() {
//        boolean valid = true;
//        if(mSleepOutPosition==0)
//            return valid;
//
//
//        String midNumber = mMidNumberEt.getText().toString();
//        if ((TextUtils.isEmpty(midNumber))||(Integer.parseInt(midNumber) < 100 || Integer.parseInt(midNumber) >= 10000)) {
//            mMidNumberEt.setError("Required.");
//            valid = false;
//        }else{
//            mMidNumberEt.setError(null);
//        }
//
//        String rearNumber = mRearNumberEt.getText().toString();
//        if ((TextUtils.isEmpty(rearNumber))||(Integer.parseInt(rearNumber) < 1000 ||Integer.parseInt(rearNumber) >= 10000)) {
//            mRearNumberEt.setError("Required.");
//            valid = false;
//        }else {
//            mRearNumberEt.setError(null);
//        }
//
//        return valid;
//    }
//
//    //이전에 입력한 외박일지 정보를 가져옴
//    private void setBeforeNumber(){
//        String beforeNumber = sharedPreferences.getString(BEFORE_PHONE_NUMBER, null);
////        Log.e("TAG", beforeNumber.equals("010--")+"");
//        if(beforeNumber !=null){
//            if(beforeNumber.equals("010--")){
//
//            StringTokenizer stringTokenizer = new StringTokenizer(beforeNumber, "-", false);
//            String beforeFrontNumber = stringTokenizer.nextToken();
//            String beforeMidNumber = stringTokenizer.nextToken();
//            String beforeRearNumber = stringTokenizer.nextToken();
//
//            for(int i = 0; i< FRONT_NUMBER_ARRAY.length; i++){
//                if(beforeFrontNumber.equals(FRONT_NUMBER_ARRAY[i]))
//                    mFrontNumberSpinner.setSelection(i);
//            }
//            mMidNumberEt.setText(beforeMidNumber);
//            mRearNumberEt.setText(beforeRearNumber);
//        }
//            }
//        int beforeType = sharedPreferences.getInt(BEFORE_SLEEP_TYPE,-1);
//        if(beforeType!=-1)
//            mSleepOutSpinner.setSelection(beforeType);
//    }
//}
//
