package kr.hs.emirim.uuuuri.ohdormitory.Adapter;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import kr.hs.emirim.uuuuri.ohdormitory.Activity.NoticeCleanDetailActivity;
import kr.hs.emirim.uuuuri.ohdormitory.Activity.NoticeDetailActivity;
import kr.hs.emirim.uuuuri.ohdormitory.Model.BasicNotice;
import kr.hs.emirim.uuuuri.ohdormitory.Model.CleanNotice;
import kr.hs.emirim.uuuuri.ohdormitory.Model.Notice;
import kr.hs.emirim.uuuuri.ohdormitory.Model.Notice2;
import kr.hs.emirim.uuuuri.ohdormitory.Model.NoticeKind;
import kr.hs.emirim.uuuuri.ohdormitory.Model.SleepOut;
import kr.hs.emirim.uuuuri.ohdormitory.Model.SleepoutNotice;
import kr.hs.emirim.uuuuri.ohdormitory.Model.User;
import kr.hs.emirim.uuuuri.ohdormitory.R;

/**
 * Created by doori on 2017-10-01.
 */

public class NoticeListAdapter extends RecyclerView.Adapter<NoticeListAdapter.ViewHolder> {
    private final String TAG = "NOTICE_LIST_ADAPTER";
    private final String PUT_EXTRA_NOTICE = "NOTICE_ITEM";

    private final String SLEEP_OUT_ARRAY[] = {"잔류", "금요외박", "토요외박"};

    private ArrayList<Notice> mDataset;

    private String mSleepOut;
    private int mSleepOutPosition;

    Spinner mSleepOutSpinner;

    User mUser;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView mTextView_content;
        TextView mTextView_time;
        LinearLayout cardTypeColor;
        CardView mMyCardView;
        public ViewHolder(View view) {
            super(view);
            mTextView_content = (TextView)view.findViewById(R.id.textview_content);
            mTextView_time= (TextView)view.findViewById(R.id.textview_time);
            cardTypeColor = (LinearLayout)view.findViewById(R.id.card_type);
            mMyCardView = (CardView)view.findViewById(R.id.myCardView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NoticeListAdapter(ArrayList<Notice> noticeCardViewDataset, User user) {
        mDataset = noticeCardViewDataset;
        mUser = user;
        Log.e(TAG, mUser.toString());
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NoticeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //recycler view에 반복될 아이템 레이아웃 연결
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice,null);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final int kind = mDataset.get(position).getType();
        switch(kind){
            case NoticeKind.BASIC_NOTICE:
                holder.cardTypeColor.setBackgroundColor(0xff9cb1c2);
                break;
            case NoticeKind.CLEAN_NOTICE:
                holder.cardTypeColor.setBackgroundColor(0xff7bc792);
                break;
            case NoticeKind.SLEEP_OUT_NOTICE:
                holder.cardTypeColor.setBackgroundColor(0xffe36363);
                break;
        }

        holder.mTextView_content.setText(mDataset.get(position).getTitle());
        if(mDataset.get(position).getW_time().equals("0"))
            holder.mTextView_time.setText("항시공지");
        else
            holder.mTextView_time.setText(mDataset.get(position).getW_time());

        holder.mMyCardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = null;
                Bundle bundle = null;
                switch(kind){
                    case NoticeKind.BASIC_NOTICE:
                        intent = new Intent(view.getContext(), NoticeDetailActivity.class);
                        bundle = new Bundle();
                        bundle.putSerializable(PUT_EXTRA_NOTICE, (BasicNotice)mDataset.get(position));
                        intent.putExtras(bundle);
                        view.getContext().startActivity(intent);
                        break;
                    case NoticeKind.CLEAN_NOTICE:
                        intent = new Intent(view.getContext(), NoticeCleanDetailActivity.class);
                        bundle = new Bundle();
                        bundle.putSerializable(PUT_EXTRA_NOTICE, (CleanNotice)mDataset.get(position));
                        intent.putExtras(bundle);
                        view.getContext().startActivity(intent);
                        break;
                    case NoticeKind.SLEEP_OUT_NOTICE:
                        showDialog(view, ((SleepoutNotice)mDataset.get(position)).getSleep_w_time(),((SleepoutNotice) mDataset.get(position)).getSleep_d_time());
                        break;
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void showDialog(View view, final String wTime, final String dTime){
        final Dialog mDialog = new Dialog(view.getContext(), R.style.MyDialog);
        mDialog.setContentView(R.layout.application_form);
        TextView sleepWTime = mDialog.findViewById(R.id.sleep_w_time);
        TextView sleepDTime = mDialog.findViewById(R.id.sleep_d_time);

        sleepWTime.setText(wTime);
        sleepDTime.setText(dTime);

        mSleepOutSpinner = mDialog.findViewById(R.id.sleep_out);
        mSleepOutSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mSleepOut = SLEEP_OUT_ARRAY[position];
                mSleepOutPosition = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mSleepOut = SLEEP_OUT_ARRAY[0];
            }
        });

//        setBeforeInfo();

        ((TextView)mDialog.findViewById(R.id.tv_parent_number)).setText(mUser.getParent_phone());

        mDialog.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final Dialog mCheckDialog = new Dialog(view.getContext(), R.style.MyDialog);
                mCheckDialog.setContentView(R.layout.dialog_style3);
                ((TextView)mCheckDialog.findViewById(R.id.message)).setText("외박일지를 제출하시겠습니까?");
                mCheckDialog.show();
                mCheckDialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO database에 저장
                        // phoneNumber, dtime, wtime, sleep type
                        SleepOut sleepOut = new SleepOut(mUser.getParent_phone(), mSleepOut,"false");
                        mCheckDialog.dismiss();
                        mDialog.dismiss();
                    }
                });

                mCheckDialog.findViewById(R.id.dialog_button_no).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCheckDialog.dismiss();
                    }
                });
                return;
            }
        });
        mDialog.show();
    }

}

