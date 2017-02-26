package com.example.aroras.iotdataviewer.channel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aroras.iotdataviewer.R;
import com.example.aroras.iotdataviewer.conf.Config;
import com.example.aroras.iotdataviewer.user;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class field_display extends AppCompatActivity {

    String curr,prev,chart,refresh,ff,ch_id,api_id;
    TextView tv,tv1;
    ImageView iv;
    int prog=10000;
    int thread_stop=0;

    int posi;
    String userId;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    ProgressDialog progress;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("alert");
        iv=(ImageView)findViewById(R.id.imageView);
         progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
progress.show();
        setSupportActionBar(toolbar);
        final android.os.Handler mHandler=new android.os.Handler();
        Intent i=getIntent();
        chart=i.getStringExtra("charts");
        refresh=i.getStringExtra("refresh");
        ff=i.getStringExtra("field");
        ch_id=i.getStringExtra("ch_id");
        api_id=i.getStringExtra("api_id");
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


       tv=(TextView)findViewById(R.id.tv3) ;

        tv1=(TextView)findViewById(R.id.tv4) ;
        Button btt = (Button)findViewById(R.id.button6) ;
        Button bttx = (Button)findViewById(R.id.button7) ;
        final EditText ett = (EditText)findViewById(R.id.editText10);
        btt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posi = spinner.getSelectedItemPosition();
                Log.e("aa", String.valueOf(posi));
                    switch(posi)
                    {
                        case 0 : prog =5000;
                                break;
                        case 1 : prog =15000;
                            break;
                        case 2 : prog =30000;
                            break;
                        case 3 : prog =300000;
                            break;
                        case 4 : prog =1800000;
                            break;
                        case 5 : prog =3600000;
                            break;
                        case 6 : prog =86400000;
                            break;
                    }
                Toast.makeText(getApplicationContext(),
                        "Frequency has been set to : "+String.valueOf(prog/1000)+" seconds",
                        Toast.LENGTH_LONG)
                        .show();

            }
        });
        bttx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                String regId = pref.getString("regId", null);
                String threshold = ett.getText().toString();
                alert all = new alert(regId,api_id,ch_id,ff,threshold);
                if(regId!=null && threshold!=null && threshold.length()>0) {
                    if (TextUtils.isEmpty(userId)) {
                        userId = mFirebaseDatabase.push().getKey();
                        userId = ch_id + ff;
                    }

                    mFirebaseDatabase.child(userId).setValue(all);
                }
                else
                    Toast.makeText(getApplicationContext(),"could not update threshold",Toast.LENGTH_SHORT).show();
            }
        });
        WebView webView=(WebView)findViewById(R.id.viww);
        webView.setWebViewClient(new Callback());
        WebSettings webSettings=webView.getSettings();
       // webSettings.setBuiltInZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        Double val = new Double(width)/new Double(425);
        val = val * 100d;
         val.intValue();
        webView.setPadding(0, 0, 0, 0);
       webView.setInitialScale(val.intValue());
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        webView.loadUrl(chart);

      /*  Display display = getWindowManager().getDefaultDisplay();
        int width=display.getWidth();

        String data="<html><head><title>Example</title><meta name=\"viewport\"\"content=\"width="+width+", initial-scale=0.65 \" /></head>";
        data=data+"<body><center><img width=\""+width+"\" src=\""+chart+"\" /></center></body></html>";

        webView.loadData(data, "text/html", null);
*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (thread_stop==0) {

                    try {
                        Thread.sleep(prog);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                        new field_display.fetch().execute();
                        //root.setBackgroundColor(Color.WHITE);



                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {

                        }

                    });
                }

            }

        }).start();


    }

    @Override
    public void onBackPressed() {

        thread_stop=1;
        super.onBackPressed();

    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,
                                                String url) {
            return (false);
        }


    }

    public class fetch extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            final String jsonStr = sh.makeServiceCall(refresh,"GET");

            Log.e("hi", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject c = new JSONObject(jsonStr);

                    JSONArray key=c.getJSONArray("feeds");



                        JSONObject pr=key.getJSONObject(0);
                      prev=pr.getString(ff);
                        pr=key.getJSONObject(1);
                        curr=pr.getString(ff);




                } catch (final JSONException e) {
                    //Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "No data yet",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                //Log.e(TAG, "Couldn't get json from server.");
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

            tv.setText("Current Value: " + curr);
            tv1.setText("Previous Value: " + prev);
            try {
                if (curr != null && prev != null) {
                    if (Float.parseFloat(curr) > Float.parseFloat(prev))
                        iv.setBackgroundResource(R.mipmap.up);
                    else
                        iv.setBackgroundResource(R.mipmap.down);

                }

            } catch (NumberFormatException e) {

            }
            progress.dismiss();
        }
    }





}
