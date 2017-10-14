package com.example.haibeey.crypyocompare;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by haibeey on 10/7/2017.
 */

    //fsym=ETH&tsyms=BTC,USD,EUR

public class usefulFunctions {
    Context con;
    public String url="https://min-api.cryptocompare.com/data/price?";
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
            conn.setReadTimeout(7000);
            conn.connect();

            InputStream S=conn.getInputStream();

            Scanner sc=new Scanner(S).useDelimiter("\\A");
            if (sc.hasNext())
                res=sc.next();
            else
                res="";

        } catch (Exception e){res="";}
        Log.i("res",res);
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

    public double getJsonKey(String key,String Data){
        try{
            JSONObject jo=new JSONObject(Data);
            return jo.getDouble(key);
        }catch (Exception e){}
        return 0;
    }

    public double exchange(double value,double rate){
        return value*rate;
    }

}

