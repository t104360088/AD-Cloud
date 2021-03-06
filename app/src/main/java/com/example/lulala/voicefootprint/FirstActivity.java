package com.example.lulala.voicefootprint;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class FirstActivity extends AppCompatActivity {

    private Button btnLogin,btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        btnLogin = (Button)findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirstActivity.this, LoginActivity.class));
            }
        });

        btnSearch = (Button)findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirstActivity.this, SearchActivity.class));
            }
        });


    }


    //創建標題功能項
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.another, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //控制標題功能項
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:
                exit_dialog();
                break;

            case R.id.action_help:
                help_dialog();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    //控制功能鍵
    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){

        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){   //確定按下退出鍵and防止重複按下退出鍵
            exit_dialog();
        }
        return false;
    }

    //說明
    public void help_dialog(){
        AlertDialog dialog = new AlertDialog.Builder(FirstActivity.this)
                .setTitle(R.string.alert_dialog_help_title)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(R.string.alert_dialog_help_message_first)
                .setCancelable(false)  //關閉手機功能鍵
                .setPositiveButton(R.string.alert_dialog_close_btn,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    //離開的彈跳視窗
    public void exit_dialog(){
        AlertDialog dialog = new AlertDialog.Builder(FirstActivity.this)
                .setTitle(R.string.alert_dialog_title)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(R.string.alert_dialog_getout_message)
                .setCancelable(false)  //關閉手機功能鍵
                .setPositiveButton(R.string.alert_dialog_positive_btn,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_negative_btn,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }
}
