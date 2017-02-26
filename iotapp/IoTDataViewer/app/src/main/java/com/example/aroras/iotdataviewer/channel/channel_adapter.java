package com.example.aroras.iotdataviewer.channel;

/**
 * Created by arora's on 2/24/2017.
 */


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.aroras.iotdataviewer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Vector;





/**
 * Created by arora's on 2/18/2017.
 */

public class channel_adapter extends RecyclerView.Adapter<channel_adapter.ViewHolder>{
    ArrayList<String > mDataset=new ArrayList<String>();
    private Context cont;
    private channels nb;
    String urll;
    String api,chid,phone;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

   // int current_pos;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView mCardView;
        public ViewHolder(CardView v) {
            super(v);
            mCardView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public channel_adapter(ArrayList<String> myDataset,Context c,channels n) {
        mDataset = myDataset;
        cont=c;
        nb=n;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public channel_adapter.ViewHolder onCreateViewHolder(ViewGroup parent,
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
         ch_id.setText((String)mDataset.get(position));

       final int  current_pos=position;

        holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                mFirebaseInstance = FirebaseDatabase.getInstance();
                                mFirebaseDatabase = mFirebaseInstance.getReference("users");
                                SharedPreferences pref = cont.getSharedPreferences("iot", 0);
                                api = pref.getString("apikey", null);
                                phone = pref.getString("phone", null);
                                chid = (String) mDataset.get(current_pos);
                                phone+="b";
                                urll="https://api.thingspeak.com/channels/"+mDataset.get(current_pos)+".json?api_key="+api;
                                new delete().execute();
                                remove_item(current_pos);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };
                AlertDialog.Builder ab = new AlertDialog.Builder(view.getContext());
                ab.setMessage("Are you sure to delete?").setPositiveButton("Yes", dialogClickListener)
                       .setNegativeButton("No", dialogClickListener).show();
               // remove_item(current_pos);
                return true;
            }
        });
        holder.mCardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent i=new Intent(cont,display.class);
                i.putExtra("chid",mDataset.get(position));
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


    private class delete extends AsyncTask<Void, Void, Void> {
        int flag=0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr;
            jsonStr = sh.makeServiceCall(urll,"DELETE");
            Log.e("qqqqq", "Response from url: " + jsonStr);
            if (jsonStr != null) {

            } else {
                flag=1;
            Log.e("ppp", "Couldn't delete the channel");
            nb.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(cont,
                            "Couldn't delete the channel!",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });

        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {


            mFirebaseDatabase.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String temp = dataSnapshot.getValue(String.class);
                    String value = "";
                    Log.e("ggggg", temp+" "+chid+" "+phone);
                    String arr[] = new String[100];
                    if(temp!=null) {
                        Log.e("asd", temp + " " + chid + " " + phone);
                        if (temp.contains("-"))
                            arr = temp.split("-");
                        else
                            arr[0] = temp;

                        Log.e("hello", arr[0]);
                        for (int i = 0; i < arr.length; ++i) {
                            if (arr[i] != null) {

                                if (!(chid.equals(arr[i]))) {
                                    if (value.length() == 0)
                                        value = value + arr[i];
                                    else
                                        value = value + "-" + arr[i];
                                }

                            }
                        }

                    }
                    if(value!="")
                        mFirebaseDatabase.child(phone).setValue(value);
                    else
                    mFirebaseDatabase.child(phone).removeValue();
                    return;
                }


                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.e("asd", "Failed to read user", error.toException());
                    Toast.makeText(cont, "Failed to fetch data rsult", Toast.LENGTH_SHORT).show();
                }
            });


        }


}


}
