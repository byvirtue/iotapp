package com.example.aroras.iotdataviewer.channel;

/**
 * Created by arora's on 2/24/2017.
 */


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.aroras.iotdataviewer.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Vector;


public class field_adapter extends RecyclerView.Adapter<field_adapter.ViewHolder>{
    ArrayList<Vector> mDataset=new ArrayList<Vector>();
    private Context cont;
    private display nb;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView mCardView;
        public ViewHolder(CardView v) {
            super(v);
            mCardView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public field_adapter(ArrayList<Vector> myDataset,Context c,display n) {
        mDataset = myDataset;
        cont=c;
        nb=n;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public field_adapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        TextView  ch_id= (TextView)  holder.mCardView.findViewById(R.id.card_text);
        ch_id.setText("field"+(String)mDataset.get(position).elementAt(0)+"\n"+mDataset.get(position).elementAt(1));
final String chart="https://thingspeak.com/channels/"+(String)mDataset.get(position).elementAt(3)+"/charts/"+(String)mDataset.get(position).elementAt(0)+"?api_key="+(String)mDataset.get(position).elementAt(2);
final String refresh="https://thingspeak.com/channels/"+(String)mDataset.get(position).elementAt(3)+"/feeds.json?api_key="+(String)mDataset.get(position).elementAt(2)+"&results=2";




        final int current_pos=position;

        holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                remove_item(current_pos);

                return true;
            }
        });
        holder.mCardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

Intent i=new Intent(cont,field_display.class);
        i.putExtra("charts",chart);
                i.putExtra("refresh",refresh);
                i.putExtra("field","field"+(String)mDataset.get(position).elementAt(0));
                i.putExtra("ch_id",(String)mDataset.get(position).elementAt(3));
                i.putExtra("api_id",(String)mDataset.get(position).elementAt(2)) ;
                nb.startActivity(i);

            }
        });
    }

    public void remove_item(int position) {
        String r= String.valueOf(position);
        // Toast.makeText(cont,r, Toast.LENGTH_SHORT).show();
        //Toast.makeText(cont,mDataset.get(position), Toast.LENGTH_SHORT).show();


        if(position<mDataset.size())
            mDataset.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }



}
