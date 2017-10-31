package com.example.haibeey.crypyocompare;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;



import java.util.ArrayList;



/**
 * Created by haibeey on 10/15/2017.
 */

public class myAdapter extends RecyclerView.Adapter<myAdapter.viewHolder> {
    ArrayList<String[]> arrayList;
    usefulFunctions uf;
    Context context;

    public myAdapter(ArrayList<String[]> arraylist, Context con){
        arrayList=arraylist;
        context=con;
        uf=new usefulFunctions(con);
    }

    @Override
    public myAdapter.viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardlayout,parent,false);
        final TextView tv1=(TextView)view.findViewById(R.id.cvtv1);
        final TextView tv2=(TextView)view.findViewById(R.id.cvtv2);
        final ImageView im1=(ImageView)view.findViewById(R.id.cvim);
        final  ProgressBar pb=(ProgressBar)view.findViewById(R.id.pbs);
        view.setId(pb.getId()*5);


        //setting of click listerner
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click","haibeeyclick");
                Intent I=new Intent(v.getContext(),Exchange.class);
                String[] arr=String.valueOf(tv1.getText()).split(" ");
                I.putExtra("cryptocurrency",arr[0]);
                I.putExtra("rate", String.valueOf(tv2.getText()));
                I.putExtra("currency",arr[2]);
                v.getContext().startActivity(I);
            }
        });


        im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] arr=String.valueOf(tv1.getText()).split(" ");
                MainActivity.BackGroundWorkForCardViews backGroundWork=((MainActivity)context).new BackGroundWorkForCardViews();
                if(((MainActivity)context).uf.isConnected()){
                    pb.setVisibility(View.VISIBLE);
                    backGroundWork.setProgressbar(pb);
                    backGroundWork.execute("fsyms="+arr[0],"tsyms="+arr[2],arr[0],arr[2],String.valueOf(pb.getId()));
                }
                else
                    ((MainActivity)context).makeToast(R.string.noInternet,true);
            }
        });
        return new viewHolder(view);

    }

    @Override
    public void onBindViewHolder(myAdapter.viewHolder holder, int position) {
        String[] arr=arrayList.get(position);
        holder.tv1.setText(arr[0]+" to "+arr[2]);
        holder.tv2.setText(arr[1]);
        holder.tv3.setText(arr[3]);
        holder.tv4.setText(arr[4]);
        holder.im1.setId(position);
        holder.pb.setId(position*3);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{
        TextView tv1,tv2,tv3,tv4;
        ImageView im1;
        ProgressBar pb;
        LinearLayout Ll;

        public viewHolder(View itemView) {
            super(itemView);
            tv1=(TextView) itemView.findViewById(R.id.cvtv1);
            tv2=(TextView) itemView.findViewById(R.id.cvtv2);
            tv3=(TextView) itemView.findViewById(R.id.cvtv3);
            tv4=(TextView) itemView.findViewById(R.id.cvtv4);
            im1=(ImageView)itemView.findViewById(R.id.cvim);
            pb=(ProgressBar)itemView.findViewById(R.id.pbs);
            Ll=(LinearLayout)itemView.findViewById(R.id.llL);
        }

    }
}
