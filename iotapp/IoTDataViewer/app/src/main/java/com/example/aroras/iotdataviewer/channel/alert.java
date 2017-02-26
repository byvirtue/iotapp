package com.example.aroras.iotdataviewer.channel;

/**
 * Created by arora's on 2/26/2017.
 */

public class alert {
    public String reg_id;
    public String api_id;
    public String ch_id;
    public String field;
    public String threshold;


    // Default constructor required for calls to
    // DataSnapshot.getValue(alert.class)
    public alert() {
    }

    public alert(String reg_id, String api_id,String ch_id,String field,String threshold) {
        this.reg_id = reg_id;
        this.api_id = api_id;
        this.ch_id=ch_id;
        this.field=field;
        this.threshold=threshold;
    }
}
