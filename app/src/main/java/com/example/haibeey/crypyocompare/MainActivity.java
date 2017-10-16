package com.example.haibeey.crypyocompare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    RecyclerView.LayoutManager mLayoutManager;
    usefulFunctions uf;
    private String[] cryptoCurrencies,currencies;
    private int[] spinnerIds=new int[4];
    String fsym="";
    String tsyms="";
    private float rate=0;
    boolean canRunThread=false;
    private boolean running=false;
    DBhelper db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //create and initialize database
        db=new DBhelper(this);
        db.initDb();

        //class to handle common stuff
        uf =new usefulFunctions(this);
        canRunThread=true;
        for (String[] i:db.getData()){
            Log.i("tyab",i[0]+" "+i[1]+" "+i[2]+" ");
        }

        //get data associated with spinners
        cryptoCurrencies=getResources().getStringArray(R.array.cryptocurrency);
        currencies=getResources().getStringArray(R.array.worldCurrency);

        //setting up recyclerview
        setRecyclerViewAdapter();

        //setting button listener to create new card
        buttonListener();

        //set spinner id for later usage
        setSpinnerIds();

        //setting spinner adapters
        setSpinner(R.id.spinner1,R.array.cryptocurrency);
        setSpinner(R.id.spinner2,R.array.worldCurrency);


    }

    @Override
    public void onResume(){
        super.onResume();
        canRunThread=true;

        //checking for internet connectivity
        if(!uf.isConnected()){
            makeToast(R.string.noInternet);
        }

    }

    @Override
    public void onStop(){
        super.onStop();
        canRunThread=false;
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

            if(validQuery(fsym) && validQuery(tsyms))
                return uf.getData(fsym+"&"+tsyms);
            else
                return ""; //"{\"BTC\":0.06272,\"USD\":302.99,\"EUR\":254.78,\"NGN\":106236.96}";
        }

        @Override
        protected void onPreExecute(){
            running=true;
            ProgressBar pb=(ProgressBar)findViewById(R.id.pb);
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s){
            ProgressBar pb=(ProgressBar)findViewById(R.id.pb);
            if(uf.isJson(s)){
                float Rate=(float)uf.getJsonKey(getCurrency(tsyms),s);
                rate=Rate>0?Rate:rate;
                updateUI(getCurrency(fsym),rate,getCurrency(tsyms));
                pb.setVisibility(View.GONE);
            }
            running=false;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //use of spinnerIds array to get the spinner selected by getting ID from parent.getId()
        int Id=parent.getId();
        if(Id==spinnerIds[0]){
            fsym="fsym="+cryptoCurrencies[position];
        }else if(Id==spinnerIds[1]){
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
                    new BackGroundWork().execute("");
                else
                    makeToast(R.string.noInternet);
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

    private void updateUI(String key,double rate,String value){
        //setting rate on screen
        //update the database
        db.update(key,String.valueOf(rate),value);
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.rcv);
        myAdapter adapter=new myAdapter(db.getData(),this);
        recyclerView.swapAdapter(adapter,true);

        TextView tv=(TextView)findViewById(R.id.tv1);
        tv.setText(String.valueOf(rate));
    }

    private String getCurrency(String fsym){
        return fsym.split("=")[1];
    }

    private void thread(){
        final Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                while (canRunThread){
                    if(uf.isConnected() && !running)
                        new BackGroundWork().execute("");
                    try{
                        Thread.sleep(10000);
                    }catch (Exception e){}

                }
            }
        });

        thread.start();
    }

    private void makeToast(int stringResource){
        Toast.makeText(MainActivity.this,stringResource,Toast.LENGTH_LONG).show();
    }

    private void setSpinnerIds(){
        spinnerIds[0]=R.id.spinner1;
        spinnerIds[1]=R.id.spinner2;
    }
}
