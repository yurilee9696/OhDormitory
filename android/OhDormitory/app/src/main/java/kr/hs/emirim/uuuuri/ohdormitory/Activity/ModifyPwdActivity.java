package kr.hs.emirim.uuuuri.ohdormitory.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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
import java.net.URLEncoder;
import java.util.Iterator;

import kr.hs.emirim.uuuuri.ohdormitory.Adapter.Connector;
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

    class Sender extends AsyncTask<Void,Void,String> {

        Activity mActivity;

        String urlAddress;

        String id, password, newPassword;
        /*
            1.OUR CONSTRUCTOR
            2.RECEIVE CONTEXT,URL ADDRESS AND EDITTEXTS FROM OUR MAINACTIVITY
        */
        public Sender(Activity activity, String urlAddress, String id, String password, String newPassword) {
            this.mActivity = activity;
            this.urlAddress = urlAddress;

            //GET TEXTS FROM EDITEXTS
            this.id = id;
            this.password = password;
            this.newPassword = newPassword;
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
                Toast.makeText(mActivity.getApplicationContext(),"변경 성공",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mActivity, MainActivity.class);
                mActivity.startActivity(intent);

            }else
            {
                //NO SUCCESS
                Toast.makeText(mActivity.getApplicationContext(),"변경 실패",Toast.LENGTH_LONG).show();
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
                bw.write(new UserDataPacker(id, password, newPassword).packUserData());

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

    class UserDataPacker {
        private String emirim_id;
        private String password;
        private String newPassword;

        public UserDataPacker(String emirim_id, String password, String newPassword) {
            this.emirim_id = emirim_id;
            this.password = password;
            this.newPassword = newPassword;
        }

        public String packUserData(){
            JSONObject jo = new JSONObject();
            StringBuffer packedData = new StringBuffer();

            try{
                jo.put("emirim_id", emirim_id);
                jo.put("password", password);
                jo.put("new_password", newPassword);

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



