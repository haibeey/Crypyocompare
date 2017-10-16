package com.example.haibeey.crypyocompare;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by haibeey on 10/15/2017.
 */

public class myAdapter extends RecyclerView.Adapter<myAdapter.viewHolder> {
    ArrayList<String[]> arrayList;
    Context context;

    public myAdapter(ArrayList<String[]> arraylist, Context con){
        arrayList=arraylist;
        context=con;
    }
    @Override
    public myAdapter.viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardlayout,parent,false);
        ImageView im=(ImageView)view.findViewById(R.id.cvim);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(myAdapter.viewHolder holder, int position) {
        String[] arr=arrayList.get(position);
        holder.tv1.setText(arr[0]);
        holder.tv2.setText(arr[1]);

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{
        TextView tv1,tv2;
        ImageView im1;

        public viewHolder(View itemView) {
            super(itemView);
            tv1=(TextView) itemView.findViewById(R.id.cvtv1);
            tv2=(TextView) itemView.findViewById(R.id.cvtv2);
            im1=(ImageView)itemView.findViewById(R.id.cvim);
        }
    }
}
