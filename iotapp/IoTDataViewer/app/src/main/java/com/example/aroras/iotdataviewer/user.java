package com.example.aroras.iotdataviewer;

/**
 * Created by arora's on 2/23/2017.
 */

public class user {

        public String name;
        public String email;
        public String phone;
        public String password;
        public String api;


    // Default constructor required for calls to
        // DataSnapshot.getValue(User.class)
        public user() {
        }

        public user(String name, String email,String phone,String password,String api) {
            this.name = name;
            this.email = email;
            this.password=password;
            this.phone=phone;
            this.api=api;
        }
    }




