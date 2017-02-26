package com.example.aroras.iotdataviewer.channel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aroras.iotdataviewer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class searchchannel extends AppCompatActivity {

    String search_id,url;
    EditText search;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchchannel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");


       search=(EditText)findViewById(R.id.searchbox);
        final Button btn=(Button)findViewById(R.id.button4);
        btn.setClickable(true);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setClickable(false);
                search_id=search.getText().toString();
                url="http://api.thingspeak.com/channels/"+search_id+".json";
                new create().execute();
            }
        });
    }

    private class create extends AsyncTask<Void, Void, Void> {

        int flag=0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();


            String jsonStr = sh.makeServiceCall(url,"GET");

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    Log.e("error", jsonStr);
                    if(jsonObj.has("error"))
                    {
                        flag=1;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Channel not available",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });

                    }                    // Getting JSON Array node



                } catch (final JSONException e) {
                    flag=1;
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
            if (flag == 0) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("iot", 0);
                final String phone = pref.getString("phone", null) + "b";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                phone,
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

                mFirebaseDatabase.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String temp = dataSnapshot.getValue(String.class);
                        // Check for null
                        if (temp == null) {


                            String userId = mFirebaseDatabase.push().getKey();
                            mFirebaseDatabase.child(phone).setValue(search_id);

                            Intent i = new Intent(getApplicationContext(), channels.class);
                            startActivity(i);
                            finish();
                            return;
                        } else {
                            temp += "-" + search_id;
                            mFirebaseDatabase.child(phone).setValue(temp);

                            Intent i = new Intent(getApplicationContext(), channels.class);
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
                        Toast.makeText(getApplicationContext(), "Failed to fetch data rsult", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        }
    }
    @Override
    public void onBackPressed() {
        Intent i;
            i = new Intent(this,channels.class);
        startActivity(i);
        finish();
    }

}
