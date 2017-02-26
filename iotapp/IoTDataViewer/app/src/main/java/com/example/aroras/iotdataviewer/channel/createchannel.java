package com.example.aroras.iotdataviewer.channel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aroras.iotdataviewer.Otp;
import com.example.aroras.iotdataviewer.R;
import com.example.aroras.iotdataviewer.user;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.Channels;
import java.util.Vector;

import static com.example.aroras.iotdataviewer.R.id.ss;

public class createchannel extends AppCompatActivity {
     public String urll="https://api.thingspeak.com/channels.json?api_key=";

       String mk="",ss="",sl="";
    /////
       public String channel_name,channel_id;

    ///////
   EditText Lat,Long;
    ////
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    String method,chid;

///////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createchannel);
        Intent ii = getIntent();
        method=ii.getStringExtra("method");
        chid=ii.getStringExtra("cid");
        if(method.equals("2"))
        {
            TextView tv = (TextView)findViewById(R.id.textView2);
            tv.setText("Update Channel");
            urll="https://api.thingspeak.com/channels/"+chid+".json?api_key=";

        }
        SharedPreferences pref = getApplicationContext().getSharedPreferences("iot", 0);
        final String api = pref.getString("apikey", null);
        urll+=api;

         Button btnn = (Button)findViewById(R.id.submit);
        Lat=(EditText)findViewById(R.id.Lat1);
        Long=(EditText)findViewById(R.id.Long1);
        final CheckBox make11=(CheckBox)findViewById(R.id.makepublic1);
        ////////
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        /////

make11.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        if(make11.isChecked()){
            mk="public_flag=1";

        }
        else
            mk="";
    }
});
        final CheckBox sl1=(CheckBox)findViewById(R.id.sl1);
        sl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sl1.isChecked()) {
                       sl="show_location=1";
                    Lat.setVisibility(v.VISIBLE);
                    Long.setVisibility(v.VISIBLE);

                } else
                {
                    sl="";
                    Lat.setVisibility(v.INVISIBLE);
                    Long.setVisibility(v.INVISIBLE);

            }
            }
        });
        final CheckBox ss1=(CheckBox)findViewById(R.id.ss1);
        ss1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ss1.isChecked()){
                    ss="show_status=1";
                }
                else
                    ss="";
            }
        });
        btnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                genurl();



                new create().execute();
            }
        }


        );
    }

    @Override
    public void onBackPressed() {
        Intent i;
        if(method.equals("2")) {
            i = new Intent(this, display.class);
            i.putExtra("chid", chid);
        }
        else
        {
            i = new Intent(this,channels.class);

        }
        startActivity(i);
        finish();
    }

    public void genurl()
    {
        EditText name1,desc1,field11,field21,field31,field41,field51,field61,field71,field81,metadata1,tags1,url1,elevation1;


        name1=(EditText)findViewById(R.id.name1);
        desc1=(EditText)findViewById(R.id.description1);
        field11=(EditText)findViewById(R.id.field11);
        field21=(EditText)findViewById(R.id.field21);
        field31=(EditText)findViewById(R.id.field31);
        field41=(EditText)findViewById(R.id.field41);
        field51=(EditText)findViewById(R.id.field51);
        field61=(EditText)findViewById(R.id.field61);
        field71=(EditText)findViewById(R.id.field71);
        field81=(EditText)findViewById(R.id.field81);
        metadata1=(EditText)findViewById(R.id.metadata1);
        tags1=(EditText)findViewById(R.id.tags1);
        url1=(EditText)findViewById(R.id.url1);
        elevation1=(EditText)findViewById(R.id.elevation1);


        String cr="";
        String name=name1.getText().toString();
        if(!(name.isEmpty()))
            cr=cr+"&name="+name;
        String desc=desc1.getText().toString();
        if(!(desc.isEmpty()))
            cr=cr+"&description="+desc;

        String field1=field11.getText().toString();
        String field2=field21.getText().toString();
        String field3=field31.getText().toString();
        String field4=field41.getText().toString();
        String field5=field51.getText().toString();
        String field6=field61.getText().toString();

        String field7=field71.getText().toString();
        String field8=field81.getText().toString();

        String metadata=metadata1.getText().toString();
        String tags=tags1.getText().toString();
        String url=url1.getText().toString();
        String elevation=elevation1.getText().toString();
        String Latt=Lat.getText().toString();
        String Longg=Long.getText().toString();

        if(!(field1.isEmpty()))
            cr=cr+"&field1="+field1;

        if(!(field2.isEmpty()))
            cr=cr+"&field2="+field2;
        if(!(field3.isEmpty()))
            cr=cr+"&field3="+field3;
        if(!(field4.isEmpty()))
            cr=cr+"&field4="+field4;
        if(!(field5.isEmpty()))
            cr=cr+"&field5="+field5;
        if(!(field6.isEmpty()))
            cr=cr+"&field6="+field6;
        if(!(field7.isEmpty()))
            cr=cr+"&field7="+field7;
        if(!(field8.isEmpty()))
            cr=cr+"&field8="+field8;
        if(!(elevation.isEmpty()))
            cr=cr+"&elevation="+elevation;
        if(!(metadata.isEmpty()))
            cr=cr+"&metadata="+metadata;
        if(!(mk.isEmpty()))
            cr=cr+"&"+mk;
        if(!(tags.isEmpty()))
            cr=cr+"&tags="+tags;
        if(!(url.isEmpty()))
            cr=cr+"&url="+url;

        if(!(sl.isEmpty()))
            cr=cr+"&"+sl;
        if(!(ss.isEmpty()))
            cr=cr+"&"+ss;

        if(!(Longg.isEmpty()))
            cr=cr+"&longitude="+Longg;
        if(!(Latt.isEmpty()))
            cr=cr+"&latitude="+Latt;

 urll=urll+cr;



    }


    private class create extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr;
            if(method.equals("2"))
                jsonStr = sh.makeServiceCall(urll,"PUT");
            else
                jsonStr = sh.makeServiceCall(urll,"POST");

            Log.e("", "Response from url: " + jsonStr);
            //Toast.makeText(getApplicationContext(),jsonStr,Toast.LENGTH_SHORT).show();
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    channel_id=jsonObj.getString("id");
                    channel_name=jsonObj.getString("name");
                    Log.e("asd", "Response from url: " + channel_name+" & "+channel_id);



                } catch (final JSONException e) {
                    Log.e("", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e("", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("iot", 0);
            final String phone = pref.getString("phone", null)+"b";


            mFirebaseDatabase.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String temp = dataSnapshot.getValue(String.class);
                    // Check for null
                    Intent i;
                    if(method.equals("2")) {
                        i = new Intent(getApplicationContext(), display.class);
                        i.putExtra("chid", chid);
                        startActivity(i);
                        finish();
                        return;
                    }
                    else
                        i = new Intent(getApplicationContext(), channels.class);
                    if (temp==null) {


                            String userId = mFirebaseDatabase.push().getKey();
                           mFirebaseDatabase.child(phone).setValue(channel_id);


                        startActivity(i);
                        finish();
                        return ;
                    }
                    else
                    {
                        temp+="-"+channel_id;
                        mFirebaseDatabase.child(phone).setValue(temp);
                        startActivity(i);
                        finish();
                        return;
                    }

                    // Display newly updated name and email
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.e("asd", "Failed to read user", error.toException());
                    Toast.makeText(getApplicationContext(),"Failed to fetch data rsult",Toast.LENGTH_SHORT).show();
                }
            });


        }

    }

}
