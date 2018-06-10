package com.example.lulala.voicefootprint;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acrcloud.rec.sdk.ACRCloudConfig;
import com.acrcloud.rec.sdk.ACRCloudClient;
import com.acrcloud.rec.sdk.IACRCloudListener;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity implements IACRCloudListener {

    private ACRCloudClient mClient;
    private ACRCloudConfig mConfig;

    private TextView mVolume, mResultTitle, mResultUrl, tv_time;

    private boolean mProcessing = false;
    private boolean initState = false;

    private String path = "";

    private long startTime = 0;
    private long stopTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle(R.string.SearchActivity_title);

        path = Environment.getExternalStorageDirectory().toString()
                + "/acrcloud/model";

        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }

        mVolume = (TextView) findViewById(R.id.volume);
        mResultTitle = (TextView) findViewById(R.id.resultTitle);
        mResultUrl= (TextView) findViewById(R.id.resultUrl);
        tv_time = (TextView) findViewById(R.id.time);

        ImageButton startBtn = (ImageButton) findViewById(R.id.start);
        ImageButton stopBtn = (ImageButton) findViewById(R.id.stop);
        ImageButton cancelBtn = (ImageButton) findViewById(R.id.cancel);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                start();
                Toast.makeText(getApplicationContext(), "Start  searching ", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stop();
                Toast.makeText(getApplicationContext(), "Stop  to  search", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancel();
                Toast.makeText(getApplicationContext(), "Stop  searching", Toast.LENGTH_SHORT).show();
            }
        });


        this.mConfig = new ACRCloudConfig();
        this.mConfig.acrcloudListener = this;

        // If you implement IACRCloudResultWithAudioListener and override "onResult(ACRCloudResult result)", you can get the Audio data.
        //this.mConfig.acrcloudResultWithAudioListener = this;

        this.mConfig.context = this;
        this.mConfig.host = "xxx"; //輸入acr-cloud主機資訊
        this.mConfig.dbPath = path; // offline db path, you can change it with other path which this app can access.
        this.mConfig.accessKey = "xxx";
        this.mConfig.accessSecret = "xxx";
        this.mConfig.protocol = ACRCloudConfig.ACRCloudNetworkProtocol.PROTOCOL_HTTP; // PROTOCOL_HTTPS
        this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE;

        //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_LOCAL;
        //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_BOTH;

        this.mClient = new ACRCloudClient();
        // If reqMode is REC_MODE_LOCAL or REC_MODE_BOTH,
        // the function initWithConfig is used to load offline db, and it may cost long time.
        this.initState = this.mClient.initWithConfig(this.mConfig);
        if (this.initState) {
            this.mClient.startPreRecord(3000); //start prerecord, you can call "this.mClient.stopPreRecord()" to stop prerecord.
        }
    }

    public void start() {
        if (!this.initState) {
            Toast.makeText(this, "init error", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mProcessing) {
            mProcessing = true;
            mVolume.setText("");
            mResultTitle.setText("");
            mResultUrl.setText("");
            if (this.mClient == null || !this.mClient.startRecognize()) {
                mProcessing = false;
                mResultTitle.setText("start error!");
            }
            startTime = System.currentTimeMillis();
        }
    }

    protected void stop() {
        if (mProcessing && this.mClient != null) {
            this.mClient.stopRecordToRecognize();
        }
        mProcessing = false;

        stopTime = System.currentTimeMillis();
    }

    protected void cancel() {
        if (mProcessing && this.mClient != null) {
            mProcessing = false;
            this.mClient.cancel();
            tv_time.setText("");
            mResultTitle.setText("");
            mResultUrl.setText("");
        }
    }

    // Old api
    @Override
    public void onResult(String result) {
        if (this.mClient != null) {
            this.mClient.cancel();
            mProcessing = false;
        }

        String tres ="";
        String showAdUrl = "";

        try {
            JSONObject j = new JSONObject(result);
            JSONObject j1 = j.getJSONObject("status");
            int j2 = j1.getInt("code");
            if(j2 == 0){
                JSONObject metadata = j.getJSONObject("metadata");
                //
                if (metadata.has("humming")) {
                    JSONArray hummings = metadata.getJSONArray("humming");
                    for(int i=0; i<hummings.length(); i++) {
                        JSONObject tt = (JSONObject) hummings.get(i);
                        String title = tt.getString("title");
                        JSONArray artistt = tt.getJSONArray("artists");
                        JSONObject art = (JSONObject) artistt.get(0);
                        String artist = art.getString("name");
                        tres = tres + (i+1) + ".  " + title + "\n";
                    }
                }
                if (metadata.has("music")) {
                    JSONArray musics = metadata.getJSONArray("music");
                    for(int i=0; i<musics.length(); i++) {
                        JSONObject tt = (JSONObject) musics.get(i);
                        String title = tt.getString("title");
                        JSONArray artistt = tt.getJSONArray("artists");
                        JSONObject art = (JSONObject) artistt.get(0);
                        String artist = art.getString("name");
                        tres = tres + (i+1) + ".Title: " + title + "\nArtist: " + artist + "\n";
                    }
                }
                if (metadata.has("streams")) {
                    JSONArray musics = metadata.getJSONArray("streams");
                    for(int i=0; i<musics.length(); i++) {
                        JSONObject tt = (JSONObject) musics.get(i);
                        String title = tt.getString("title");
                        String channelId = tt.getString("channel_id");
                        tres = tres + (i+1) + ".Title: " + title + "    Channel Id: " + channelId + "\n";
                    }
                }
                if (metadata.has("custom_files")) {
                    JSONArray musics = metadata.getJSONArray("custom_files");
                    for(int i=0; i<musics.length(); i++) {
                        JSONObject tt = (JSONObject) musics.get(i);
                        String title = tt.getString("title");
                        String adUrl = tt.getString("adUrl");
                        tres = tres + (i+1) + ".Title: " + title ;
                        showAdUrl = "Url: " + adUrl ;


                     /*   Uri uri = Uri.parse(title);               //跳轉網頁
                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(it); */
                    }
                }
              //  tres = tres + "\n" + result;  //搜尋結果與系統資訊相加
            }else{
                tres = result;
            }
        } catch (JSONException e) {
            tres = result;
            e.printStackTrace();
        }

        mResultTitle.setText(tres);  //秀出結果
        mResultUrl.setText(showAdUrl);
        mResultUrl.setAutoLinkMask(Linkify.WEB_URLS);
        mResultUrl.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onVolumeChanged(double volume) {
        long time = (System.currentTimeMillis() - startTime) / 1000 ;
        mVolume.setText("Volume：" + volume + "\n\nRecording time：" + time + " s");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("MainActivity", "release");
        if (this.mClient != null) {
            this.mClient.release();
            this.initState = false;
            this.mClient = null;
        }
    }
}
