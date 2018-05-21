package kr.hs.emirim.uuuuri.ohdormitory.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import kr.hs.emirim.uuuuri.ohdormitory.Model.BasicNotice;
import kr.hs.emirim.uuuuri.ohdormitory.Model.Notice;
import kr.hs.emirim.uuuuri.ohdormitory.Model.Notice2;
import kr.hs.emirim.uuuuri.ohdormitory.R;

public class NoticeDetailActivity extends AppCompatActivity {
    private final String PUT_EXTRA_NOTICE = "NOTICE_ITEM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        //custom actionbar
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.custom_actionbar2, null);

        actionBar.setCustomView(actionbar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        BasicNotice notice = (BasicNotice) bundle.getSerializable(PUT_EXTRA_NOTICE);

        TextView noticeTitle = findViewById(R.id.notice_title);
        TextView noticeDate = findViewById(R.id.notice_date);
        TextView noticeContent = findViewById(R.id.notice_content);

        noticeTitle.setText(notice.getTitle());
        noticeDate.setText("게시일 : " +notice.getW_time());
        noticeContent.setText(notice.getContent());
    }
    // onClick Method
    public void settings(View v){
        switch (v.getId()){
            case R.id.action_settings:
                Intent intent = new Intent(NoticeDetailActivity.this, UserInfoActivity.class);
                startActivity(intent);
                break;
        }

    }
    public void back(View v){
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
        }
    }
}
