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

    float spinnerData1;
    String spinnerData2;
    String spinnerData3;
    usefulFunctions uf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        uf=new usefulFunctions(this);

        spinnerData1=getIntent().getFloatExtra("rate",0);
        spinnerData2=getIntent().getStringExtra("spinner1");
        spinnerData3=getIntent().getStringExtra("spinner2");

        TextView textView=(TextView)findViewById(R.id.tvv);
        textView.setText(spinnerData2+" to "+spinnerData3);

        //set up listener for the exchangeButton
        exchangeListenner();

    }

    private void calculateResult(){
        //textview that shows the result
        TextView textView=(TextView)findViewById(R.id.atva);
        //input view
        AutoCompleteTextView autoCompleteTextView=(AutoCompleteTextView)findViewById(R.id.auto);

        String value=(String)autoCompleteTextView.getText().toString();

        float result=(float)uf.exchange(spinnerData1,Double.valueOf(value));

        textView.setText(String.valueOf(((long) result)));


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
}
