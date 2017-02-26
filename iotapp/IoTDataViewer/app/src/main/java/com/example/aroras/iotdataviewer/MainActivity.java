package com.example.aroras.iotdataviewer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aroras.iotdataviewer.channel.channels;
import com.example.aroras.iotdataviewer.conf.Config;
import com.example.aroras.iotdataviewer.utils.NotificationUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText inputName, inputEmail,inputph,password,loginph,loginpswd,inputapi;
    private Button btnSave,btnlogin;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String userId;

    String phno ;

    public void pass()
    {
        final EditText txtUrl = new EditText(this);

      //  txtUrl.setHint("");

        new AlertDialog.Builder(this)
                .setTitle("Forgot password")
                .setMessage("Enter phone number")
                .setView(txtUrl)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        phno = txtUrl.getText().toString();

                        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                user usr = snapshot.getValue(user.class);
                                if (usr!=null) {
                                    Intent i = new Intent(getApplicationContext(), Otp.class);
                                    i.putExtra("phone",phno);
                                    i.putExtra("name", " ");
                                    i.putExtra("email", " ");
                                    i.putExtra("pswd", " ");
                                    i.putExtra("api", " ");
                                    i.putExtra("method","2");
                                    startActivity(i);
                                    finish();
                                }
                                else
                                    Toast.makeText(getApplicationContext(),"user does not exist",Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Toast.makeText(getApplicationContext(),"Failed to fetch data rsult",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        SharedPreferences pref = getApplicationContext().getSharedPreferences("iot", 0);
        final String ph = pref.getString("phone", null);
        final String ak = pref.getString("apikey", null);
        if(ph!=null && ak!=null)
        {
            Intent i=new Intent(getApplicationContext(),channels.class);
            startActivity(i);
            finish();
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();


                }
            }
        };

        displayFirebaseRegId();




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ScrollView scrollView=(ScrollView)findViewById(R.id.scroll);
                scrollView.scrollTo(0,800);

            }
        });

       // txtDetails = (TextView) findViewById(R.id.txt_user);
        inputName = (EditText) findViewById(R.id.editText5);
        inputph = (EditText) findViewById(R.id.editText7);
        password = (EditText) findViewById(R.id.editText8);
        inputEmail = (EditText) findViewById(R.id.editText6);
        btnSave = (Button) findViewById(R.id.button2);
        btnlogin = (Button) findViewById(R.id.button1);
        loginph=(EditText)findViewById(R.id.editText);
        loginpswd=(EditText)findViewById(R.id.editText4);
        inputapi = (EditText)findViewById(R.id.editText2);


        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        // store app title to 'app_title' node
        mFirebaseInstance.getReference("app_title").setValue("Realtime Database");

        // app_title change listener
        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String name = inputName.getText().toString();
                String email = inputEmail.getText().toString();
                String phone= inputph.getText().toString();
                String pswd= password.getText().toString();
                String api= inputapi.getText().toString();
                btnSave.setClickable(false);
                boolean valid=true;
                if(!(inputName.getText().toString().length()>3)){
                    inputName.setError("min 4 characters ");
                    inputName.requestFocus();
                    valid=false;
                }



                if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inputEmail.setError("enter a valid email address");
                    valid = false;

                    inputEmail.requestFocus();
                }

                if (phone.isEmpty() || phone.length()!=10) {
                    inputph.setError("enter a valid 10 digit phone number");
                    valid = false;
                    inputph.requestFocus();
                }

                if (pswd.isEmpty() || password.length() < 8 ) {
                    password.setError("minimun 8 characters");
                    valid = false;
                    password.requestFocus();
                }

                if(name.length() >0 && email.length() >0 && phone.length()>0 && pswd.length()>0 && valid) {
                    // Check for already existed userId
                    if (isNetworkAvailable()) {
                        if (TextUtils.isEmpty(userId)) {
                            if(api.length()>0)
                              signup(name,email,phone,pswd,api);
                            else {

                                signup(name, email, phone, pswd, getString(R.string.our_api));

                                 }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "You are not connected to Network", Toast.LENGTH_SHORT).show();
                        btnSave.setClickable(true);
                    }
                }
                else btnSave.setClickable(true);

            }
        });



        TextView forgot = (TextView)findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    pass();

            }
        });




   btnlogin.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           btnlogin.setClickable(false);
           String phone=loginph.getText().toString();
           String pswd=loginpswd.getText().toString();
           boolean valid=true;
           if (phone.isEmpty() || phone.length()!=10) {
               loginph.setError("enter a valid 10 digit phone number");
               valid = false;
               loginph.requestFocus();
           }
           if (pswd.isEmpty() ) {
               loginpswd.setError("Required");
               valid = false;
               loginpswd.requestFocus();
           }

           if( phone.length()>0 && pswd.length()>0 && valid ) {

               if (isNetworkAvailable()) {
                   logincheck(phone, pswd);
               } else {
                   btnlogin.setClickable(true);
                   Toast.makeText(getApplicationContext(), "You are not connected to Network", Toast.LENGTH_SHORT).show();

               }
           }
           else   btnlogin.setClickable(true);

       }
   });

    }




    private void logincheck(final String key, final String pswd) {

        // User data change listener


        mFirebaseDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    user usr = dataSnapshot.getValue(user.class);
                    if (usr != null) {
                        if (pswd.equals(usr.password)) {
                            Intent i = new Intent(getApplicationContext(), channels.class);
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("iot", 0);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("phone", loginph.getText().toString());
                            editor.putString("apikey", usr.api);
                            editor.commit();

                            startActivity(i);
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                            btnlogin.setClickable(true);
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                        btnlogin.setClickable(true);
                    }



                // Display newly updated name and email
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
                Toast.makeText(getApplicationContext(),"Failed to fetch data rsult",Toast.LENGTH_SHORT).show();
                btnlogin.setClickable(true);
            }
        });
    }

   public void signup(final String name, final String email, final String key, final String pswd,final String api) {

        // User data change listener
        mFirebaseDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user usr = dataSnapshot.getValue(user.class);
                // Check for null
                if (usr == null) {

                    Log.e(TAG, "User data is null!");

                    //Toast.makeText(getApplicationContext(),inputph.getText().toString(),Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(getApplicationContext(), Otp.class);
                    i.putExtra("phone", key);
                    i.putExtra("name", name);
                    i.putExtra("email", email);
                    i.putExtra("pswd", pswd);
                    i.putExtra("api", api);
                    i.putExtra("method","1");
                    startActivity(i);
                    finish();
                    return ;
                }
                else
                {
                    inputph.setText("");
                    btnSave.setClickable(true);
                    Toast.makeText(getApplicationContext(),"Phone number already registered",Toast.LENGTH_SHORT).show();
                    return;
                }

                // Display newly updated name and email
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
                Toast.makeText(getApplicationContext(),"Failed to fetch data rsult",Toast.LENGTH_SHORT).show();
            }
        });

    }




    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //@Override
  /*  public void onBackPressed() {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

    }*/
    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

}
