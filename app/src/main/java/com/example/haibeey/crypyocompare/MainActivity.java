package com.example.haibeey.crypyocompare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

// TODO: 10/14/2017
//fill up the fill implement back end for the other card
//add loading future
//beautify the ui

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    //fsym=ETH&tsyms=BTC,USD,EUR
    usefulFunctions uf;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String[] cryptoCurrencies;
    private String[] currencies;
    private String[] demand;
    private String[] price;
    private int[] spinnerIds=new int[4];
    String fsym="";
    String tsyms="";
    String key="NGN";
    private float rate=0;
    Boolean canRunThread=false;
    private boolean running=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //class to handle common stuff
        uf =new usefulFunctions(this);
        canRunThread=true;

        //get data associated with spinners
        cryptoCurrencies=getResources().getStringArray(R.array.cryptocurrency);
        currencies=getResources().getStringArray(R.array.worldCurrency);
        demand=getResources().getStringArray(R.array.demand);
        price=getResources().getStringArray(R.array.price);

        //setting button listener for new activity
        buttonListener();

        //set spinner id for later usage
        setSpinnerIds();

        //setting spinner adapters
        setSpinner(R.id.spinner1,R.array.cryptocurrency);
        setSpinner(R.id.spinner2,R.array.worldCurrency);
        setSpinner(R.id.spone,R.array.demand);
        setSpinner(R.id.sptwo,R.array.price);

        //set up a thread to check for new rate from https://min-api.cryptocompare.com/data/
        //in approximately 10 seconds
        thread();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
        }

        @Override
        protected void onPostExecute(String s){
            if(uf.isJson(s)){
                float Rate=(float)uf.getJsonKey(key,s);
                rate=Rate>0?Rate:rate;
                updateUI(rate);
            }else {
                makeToast(R.string.loading);
            }
            running=false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //use of spinnerIds array to get the spinner selected by getting ID from parent.getId()
        int Id=parent.getId();
        if(Id==spinnerIds[0]){
            fsym="fsym="+cryptoCurrencies[position];
            TextView textView=(TextView)findViewById(R.id.tvone);
            textView.setText("Notify me when "+cryptoCurrencies[position]+" price");
        }else if(Id==spinnerIds[1]){
            tsyms="tsyms="+currencies[position];
            key=currencies[position];
        }else if(Id==spinnerIds[2]){

        }else if(Id==spinnerIds[3]){

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
                Intent I=new Intent(getApplicationContext(),Exchange.class);
                I.putExtra("rate",rate);
                I.putExtra("spinner1",getCurrency(fsym));
                I.putExtra("spinner2",getCurrency(tsyms));
                startActivity(I);
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
        if(data.contains("="))
            return true;
        return false;
    }

    private void updateUI(double rate){
        //setting rate on screen
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
        spinnerIds[2]=R.id.spone;
        spinnerIds[3]=R.id.sptwo;
    }
}
