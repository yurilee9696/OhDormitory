package kr.hs.emirim.uuuuri.ohdormitory.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import kr.hs.emirim.uuuuri.ohdormitory.Model.User;
import kr.hs.emirim.uuuuri.ohdormitory.R;

public class UserInfoActivity extends AppCompatActivity {
    private final String TAG = "USER_INFO_ACTIVITY";
    private final String USER_INFO_PREF = "User info";
    private final String OBJECT_USER = "Object user";
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mUser = getUserInfo();
        Log.e(TAG, mUser.toString());

        ((TextView)findViewById(R.id.userName)).setText(mUser.getName());
        ((TextView)findViewById(R.id.roomNumber)).setText(String.valueOf(mUser.getRoom_num()));

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.scoreActivity).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, ScoreActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, ToggleSettingsActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.sign_out).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                signOut();
                Intent intent = new Intent(UserInfoActivity.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.modifyUserPwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, ModifyPwdActivity.class);
                startActivity(intent);
            }
        });



    }
    // 현재 사용자 객체 get
    private User getUserInfo(){
        SharedPreferences prefs = getSharedPreferences(USER_INFO_PREF, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(OBJECT_USER, "");
        return gson.fromJson(json, User.class);
    }

    private void signOut(){
        //TODO : shared preference save null
        SharedPreferences prefs = getSharedPreferences(USER_INFO_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(user);
        editor.putString(OBJECT_USER, null);
        editor.commit();
    }
}
