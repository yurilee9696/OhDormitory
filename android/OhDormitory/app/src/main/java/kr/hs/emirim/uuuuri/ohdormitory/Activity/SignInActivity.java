package kr.hs.emirim.uuuuri.ohdormitory.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import kr.hs.emirim.uuuuri.ohdormitory.Model.User;
import kr.hs.emirim.uuuuri.ohdormitory.R;

public class SignInActivity extends BaseActivity{
    private final String USER_INFO_PREF = "User info";
    private final String OBJECT_USER = "Object user";
    private final String URL = "http://54.203.113.95/getUser.php";

    private final String TAG = "SIGN_IN_ACTIVITY";

    EditText mMailEt;
    EditText mPassWordEt;
    Dialog mDialog;

    // PHP를 읽어올때 사용할 변수
    private Sender sender;

    // 테이블에 포함된 레코드들을 가지고 있을 리스트 변수
    private ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sigin_in);

        getWindow().setBackgroundDrawableResource(R.drawable.signin);

        users = new ArrayList<>();
        mMailEt = (EditText) findViewById(R.id.email);
        mPassWordEt = (EditText) findViewById(R.id.password);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startSignIn(mMailEt.getText().toString(), mPassWordEt.getText().toString());
            }
        });

        sender = new Sender();
        sender.execute(URL);

    }

    private void startSignIn(final String email, final String password) {

        if (!validateForm() ) {
            // check the text of editText and before record of SignIn
            return;
        }

        showProgressDialog(); // show progress bar

        for(User user : users) {
            if (user.getEmirim_id().equals(email) && user.getPassword().equals(password))
            {
                hideProgressDialog();
                Log.e(TAG, "user:"+user.toString());
                moveActivity(user);
                return;
            }
        }
        hideProgressDialog();
        showDialog("회원정보를 확인하세요.");
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mMailEt.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mMailEt.setError("Required.");
            valid = false;
        } else {
            mMailEt.setError(null);
        }

        String password = mPassWordEt.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassWordEt.setError("Required.");
            valid = false;
        } else {
            mPassWordEt.setError(null);
        }

        return valid;
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

    private void moveActivity(User user){
        SharedPreferences prefs = getSharedPreferences(USER_INFO_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString(OBJECT_USER, json);
        editor.commit();

        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }





    class Sender extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                URL phpUrl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection)phpUrl.openConnection();

                if ( conn != null ) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    if ( conn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        while ( true ) {
                            String line = br.readLine();
                            if ( line == null )
                                break;
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {
            Log.e(TAG, "잉?");
            try {
                // PHP에서 받아온 JSON 데이터를 JSON오브젝트로 변환
                JSONObject jObject = new JSONObject(str);
                // results라는 key는 JSON배열로 되어있다.
                JSONArray results = jObject.getJSONArray("user");
                Log.e(TAG, results.length()+"??");

                for ( int i = 0; i < results.length(); ++i ) {
                    JSONObject temp = results.getJSONObject(i);
                    users.add(new User(temp.getString("emirim_id"), temp.getString("password"), temp.getString("name"), temp.getInt("room_num"), temp.getString("parent_phone")));
                }

                Log.e(TAG, "콩앙"+users.toString());
            } catch (JSONException e) {
                e.printStackTrace();            Log.e(TAG, e.toString());

            }
        }
    }

}

