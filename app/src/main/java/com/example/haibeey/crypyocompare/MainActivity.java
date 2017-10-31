package com.example.haibeey.crypyocompare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    RecyclerView.LayoutManager mLayoutManager;
    usefulFunctions uf;
    private String[] cryptoCurrencies,currencies;
    TextView mTv;
    String fsym="fsyms=BTC",tsyms="tsyms=NGN";
    String Rate="$1917879.95";
    private float rate=0;
    DBhelper db;
    Handler handler;
    Thread thread;
    int dataSent=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mTv=(TextView)findViewById(R.id.tv1);

        //create and initialize database
        db=new DBhelper(this);
        db.initDb();

        //class to handle common stuff
        uf =new usefulFunctions(this);

        //get data associated with spinners
        cryptoCurrencies=getResources().getStringArray(R.array.cryptocurrency);
        currencies=getResources().getStringArray(R.array.worldCurrency);

        //setting up recyclerview
        setRecyclerViewAdapter();

        //setting button listener to create new card
        buttonListener();

        //setting spinner adapters
        setSpinner(R.id.spinner1,R.array.cryptocurrency);
        setSpinner(R.id.spinner2,R.array.worldCurrency);

        if(!uf.isConnected()){
            makeToast(R.string.noInternet,true);
        }else{
            new BackGroundWork().execute(fsym,tsyms);
        }
        //define a handler to handle toast message when updating all data in database
        handler();

        //listenning refreshes
        refreshListenner();

    }


    private void handler(){
        handler=new Handler(){
            int numberOfMsgSent=0;
            @Override
            public void handleMessage(Message msg) {
                ++numberOfMsgSent;
                ProgressBar pb=(ProgressBar) findViewById(R.id.pboab);
                if(msg.equals(null)){
                    pb.setVisibility(View.GONE);
                    makeToast(R.string.not,false);
                }else if(msg.obj=="update"){
                    pb.setVisibility(View.GONE);
                }else if(msg.obj=="no internet"){
                    pb.setVisibility(View.GONE);
                    makeToast(R.string.noInternet,false);
                } else if (msg.obj == "load") {
                    pb.setVisibility(View.VISIBLE);
                }
                //This ensures there is enough data before updating the ui
                if(numberOfMsgSent>=dataSent){
                    RecyclerView recyclerView=(RecyclerView)findViewById(R.id.rcv);
                    myAdapter adapter=new myAdapter(db.getData(),MainActivity.this);
                    recyclerView.swapAdapter(adapter,true);
                    numberOfMsgSent=0;
                }
            }
        };
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState){
        saveInstanceState.putString("theRate",Rate);
        super.onSaveInstanceState(saveInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle saveInstanceState){
        mTv.setText(saveInstanceState.getString("theRate"));
        super.onRestoreInstanceState(saveInstanceState);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    private void setRecyclerViewAdapter(){
        RecyclerView recyclerView=(RecyclerView) findViewById(R.id.rcv);
        recyclerView.setHasFixedSize(true);

        mLayoutManager=new LinearLayoutManager(this);

        recyclerView.setLayoutManager(mLayoutManager);

        myAdapter adapter=new myAdapter(db.getData(),this);
        recyclerView.setAdapter(adapter);
    }


    class BackGroundWork extends AsyncTask<String ,Void,String > {

        @Override
        protected String doInBackground(String... params) {
            if(validQuery(params[0]) && validQuery(params[1]))
                return uf.getData(params[0]+"&"+params[1]);
            else
                return "";
        }

        @Override
        protected void onPreExecute(){
            ProgressBar pb=(ProgressBar)findViewById(R.id.pb);
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s){
            ProgressBar pb=(ProgressBar)findViewById(R.id.pb);
            if(uf.isJson(s)){
                try {
                    String[] arr=uf.getJsonKey(getCurrency(fsym),getCurrency(tsyms),s);
                    updateUI(getCurrency(fsym),arr[0],getCurrency(tsyms),arr[1],arr[2]);
                    pb.setVisibility(View.GONE);
                }catch (Exception e){
                    pb.setVisibility(View.GONE);
                    makeToast(R.string.not,true);
                }
            }else{
                pb.setVisibility(View.GONE);
                makeToast(R.string.not,true);
            }
        }
    }

    class BackGroundWorkForCardViews extends AsyncTask<String ,Void,String > {
        String cryptocurrency="";
        String currency="";
        int id;
        ProgressBar pb;

        public void setProgressbar(ProgressBar Pb){
            pb=Pb;
        }
        @Override
        protected String doInBackground(String... params) {
            cryptocurrency=params[2];currency=params[3];id=Integer.valueOf(params[4]);
            if(validQuery(params[0]) && validQuery(params[1]))
                return uf.getData(params[0]+"&"+params[1]);
            else
                return "";
        }

        @Override
        protected void onPreExecute(){
            makeToast(R.string.load,false);
        }

        @Override
        protected void onPostExecute(String s){
            if(uf.isJson(s)){
                try{
                    String arr[]=uf.getJsonKey(cryptocurrency,currency,s);
                    updateDB(cryptocurrency,arr[0],currency,arr[1],arr[2]);
                    pb.setVisibility(View.GONE);
                }catch (Exception e){
                    makeToast(R.string.not,false);
                }
            }
            else{
                makeToast(R.string.not,false);
                pb.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //use of spinnerIds array to get the spinner selected by getting ID from parent.getId()
        int Id=parent.getId();
        if(Id==R.id.spinner1){
            fsym="fsyms="+cryptoCurrencies[position];
        }else if(Id==R.id.spinner2){
            tsyms="tsyms="+currencies[position];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void buttonListener(){
        Button b1=(Button)findViewById(R.id.b1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uf.isConnected())
                    new BackGroundWork().execute(fsym,tsyms);
                else
                    makeToast(R.string.noInternet,true);
            }
        });
    }

    private void refreshListenner(){
        ImageView im=(ImageView)findViewById(R.id.refresh);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillAll();
            }
        });
    }

    private void setSpinner(int id,int stringResource){
        Spinner spinner =(Spinner)findViewById(id);

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,stringResource,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    private boolean validQuery(String data){
        //check is a string is a valid query
        return data.contains("=");
    }

    private void updateUI(String key,String  rate,String value,String vol,String lastupdate){
        //update the database and setting rate on screen
        db.update(key,rate,value,vol,lastupdate);
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.rcv);
        myAdapter adapter=new myAdapter(db.getData(),this);
        recyclerView.swapAdapter(adapter,true);

        TextView tv=(TextView)findViewById(R.id.tv1);
        tv.setText(String.valueOf(rate));
    }

    private void updateDB(String key,String rate,String value,String vol,String lastupdate){
        db.update(key,rate,value,vol,lastupdate);
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.rcv);
        myAdapter adapter=new myAdapter(db.getData(),this);
        recyclerView.swapAdapter(adapter,true);
    }

    private String getCurrency(String fsym){
        return fsym.split("=")[1];
    }

    public void makeToast(int stringResource,boolean b){
        Toast.makeText(MainActivity.this,stringResource,b==true?Toast.LENGTH_LONG:Toast.LENGTH_SHORT).show();
    }

    private void fillAll(){
        final ArrayList<String[]> arr=db.getData();
        dataSent=arr.size();
        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                for(String[] data:arr){
                    if(uf.isConnected()){
                        Message msg=new Message();
                        msg.obj="load";
                        handler.sendMessage(msg);
                        update(data[0],data[2]);
                    }
                    else {
                        Message msg=new Message();
                        msg.obj="no internet";
                        handler.sendMessage(msg );
                    }
                }
            }
        });
        thread.start();
    }

    private  void update(String cryptoCurrency,String currency){
        try {
            String data=uf.getData("fsyms="+cryptoCurrency+"&"+"tsyms="+currency);
            String[] arr=uf.getJsonKey(cryptoCurrency,currency,data);
            db.update(cryptoCurrency,arr[0],currency,arr[1],arr[2]);
            Message msg=new Message();
            msg.obj="update";
            handler.sendMessage(msg );
        }catch (Exception e){
            Log.i("traceS",e.toString());
            handler.sendEmptyMessage(1);
        }
    }

}
