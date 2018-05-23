package kr.hs.emirim.uuuuri.ohdormitory.FCM;

/**
 * Created by doori on 2017-10-07.
 */

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import kr.hs.emirim.uuuuri.ohdormitory.Adapter.NoticeListAdapter;
import kr.hs.emirim.uuuuri.ohdormitory.Model.User;
import kr.hs.emirim.uuuuri.ohdormitory.R;


public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    private FirebaseDatabase mDatabase;
    Context mContext;
    User mUser;

    private final String USER_INFO_PREF = "User info";
    private final String OBJECT_USER = "Object user";

    public FirebaseInstanceIDService(Context context){
        mContext = context;
        mUser = getUserInfo();
    }

    private static final String TAG = "MyFirebaseIIDService";
    String token;
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        token= FirebaseInstanceId.getInstance().getToken();

    }

    public void sendRegistrationToServer() {
        onTokenRefresh();

        GetData task = new GetData();

        Log.e(TAG, "Refreshed token: " + token);
        Log.e(TAG, "emirim_id: " + mUser.getEmirim_id());

        task.execute(mUser.getEmirim_id(),token);

//        // Add custom implementation, as needed.
//        mDatabase = FirebaseDatabase.getInstance();
//        DatabaseReference tokenRef = mDatabase.getReference("token");
//
//        Map<String, Object> tokenUpdates = new HashMap<String, Object>();
//        tokenUpdates.put("students/"+ FirebaseAuth.getInstance().getCurrentUser().getUid(), token);
//        tokenRef.updateChildren(tokenUpdates);



    }


    private class GetData extends AsyncTask<String, Void, String> {



        String errorString = null;


        @Override

        protected void onPreExecute() {

            super.onPreExecute();



        }



        @Override

        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            if (result.equals("1")){
                Log.e(TAG,"성공"+result);
            }

            else {
                Log.e(TAG,"실패"+result);

            }

        }



        @Override

        protected String doInBackground(String... params) {

            String emirim_id = params[0];

            String token = params[1];


            String serverURL = "http://54.203.113.95:8080/insertUserToken.php";

            String postParameters = "emirim_id=" + emirim_id + "&" + "token=" + token;


            try {


                URL url = new URL(serverURL);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();



                httpURLConnection.setReadTimeout(5000);

                httpURLConnection.setConnectTimeout(5000);

                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);

                httpURLConnection.connect();



                OutputStream outputStream = httpURLConnection.getOutputStream();

                outputStream.write(postParameters.getBytes("UTF-8"));

                outputStream.flush();

                outputStream.close();



                int responseStatusCode = httpURLConnection.getResponseCode();

                Log.d(TAG, "response code - " + responseStatusCode);


                InputStream inputStream;

                if(responseStatusCode == HttpURLConnection.HTTP_OK) {

                    inputStream = httpURLConnection.getInputStream();

                }

                else{

                    inputStream = httpURLConnection.getErrorStream();

                }



                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


                StringBuilder sb = new StringBuilder();

                String line;


                while((line = bufferedReader.readLine()) != null){

                    sb.append(line);

                }



                bufferedReader.close();



                return sb.toString().trim();



            } catch (Exception e) {


                Log.d(TAG, "InsertData: Error ", e);

                errorString = e.toString();


                return null;

            }


        }

    }
    // 현재 사용자 객체 get
    private User getUserInfo(){
        SharedPreferences prefs = mContext.getSharedPreferences(USER_INFO_PREF, mContext.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(OBJECT_USER, "");
        return gson.fromJson(json, User.class);
    }

}