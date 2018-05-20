package kr.hs.emirim.uuuuri.ohdormitory.SQL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import kr.hs.emirim.uuuuri.ohdormitory.Activity.MainActivity;
import kr.hs.emirim.uuuuri.ohdormitory.Adapter.Connector;

public class Sender extends AsyncTask<Void,Void,String> {

    Activity mActivity;

    String urlAddress;

    String id, password, newPassword;

    ProgressDialog pd;

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
            bw.write(new DataPacker(id, password, newPassword).packUserData());

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