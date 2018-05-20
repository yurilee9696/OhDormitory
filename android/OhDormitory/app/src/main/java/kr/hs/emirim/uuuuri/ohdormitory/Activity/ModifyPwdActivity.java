package kr.hs.emirim.uuuuri.ohdormitory.Activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import kr.hs.emirim.uuuuri.ohdormitory.SQL.Sender;
import kr.hs.emirim.uuuuri.ohdormitory.Model.User;
import kr.hs.emirim.uuuuri.ohdormitory.R;


public class ModifyPwdActivity extends AppCompatActivity {

    private final String USER_INFO_PREF = "User info";
    private final String OBJECT_USER = "Object user";

    private final String URL = "http://54.203.113.95/updateUserData.php";

    private EditText mEtExistingPw;
    private EditText mEtNewPw;
    private EditText mEtRePw;
    private Dialog mDialog;

    private User mCurrentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);

        Gson gson = new Gson();
        SharedPreferences prefs = getSharedPreferences(USER_INFO_PREF, MODE_PRIVATE);

        String json = prefs.getString(OBJECT_USER, "");
        mCurrentUser = gson.fromJson(json, User.class);

        mEtExistingPw = findViewById(R.id.et_existing_pwd);
        mEtNewPw = findViewById(R.id.et_new_pwd);
        mEtRePw = findViewById(R.id.et_re_pwd);


        findViewById(R.id.btn_modify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validForm()){
                    Sender s = new Sender(ModifyPwdActivity.this, URL, mCurrentUser.getEmirim_id(), mEtExistingPw.getText().toString(), mEtNewPw.getText().toString());
                    s.execute();

                }else{
                    Log.e("??", "네?");
                    //                    mEtExistingPw.setText("");
//                    mEtNewPw.setText("");
//                    mEtRePw.setText("");
                }
            }
        });

    }

    private boolean validForm() {
        if (TextUtils.isEmpty(mEtExistingPw.getText().toString())) {
            mEtExistingPw.setError("Required.");
            return false;
        } else {
            mEtExistingPw.setError(null);
        }

        if (TextUtils.isEmpty(mEtNewPw.getText().toString())) {
            mEtNewPw.setError("Required.");
            return false;
        } else {
            mEtNewPw.setError(null);
        }

        if (TextUtils.isEmpty(mEtRePw.getText().toString())) {
            mEtRePw.setError("Required.");
            return false;
        } else {
            mEtRePw.setError(null);
        }

        if(!(mCurrentUser.getPassword().equals(mEtExistingPw.getText().toString()))){
            showDialog("현재 비밀번호를 확인하세요.");
            return false;
        }

        if(!(mEtRePw.getText().toString().equals(mEtNewPw.getText().toString()))){
            showDialog("비밀번호가 일치하지 않습니다.");
            return false;
        }

        if(mEtExistingPw.getText().toString().equals(mEtNewPw.getText().toString())){
            showDialog("기존 비밀번호와 동일합니다.");
            return false;
        }
        return true;
    }

    private void showDialog(String text){
        mDialog = new Dialog(this, R.style.MyDialog);
        mDialog.setContentView(R.layout.dialog_style2);
        // set the message
        ((TextView) mDialog.findViewById(R.id.dialog_text)).setText(text);
        mDialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                mDialog = null;
                return;
            }
        });
        mDialog.show();
    }


}



