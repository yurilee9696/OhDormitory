package kr.hs.emirim.doori.ohdormitoryparents.FCM;

/**
 * Created by doori on 2017-10-07.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    private FirebaseDatabase mDatabase;
    Context mContext;
    String phone_number;

    private final String USER_INFO_PREF = "User info";
    private final String OBJECT_USER = "Object user";

    public FirebaseInstanceIDService(Context context) {
        mContext = context;
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        phone_number = pref.getString("phone_number", "");
    }

    private static final String TAG = "MyFirebaseIIDService";
    String token;

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        token = FirebaseInstanceId.getInstance().getToken();

    }

    public void sendRegistrationToServer() {
        onTokenRefresh();

        GetData task = new GetData();

        Log.e(TAG, "Refreshed token: " + token);
        Log.e(TAG, "phone_number: " + phone_number);

        task.execute(phone_number, token);

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

            if (result.replaceAll("1","").equals("")) {
                Log.e(TAG, "성공" + result);
            } else {
                Log.e(TAG, "실패" + result);

            }

        }


        @Override

        protected String doInBackground(String... params) {

            String parent_phone = params[0];

            String token = params[1];


            String serverURL = "http://54.203.113.95/insertParentToken.php";

            String postParameters = "parent_phone=" + parent_phone + "&" + "token=" + token;


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

                if (responseStatusCode == HttpURLConnection.HTTP_OK) {

                    inputStream = httpURLConnection.getInputStream();

                } else {

                    inputStream = httpURLConnection.getErrorStream();

                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


                StringBuilder sb = new StringBuilder();

                String line;


                while ((line = bufferedReader.readLine()) != null) {

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
}

//}
//public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
//    private FirebaseDatabase mDatabase;
//
//    private static final String TAG = "MyFirebaseIIDService";
//    String token;
//    // [START refresh_token]
//    @Override
//    public void onTokenRefresh() {
//        // Get updated InstanceID token.
//        token= FirebaseInstanceId.getInstance().getToken();
//        Log.e(TAG, "Refreshed token: " + token);
//
//    }
//
//    public void sendRegistrationToServer(String myPhoneNumber) {
//        onTokenRefresh();
//        // Add custom implementation, as needed.
//        mDatabase = FirebaseDatabase.getInstance();
//        DatabaseReference tokenRef = mDatabase.getReference("token");
//
//        Map<String, Object> tokenUpdates = new HashMap<String, Object>();
//        tokenUpdates.put("parents/"+ myPhoneNumber, token);
//        tokenRef.updateChildren(tokenUpdates);
//
//    }
//}