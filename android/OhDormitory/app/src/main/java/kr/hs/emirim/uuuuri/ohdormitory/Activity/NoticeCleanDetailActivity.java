package kr.hs.emirim.uuuuri.ohdormitory.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Map;

import kr.hs.emirim.uuuuri.ohdormitory.Model.CleanNotice;
import kr.hs.emirim.uuuuri.ohdormitory.Model.Notice2;
import kr.hs.emirim.uuuuri.ohdormitory.R;

public class NoticeCleanDetailActivity extends AppCompatActivity {
    private final String PUT_EXTRA_NOTICE = "NOTICE_ITEM";
    private final int CLEAN_AREA_SIZE = 7;

    private int FOURTH_ID = 1000;
    private int FIFTH_ID = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_clean_detail);

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
        CleanNotice notice = (CleanNotice) bundle.getSerializable(PUT_EXTRA_NOTICE);
        Map<Integer, Integer> cleanList = notice.getCleanList();

        ArrayList<Button> fourthFloor = new ArrayList<>();
        ArrayList<Button> fifthFloor = new ArrayList<>();
        int index = 0;

        GridLayout fourthGrid = findViewById(R.id.fourth_grid);
        for(int i = 0; i<CLEAN_AREA_SIZE; i++){
            Button button = new Button(NoticeCleanDetailActivity.this);
            button.setId(FOURTH_ID++);
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setBackgroundResource(R.drawable.gridlayout_solid);
            button.setText(String.valueOf(cleanList.get(index++)));
            fourthGrid.addView(button);
            fourthFloor.add(button);
        }

        GridLayout fifthGrid = findViewById(R.id.fifth_grid);

        for(int i = 0; i<CLEAN_AREA_SIZE; i++){
            Button button = new Button(NoticeCleanDetailActivity.this);
            button.setId(FIFTH_ID++);
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setBackgroundResource(R.drawable.gridlayout_solid2);
            button.setText(String.valueOf(cleanList.get(index++)));
            fifthGrid.addView(button);
            fifthFloor.add(button);
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
