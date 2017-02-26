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

        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.View;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.aroras.iotdataviewer.R;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Vector;

public class display extends AppCompatActivity {
    public String TAG = "hello";
    public String channel_id,url,url1;
    public HashMap<String, String> channel = new HashMap<>();
    public HashMap<String, String> field = new HashMap<>();
    public TextView tv,tv1;
   public RecyclerView rv1;
    ArrayList<Vector> mDataset=new ArrayList<Vector>();
    int use_default_key=0;
     String api;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog

        tv=(TextView)findViewById(R.id.tv);
        tv1=(TextView)findViewById(R.id.tv1);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("iot", 0);
        api = pref.getString("apikey", null);

        Intent i = getIntent();
        channel_id = i.getStringExtra("chid");
       // Toast.makeText(this,channel_id,Toast.LENGTH_SHORT).show();
        url = "https://api.thingspeak.com/channels/" + channel_id + ".json?api_key="+api;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),createchannel.class);
                i.putExtra("method","2");
                i.putExtra("cid",channel_id);
                startActivity(i);
                finish();
            }
        });
        new fetch().execute();
    }



    public class fetch extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            final String jsonStr = sh.makeServiceCall(url,"GET");

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject c = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    //     JSONArray channel = jsonObj.getJSONArray(channel_id);

                    // looping through All Contacts
                    // for (int i = 0; i < jsonObj.length(); i++)
                    {
                        //   JSONObject c = jsonObj.getJSONObject(i);

                        String id = c.getString("id");
                        String name = c.getString("name");
                        String description = c.getString("description");
                        String latitude = c.getString("latitude");
                        String longitude = c.getString("longitude");

                        String wr_key,rd_key;
                        if(!c.has("api_keys"))
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"public channel",Toast.LENGTH_SHORT).show();
                                }
                            });
                     //
                            wr_key=api;
                            rd_key=api;

                        }
                        else {
                            JSONArray key=c.getJSONArray("api_keys");
                            JSONObject w_key=key.getJSONObject(0);
                           wr_key=w_key.getString("api_key");
                            key = c.getJSONArray("api_keys");
                            w_key = key.getJSONObject(1);
                            rd_key = w_key.getString("api_key");

                        }

                        channel.put("id", id);
                        channel.put("name", name);
                        channel.put("description", description);
                        channel.put("latitude", latitude);
                        channel.put("longitude", longitude);

                        channel.put("write_key", wr_key);
                        channel.put("read_key", rd_key);


                        //  Toast.makeText(getApplicationContext(), id + " " + name + " " + description + " " + latitude + " " + longitude, Toast.LENGTH_SHORT).show();
                        // adding contact to contact list
                        Log.e(TAG, "Response from url:my " +rd_key+" "+wr_key +" "+ id  + " " + name + " " + description + " " + latitude + " " + longitude);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
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
                Log.e(TAG, "Couldn't get json from server.");
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

            // Dismiss the progress dialog

            String temp=channel.get("write_key");

            url1="https://thingspeak.com/channels/"+channel_id+"/feeds.json?api_key="+temp+"&results=0";

            new fetch2().execute();

        }

    }
  public void callll(){

        rv1 = (RecyclerView) findViewById(R.id.rv);
        rv1.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv1.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new com.example.aroras.iotdataviewer.channel.DividerItemDecoration(getApplicationContext());
        rv1.addItemDecoration(dividerItemDecoration);
        field_adapter mAdapter = new field_adapter(mDataset, getApplicationContext(), this);
        rv1.setAdapter(mAdapter);
    }
    public class fetch2 extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url1,"GET");

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject c1 = new JSONObject(jsonStr);
                    String c2 = c1.getString("channel");

                    JSONObject c= new JSONObject(c2);

                    Log.e(TAG,"ddd " +c2+" "+c.getString("field1"));
                    // Getting JSON Array node
                    //     JSONArray channel = jsonObj.getJSONArray(channel_id);

                    // looping through All Contacts
                    // for (int i = 0; i < jsonObj.length(); i++)
                    {
                        //   JSONObject c = jsonObj.getJSONObject(i);

                        if(c.has("field1"))
                            field.put("field1",c.getString("field1"));
                        if(c.has("field2"))
                            field.put("field2",c.getString("field2"));
                        if(c.has("field3"))
                            field.put("field3",c.getString("field3"));
                        if(c.has("field4"))
                            field.put("field4",c.getString("field4"));
                        if(c.has("field5"))
                            field.put("field5",c.getString("field5"));
                        if(c.has("field6"))
                            field.put("field6",c.getString("field6"));
                        if(c.has("field7"))
                            field.put("field7",c.getString("field7"));
                        if(c.has("field8"))
                            field.put("field8",c.getString("field8"));













                        //  Toast.makeText(getApplicationContext(), id + " " + name + " " + description + " " + latitude + " " + longitude, Toast.LENGTH_SHORT).show();
                        // adding contact to contact list
                        Log.e(TAG, "Response from url:my " +field.get("field1")+" "+field.get("field2")+" "+field.get("field3") +" "+ field.get("field4")  + " " + field.get("field5")+ " " + field.get("field6") + " " + field.get("field7") + " " + field.get("field8"));
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
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
                Log.e(TAG, "Couldn't get json from server.");
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

              tv.setText("Channel id : "+channel.get("id")+"\n"+"Channel name : "+channel.get("name"));
            tv1.setText("Description : "+channel.get("description"));
            for(int i=1;i<9;++i) {
                if(field.get("field"+String.valueOf(i))!=null) {
                    Vector<String> v = new Vector<String>(4, 2);
                    v.add(String.valueOf(i));
                    v.add(field.get("field" + String.valueOf(i)));
                    v.add(channel.get("write_key"));
                    v.add(channel_id);
                    mDataset.add(v);
                }
            }
                callll();
            progress.dismiss();


        }

    }


}
