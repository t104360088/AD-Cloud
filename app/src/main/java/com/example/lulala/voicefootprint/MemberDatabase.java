package com.example.lulala.voicefootprint;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MemberDatabase {

    public String adname;
    public String url;

    public MemberDatabase(String adname, String url) {
        this.adname = adname;
        this.url = url;
    }

}
