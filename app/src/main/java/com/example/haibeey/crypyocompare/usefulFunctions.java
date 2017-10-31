package com.example.haibeey.crypyocompare;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by haibeey on 10/7/2017.
 */


public class usefulFunctions {
    Context con;
    public String url="https://min-api.cryptocompare.com/data/pricemultifull?";
    usefulFunctions(Context con){
        this.con=con;
    }

    public String  getData(String page){
        String res;
        try{
            URL url=new URL(this.url+page);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setReadTimeout(25000);
            conn.connect();

            InputStream S=conn.getInputStream();

            Scanner sc=new Scanner(S).useDelimiter("\\A");
            if (sc.hasNext())
                res=sc.next();
            else
                res="";

        } catch (Exception e){res="";}
        return res;
    }

    public boolean isConnected(){ConnectivityManager check= (ConnectivityManager)this.con.getSystemService(this.con.CONNECTIVITY_SERVICE);
        if(check!=null){
            NetworkInfo[] info=check.getAllNetworkInfo();
            if(info!=null){
                for(int i=0;i<info.length;i++){
                    if(info[i].getState()==NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public boolean isJson(String S){
        if(S.contains("{") && S.contains("}"))
            return true;
        return false;
    }

    public String[] getJsonKey(String key,String Value,String Data){
        try{
            JSONObject jo=new JSONObject(Data).getJSONObject("DISPLAY");
            JSONObject theKey=jo.getJSONObject(key);
            JSONObject theValue=theKey.getJSONObject(Value);
            return new String []{theValue.getString("PRICE"),theValue.getString("LASTUPDATE"),theValue.getString("VOLUME24HOUR")};

        }catch (Exception e){e.printStackTrace();}
        return new String[]{};
    }

    public float exchange(float value,float rate){
        return value*rate;
    }

    public int generateId(View v,int start){
        while(v.findViewById(++start%2147483647)!=null){}
        return start;
    }

}

