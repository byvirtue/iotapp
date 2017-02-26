package com.example.aroras.iotdataviewer.channel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.aroras.iotdataviewer.MainActivity;
import com.example.aroras.iotdataviewer.R;
import com.example.aroras.iotdataviewer.conf.Config;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Vector;

public class channels extends AppCompatActivity {
    ArrayList<String> myDataset1=new ArrayList<String>();
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    public String retreiv_data;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),createchannel.class);
                i.putExtra("method","1");
                i.putExtra("cid"," ");
                startActivity(i);
                finish();
            }
        });
        SharedPreferences pref1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref1.getString("regId", null);

        Log.e("my appp", "Firebase reg id: " + regId);
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),searchchannel.class);

                startActivity(i);
                finish();
            }
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences("iot", 0);
        final String phone = pref.getString("phone", null)+"b";


        mFirebaseDatabase.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                retreiv_data = dataSnapshot.getValue(String.class);
                String temp[]= new String[100];
                if(retreiv_data!=null && retreiv_data.contains("-"))
                    temp=retreiv_data.split("-");
                else
                    temp[0]=retreiv_data;
                //Log.e("hello",temp[0]);
                for(int i=0;i<temp.length;++i)
                    if(temp[i]!=null)
                    myDataset1.add(temp[i]);
                complete(phone);

              }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("asd", "Failed to read user", error.toException());
                Toast.makeText(getApplicationContext(),"Failed to fetch data result",Toast.LENGTH_SHORT).show();
            }
        });

    }


    void complete(String phone)
    {
       // Toast.makeText(getApplicationContext(),phone,Toast.LENGTH_SHORT).show();
        RecyclerView rv1 = (RecyclerView) findViewById(R.id.recycler_view1);
        rv1.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv1.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new com.example.aroras.iotdataviewer.channel.DividerItemDecoration(getApplicationContext());
        rv1.addItemDecoration(dividerItemDecoration);
        // specify an adapter (see also next example)

        channel_adapter mAdapter = new channel_adapter(myDataset1, getApplicationContext(), this);
        rv1.setAdapter(mAdapter);
progress.dismiss();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.logout) {
            Intent i=new Intent(getApplicationContext(),MainActivity.class);
            SharedPreferences pref = getApplicationContext().getSharedPreferences("iot", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("phone",null);
            editor.putString("apikey",null);
            editor.commit();

            startActivity(i);
            finish();

            return true;
        }
        if (id == R.id.newpass) {

            Intent i=new Intent(getApplicationContext(),password.class);
            SharedPreferences pref = getApplicationContext().getSharedPreferences("iot", 0);
            String phone = pref.getString("phone", null);
            i.putExtra("phone",phone);
            startActivity(i);
            finish();

            return true;
        }
        if (id == R.id.notify) {

            SharedPreferences pref = getApplicationContext().getSharedPreferences("iot", 0);
            String change = pref.getString("change", null);
            SharedPreferences.Editor editor = pref.edit();
            if(change!=null && change.equals("off")) {

                editor.putString("change", "on");
                editor.commit();
                Toast.makeText(getApplicationContext(),"Notification Enabled",Toast.LENGTH_SHORT).show();
                item.setTitle("Disabled Notification");
            }
            else
            {
                editor.putString("change", "off");
                editor.commit();
                item.setTitle("Enable Notification");
                Toast.makeText(getApplicationContext(),"Notification Disabled",Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
