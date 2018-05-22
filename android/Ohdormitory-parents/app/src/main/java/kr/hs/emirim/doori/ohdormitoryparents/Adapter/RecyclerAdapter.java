package kr.hs.emirim.doori.ohdormitoryparents.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kr.hs.emirim.doori.ohdormitoryparents.Activity.QRDetailActivity;
import kr.hs.emirim.doori.ohdormitoryparents.Model.StudentInfo;
import kr.hs.emirim.doori.ohdormitoryparents.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private final String PUT_STUDENT_INFO = "STUDENT_ITEM";

    Context context;
    List<StudentInfo> studentInfoList;
    int item_layout;

    public RecyclerAdapter(Context context, List<StudentInfo> items, int item_layout) {
        this.context = context;
        this.studentInfoList = items;
        this.item_layout = item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qr, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final StudentInfo studentInfo = studentInfoList.get(position);
        //Drawable drawable = ContextCompat.getDrawable(context, item.getImage());
        //holder.image.setBackground(drawable);
        holder.sleepout_time.setText(studentInfo.getSleep_w_time()+" ~ "+studentInfo.getSleep_d_time());
        holder.name.setText(studentInfo.getName());
        holder.sleepout_type.setText(studentInfo.getSleep_type());

        holder.recognize_message.setText(studentInfo.isRecognize()?"인증이 완료되었습니다.":"인증이 필요합니다.");

        if(!studentInfo.isRecognize()) {
            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, QRDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(PUT_STUDENT_INFO, studentInfo);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.studentInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView sleepout_time;
        TextView sleepout_type;
        TextView name;
        TextView recognize_message;



        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            sleepout_time = (TextView) itemView.findViewById(R.id.textview_time);
            sleepout_type = (TextView) itemView.findViewById(R.id.textview_sleep_type);
            name = (TextView) itemView.findViewById(R.id.textview_name);
            recognize_message = (TextView) itemView.findViewById(R.id.textview_recognize);
            cardview = (CardView) itemView.findViewById(R.id.myCardView);
        }
    }
}

