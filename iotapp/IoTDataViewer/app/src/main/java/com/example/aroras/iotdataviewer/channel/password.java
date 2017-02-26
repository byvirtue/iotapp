package com.example.aroras.iotdataviewer.channel;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.aroras.iotdataviewer.MainActivity;
import com.example.aroras.iotdataviewer.R;
import com.example.aroras.iotdataviewer.user;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class password extends AppCompatActivity {
    String phone,newp ;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    boolean valid=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText newpass = (EditText) findViewById(R.id.editText9);
        Intent ii = getIntent();

        phone = ii.getStringExtra("phone");
        Button btn = (Button) findViewById(R.id.button5);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newp = newpass.getText().toString();
                if (newp.isEmpty() || newp.length() < 8) {
                    newpass.setError("minimun 8 characters");
                    valid = false;
                    newpass.requestFocus();
                }
                if (valid)
                {
                    mFirebaseDatabase.child(phone).child("password").setValue(newp);
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                SharedPreferences pref = getApplicationContext().getSharedPreferences("iot", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("phone", null);
                editor.putString("apikey", null);
                editor.commit();
                startActivity(i);
                finish();
            }
            }
        });
    }
}
