package kr.hs.emirim.uuuuri.ohdormitory.SQL;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

public class DataPacker {
    private String emirim_id;
    private String password;
    private String newPassword;

    public DataPacker(String emirim_id, String password, String newPassword) {
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

                Log.e("??", key+"/"+value);

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
