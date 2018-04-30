package com.example.lulala.voicefootprint;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity implements ChildEventListener {

    ArrayAdapter<String>fileDBAdapter;

    private FirebaseAuth fileAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = this.getIntent();
        final String uid = intent.getStringExtra("UID");

        final ListView list = (ListView) findViewById(R.id.listView);
        fileDBAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1);
        list.setAdapter(fileDBAdapter);

        fileAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.e("Nick","onAuthStateChanged");
                FirebaseUser user = firebaseAuth.getCurrentUser();

                FirebaseDatabase fireDB = FirebaseDatabase.getInstance();
                DatabaseReference myRef = fireDB.getReference(uid);
                myRef.addChildEventListener(MainActivity.this);

               /* if (getReferrer(uid)==null){
                    first_login_dialog();
                }  */
            }
        };
    }

    public void first_login_dialog(){
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("You  don't  have  any  advertisement")
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(" You  can  contact  us  to  add  the  advertisement ")
                .setCancelable(false)  //關閉手機功能鍵
                .setPositiveButton("I  know",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    //創建標題功能項
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //控制標題功能項
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_contact:
                contact();
                break;

            case R.id.action_sign_out:
                sign_out_dialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //控制功能鍵
    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){

        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){   //確定按下退出鍵and防止重複按下退出鍵
            sign_out_dialog();
        }
        return false;
    }

    //聯絡我們
    public void contact(){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "t104360452@ntut.org.tw" , null));
        //emailIntent.putExtra(Intent.EXTRA_SUBJECT, "xxx");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "If you want to add or delete AD,You can tell us");
        startActivity(Intent.createChooser(emailIntent, "SEND EMAIL"));
    }

    //彈跳視窗
    public void sign_out_dialog(){
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Alert  Dialog")
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("Do you want to sign out ?")
                .setCancelable(false)  //關閉手機功能鍵
                .setPositiveButton("YES",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("NO",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void onStart(){
        super.onStart();
        fileAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop(){
        if(authStateListener !=null){
            fileAuth.removeAuthStateListener(authStateListener);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        fileAuth.signOut();
        super.onDestroy();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot,String s){
        fileDBAdapter.add(
                String.valueOf(dataSnapshot.child("name").getValue())
        );
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot){
        fileDBAdapter.remove(
                String.valueOf(dataSnapshot.child("name").getValue())
        );
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot,String s){}

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot,String s){}

    @Override
    public void onCancelled(DatabaseError databaseError){}

}
