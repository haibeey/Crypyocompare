package com.example.haibeey.crypyocompare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

public class Exchange extends AppCompatActivity {

    String spinnerData1;
    String spinnerData2;
    String spinnerData3;
    usefulFunctions uf;
    String sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        uf=new usefulFunctions(this);

        spinnerData1=parse(getIntent().getStringExtra("rate"));
        spinnerData2=getIntent().getStringExtra("cryptocurrency");
        spinnerData3=getIntent().getStringExtra("currency");

        TextView textView=(TextView)findViewById(R.id.tvv);
        textView.setText(spinnerData2+" to "+spinnerData3);

        //set up listener for the exchangeButton
        exchangeListenner();

    }

    private void calculateResult(){
        //text view that shows the result
        TextView textView=(TextView)findViewById(R.id.atva);
        //input view
        AutoCompleteTextView autoCompleteTextView=(AutoCompleteTextView)findViewById(R.id.auto);
        //user input
        String value=autoCompleteTextView.getText().toString();

        Float result= uf.exchange(Float.valueOf(spinnerData1), Float.valueOf(value));
        textView.setText(sign+String.valueOf(result));



    }

    private void exchangeListenner(){
        Button button=(Button)findViewById(R.id.bua);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    calculateResult();
                }catch(NumberFormatException e){
                    TextView textView=(TextView)findViewById(R.id.atva);
                    textView.setText("Bad Input");
                }
            }
        });
    }

    private String parse(String s){
        s=s.replaceAll(",","");
        sign=s.split(" ")[0]+" ";
        return s.split(" ")[1];
    }

}
