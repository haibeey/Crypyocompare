package com.example.haibeey.crypyocompare;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by haibeey on 10/7/2017.
 */

public class usefulFunctions {
    Context con;
    String url;
    usefulFunctions(Context con){
        this.con=con;
        this.url=url;
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
        return res;
    }

    public boolean isConnected(){
        ConnectivityManager check= (ConnectivityManager)this.con.getSystemService(this.con.CONNECTIVITY_SERVICE);
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

    public long exchange(int value,int rate){
        return value*rate;
    }

    
}

