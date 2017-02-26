package com.example.aroras.iotdataviewer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.aroras.iotdataviewer.channel.HttpHandler;
import com.example.aroras.iotdataviewer.channel.channels;
import com.example.aroras.iotdataviewer.channel.password;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Otp extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String userId,concat_channel_id;
  public String url1,phone,name,email,pswd,api,method;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        Intent ii = getIntent();
        phone = ii.getStringExtra("phone");
        name = ii.getStringExtra("name");
        email = ii.getStringExtra("email");
        pswd = ii.getStringExtra("pswd");
        api = ii.getStringExtra("api");
        method = ii.getStringExtra("method");
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Random rr = new Random(System.nanoTime());
        final int rand = ((rr.nextInt(9999) + rr.nextInt(9999) + rr.nextInt(9999) ) % 9000)+1000;
        TextView et = (TextView) findViewById(R.id.textView);
        Button btn=(Button)findViewById(R.id.button3);
        final EditText otp=(EditText)findViewById(R.id.editText3);
        et.setText("OTP has been sent to the number : " + phone);
        //Toast.makeText(getApplicationContext(),String.valueOf(rand).toString(),Toast.LENGTH_SHORT).show();

        String uu = "http://sms.dataoxytech.com/index.php/smsapi/httpapi/?uname=sylvester007&password=forskmnit&sender=FORSKT&receiver="+phone+"&route=TA&msgtype=1&sms=Your%20OTP%20is%20"+rand;




        new RetrieveFeedTask().execute(uu);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ot=otp.getText().toString();
                if(ot.equals(String.valueOf(rand))) {
                    if(method.equals("1")) {
                        url1 = "http://api.thingspeak.com/channels.json?api_key=" + api;
                        if (!(api.equals(getString(R.string.our_api)))) {
                            new verify_add().execute();
                        } else {
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("iot", 0);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("phone", phone);
                            editor.putString("apikey", api);
                            editor.commit();
                            createUser(name, email, phone, pswd, api);
                            Intent i = new Intent(getApplicationContext(), channels.class);

                            startActivity(i);

                            finish();
                        }
                    }else
                    {
                        Intent i = new Intent(getApplicationContext(), password.class);
                        i.putExtra("phone",phone);
                        startActivity(i);
                    }

                }
                else
                    Toast.makeText(getApplicationContext(),"Wrong OTP entered",Toast.LENGTH_SHORT).show();


            }
        });

    }
    private void createUser(String name, String email,String phone,String  pswd,String api) {
        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
            userId=phone;
        }

        user usr = new user(name, email,phone ,pswd,api);

        mFirebaseDatabase.child(phone).setValue(usr);


    }



    class RetrieveFeedTask extends AsyncTask<String, Void,Void> {

        private Exception exception;

        URL url = null;
        InputStream is=null;
        protected Void doInBackground(String... urls) {





                try {
                    url = new URL(urls[0]);
                    is=url.openStream();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }




            return null;
        }


    }

    private class verify_add extends AsyncTask<Void, Void, Void> {
        int flag=0;
        ProgressDialog asyncDialog = new ProgressDialog(Otp.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.setMessage("loading...");
            //show dialog
            asyncDialog.show();
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url1,"GET");

            Log.e("", "Response from url: " + jsonStr);
            //Toast.makeText(getApplicationContext(),jsonStr,Toast.LENGTH_SHORT).show();
            if (jsonStr != null) {
                try {


                    JSONArray array=new JSONArray(jsonStr);
                   JSONObject temp=new JSONObject(array.getString(0));
                    concat_channel_id=temp.getString("id");
                    for(int j=1;j<array.length();++j)
                    {
                        temp=new JSONObject(array.getString(j));

                        concat_channel_id+="-"+temp.getString("id");
                    }
                    // Getting JSON Array node
                    Log.e("asd", "Response from url: " + concat_channel_id);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    concat_channel_id,
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });


                } catch (final JSONException e) { flag=1;
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
                flag=1;
                Log.e("", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "API KEY not valid!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            final String key =phone+"b";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            phone,
                            Toast.LENGTH_LONG)
                            .show();
                }
            });

            mFirebaseDatabase.child(key).setValue(concat_channel_id);
            SharedPreferences pref = getApplicationContext().getSharedPreferences("iot", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("phone",phone);
            editor.putString("apikey",api);
            editor.commit();
            createUser(name, email, phone, pswd,api);
            Intent i = new Intent(getApplicationContext(), channels.class);
            startActivity(i);
            finish();

        }

    }


}
