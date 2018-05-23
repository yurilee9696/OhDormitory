package kr.hs.emirim.uuuuri.ohdormitory.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kr.hs.emirim.uuuuri.ohdormitory.Adapter.NoticeListAdapter;
import kr.hs.emirim.uuuuri.ohdormitory.Model.User;
import kr.hs.emirim.uuuuri.ohdormitory.R;

public class QRCamActivity extends AppCompatActivity {

    private String TAG = "QRCamActivity";
    private FirebaseDatabase mDatabase;

    private final String USER_INFO_PREF = "User info";
    private final String OBJECT_USER = "Object user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcam);

        new IntentIntegrator(this).initiateScan();


    }
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        //  com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE
        //  = 0x0000c0de; // Only use bottom 16 bits
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (result.getContents()==null) {
                // 취소됨
                final Dialog mDialog = new Dialog(QRCamActivity.this, R.style.MyDialog);
                mDialog.setContentView(R.layout.dialog_style2);
                ((TextView)mDialog.findViewById(R.id.dialog_text)).setText("취소되었습니다.");
                mDialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();

                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });
                mDialog.show();
            } else {
                // 스캔된 QRCode --> result.getContents()
                Log.e("결과",result.getContents());
                recognizeSleepOut(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void recognizeSleepOut(final String contents){

        User user = getUserInfo();
        String[] QRinfo = contents.split("/");

        if(QRinfo[2].equals(user.getParent_phone())){

            GetData task = new GetData();
            task.execute(QRinfo);

        }else{
            final Dialog mCheckDialog = new Dialog(QRCamActivity.this, R.style.MyDialog);
            mCheckDialog.setContentView(R.layout.dialog_style2);
            ((TextView)mCheckDialog.findViewById(R.id.dialog_text)).setText("본인의 부모님께 발송된 QR코드로 인증해주세요.");
            mCheckDialog.show();

            mCheckDialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCheckDialog.dismiss();

                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
        }


//        mDatabase = FirebaseDatabase.getInstance();
//
//        final DatabaseReference sleepOutRef = mDatabase.getReference("sleep-out");
//
//        ValueEventListener sleepOutListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot sleepOut) {
//                Iterator<DataSnapshot> childIterator = sleepOut.getChildren().iterator();
//                //users의 모든 자식들의 key값과 value 값들을 iterator로 참조
//                while(childIterator.hasNext()) {
//                    DataSnapshot sleepOutStudent=childIterator.next();
//                    String qrContent[] = contents.split("/");
//                    String uid = sleepOutStudent.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getKey();
//
//                    if(qrContent[1].equals(uid)){
//                        Map<String, Object> sleepRecognizeUpdates = new HashMap<String, Object>();
//                        sleepRecognizeUpdates.put(contents+"/recognize", "true");
//                        sleepOutRef.updateChildren(sleepRecognizeUpdates);
//                        final Dialog mDialog = new Dialog(QRCamActivity.this, R.style.MyDialog);
//                        mDialog.setContentView(R.layout.dialog_style2);
//                        ((TextView)mDialog.findViewById(R.id.dialog_text)).setText("인증되었습니다.");
//                        mDialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener(){
//                            @Override
//                            public void onClick(View view) {
//                                Intent intent = new Intent(QRCamActivity.this, MainActivity.class);
//                                startActivity(intent);
//                            }
//                        });
//                        mDialog.show();
//
//
//                    }else{
//                        final Dialog mDialog = new Dialog(QRCamActivity.this, R.style.MyDialog);
//                        mDialog.setContentView(R.layout.dialog_style2);
//                        ((TextView)mDialog.findViewById(R.id.dialog_text)).setText("본인의 부모님께 발송된 QR코드로 인증해주세요.");
//                        mDialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener(){
//                            @Override
//                            public void onClick(View view) {
//                                Intent intent = new Intent(QRCamActivity.this, MainActivity.class);
//                                startActivity(intent);
//                            }
//                        });
//                        mDialog.show();
//
//
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        };
//        sleepOutRef.addListenerForSingleValueEvent(sleepOutListener);







    }


    private class GetData extends AsyncTask<String, Void, String> {


        ProgressDialog progressDialog;

        String errorString = null;


        @Override

        protected void onPreExecute() {

            super.onPreExecute();


            progressDialog = ProgressDialog.show(QRCamActivity.this,

                    "잠시만 기다려주세요", null, true, true);

        }



        @Override

        protected void onPostExecute(String result) {

            super.onPostExecute(result);


            progressDialog.dismiss();




            if (result.equals("1")){
                Log.e(TAG,"성공"+result);
                final Dialog mCheckDialog = new Dialog(QRCamActivity.this, R.style.MyDialog);
                mCheckDialog.setContentView(R.layout.dialog_style2);
                ((TextView)mCheckDialog.findViewById(R.id.dialog_text)).setText("인증되었습니다.");
                mCheckDialog.show();

                mCheckDialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCheckDialog.dismiss();

                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });



            }

            else {


                Log.e(TAG,"실패"+result);
                final Dialog mCheckDialog = new Dialog(QRCamActivity.this, R.style.MyDialog);
                mCheckDialog.setContentView(R.layout.dialog_style2);
                ((TextView)mCheckDialog.findViewById(R.id.dialog_text)).setText("인증에 실패했습니다.");
                mCheckDialog.show();

                mCheckDialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCheckDialog.dismiss();

                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });


            }

        }



        @Override

        protected String doInBackground(String... params) {


            String notice_id = params[0];

            String emirim_id = params[1];



            String serverURL = "http://54.203.113.95:8080/updateSleepoutRecognize.php";

            String postParameters = "notice_id=" + notice_id + "&" + "emirim_id=" + emirim_id;


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
        SharedPreferences prefs = getSharedPreferences(USER_INFO_PREF, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(OBJECT_USER, "");
        return gson.fromJson(json, User.class);
    }


}
