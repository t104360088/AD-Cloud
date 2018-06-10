package com.example.lulala.voicefootprint;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class MainActivity extends AppCompatActivity implements ChildEventListener {

    ArrayAdapter<String>fileDBAdapter;

    private FirebaseAuth fileAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FloatingActionButton btnAddAd;
    private ListView list;

    final String[] list_item_function = {"移除廣告","前往廣告網址"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.MainActivity_title);

        // 在toolbar展示icon
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        //取得登入時的使用者ID
        final Intent intent = this.getIntent();
        final String uid = intent.getStringExtra("UID");

        list = (ListView) findViewById(R.id.listView);
        fileDBAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1);
        list.setAdapter(fileDBAdapter);


        //跳至新增廣告
        btnAddAd = (FloatingActionButton) findViewById(R.id.add_ad_btn);
        btnAddAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddAdvertisementActivity.class));
            }
        });


        fileAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.e("Nick","onAuthStateChanged");
                FirebaseUser user = firebaseAuth.getCurrentUser();

                FirebaseDatabase fireDB = FirebaseDatabase.getInstance();
                DatabaseReference mDatabaseRef = fireDB.getReference(uid);
                mDatabaseRef.addChildEventListener(MainActivity.this);

            }
        };





        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l)
            {

                AlertDialog.Builder dialog_list = new AlertDialog.Builder(MainActivity.this);
                dialog_list.setTitle(R.string.select_service_item);
                dialog_list.setItems(list_item_function, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                      final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference(uid);




                        //取得廣告名稱
                        final  String itemName = list.getItemAtPosition(position).toString();  //取得清單項目名稱(包含上架時間)
                        final  String adName = itemName.substring(0,itemName.length()-22);  //取得從0到由後往前數的第22個字元之間的值(為了扣除不要的字串)




                     /*   //取得下載網址
                        final StorageReference A =FirebaseStorage.getInstance().getReference(uid);
                        String b = A.child(adName).getDownloadUrl().toString(); //測試中


                        A.child(adName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Got the download URL for 'users/me/profile.png'
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        }); */

                        switch (which)
                        {
                            case 0:

                                AlertDialog delete_the_advertisement = new AlertDialog.Builder(MainActivity.this)
                                        .setTitle(R.string.delete_ad)
                                        .setIcon(R.mipmap.ic_launcher)
                                        .setMessage(R.string.alert_dialog_delete_message)
                                        .setCancelable(false)  //關閉手機功能鍵
                                        .setPositiveButton(R.string.alert_dialog_positive_btn,new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                               //監聽一次檔案變更，並刪除廣告的音訊檔案
                                                mDatabaseRef.child(adName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        String file_path =  String.valueOf(dataSnapshot.child("file path").getValue());
                                                        StorageReference mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://voicefootprint.appspot.com"+file_path);
                                                        fileDelete(mStorageRef);

                                                    }
                                                    @Override
                                                    public void onCancelled(DatabaseError error) {
                                                        // Failed to read value
                                                        Log.w("Failed to read value.", error.toException());
                                                    }
                                                });

                                                //刪除廣告的基本資訊
                                                mDatabaseRef.child(adName).removeValue();
                                                Toast.makeText(MainActivity.this, getString(R.string.toast_delete)+adName, Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton(R.string.alert_dialog_negative_btn, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .show();
                                delete_the_advertisement.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                                delete_the_advertisement.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));

                                break;


                            case 1:

                                //觸及後監聽清單項目的網址，取得網址後跳轉至該網址
                                mDatabaseRef.child(adName).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        String url =  String.valueOf(dataSnapshot.child("url").getValue());

                                        Uri uri = Uri.parse(url);               //跳轉網頁
                                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(it);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        // Failed to read value
                                        Log.w("Failed to read value.", error.toException());
                                    }
                                });

                                break;

                        /*    case 2:

                                mDatabaseRef.child(itemName).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        String url =  String.valueOf(dataSnapshot.child("url").getValue());

                                        Uri uri = Uri.parse(url);               //跳轉網頁
                                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(it);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        // Failed to read value
                                        Log.w("Failed to read value.", error.toException());
                                    }
                                });
                                break;  */


                        }
                    }
                });
                dialog_list.show();
            }
        });


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
            sign_out_dialog();
        }
        return false;
    }

    //功能項幫助
    //功能項登出
    public void help_dialog(){
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.alert_dialog_help_title)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(R.string.alert_dialog_help_message_main)
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

    //功能項聯絡我們
    public void contact(){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "t104360452@ntut.org.tw" , null));
        //emailIntent.putExtra(Intent.EXTRA_SUBJECT, "xxx");
        emailIntent.putExtra(Intent.EXTRA_TEXT, R.string.contact_send_mail_context);
        startActivity(Intent.createChooser(emailIntent, getString(R.string.contact_send_mail_title)));
    }

    //功能項登出
    public void sign_out_dialog(){
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.alert_dialog_title)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(R.string.alert_dialog_signout_message)
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

    @Override
    protected void onStart(){
        super.onStart();
        fileAuth.addAuthStateListener(authStateListener);

        int size=list.getAdapter().getCount();//获取数据集的个数

        if(size>0){
            fileDBAdapter.clear();//清空数据集
            fileDBAdapter.notifyDataSetChanged();//通知下观察者我更改了数据
            list.setAdapter(fileDBAdapter);//重新设置adapter
        }


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
                String.valueOf(dataSnapshot.child("adname").getValue()) +
                        "\n上架時間：" + String.valueOf(dataSnapshot.child("upload time").getValue())
        );

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot){

        fileDBAdapter.remove(
                String.valueOf(dataSnapshot.child("adname").getValue()) +
                        "\n上架時間：" + String.valueOf(dataSnapshot.child("upload time").getValue())
        );
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot,String s){}

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot,String s){}

    @Override
    public void onCancelled(DatabaseError databaseError){}

    //刪除音訊檔案
    public void fileDelete(final StorageReference mStorageRef){
        mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
