package com.example.lulala.voicefootprint;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;


public class AddAdvertisementActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText inputAdName, inputUrl;
    private TextView showAudioPath;
    private Button btnAdd,btnRevise;
    private ImageButton btnChooseAudio;
    private ProgressBar progressBar;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advertisement);
        setTitle(R.string.AddAdvertisementActivity_title);


        // 在toolbar展示icon
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);


        inputAdName = (EditText) findViewById(R.id.adname);
        inputUrl = (EditText) findViewById(R.id.url);
        showAudioPath = (TextView) findViewById(R.id.show_audio_path);
        btnAdd = (Button) findViewById(R.id.btn_add_ad);
        btnChooseAudio = (ImageButton) findViewById(R.id.btn_add_audio);
        btnRevise = (Button) findViewById(R.id.btn_ad_revise);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // 取得user的uid作為參考點，若無參考點，則建立一個參考點
        String uid = FirebaseAuth.getInstance().getUid();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(uid);

        mStorageRef = FirebaseStorage.getInstance().getReference(uid);


        btnChooseAudio.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
                picker.setType("audio/*");
                picker.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                Intent destIntent = Intent.createChooser(picker, null);
                startActivityForResult(destIntent, 101);

            }
        });


        //新增廣告功能
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                final String input_adname = inputAdName.getText().toString();
                final String input_url = inputUrl.getText().toString();
                final String show_file_path = showAudioPath.getText().toString();

                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String adname = String.valueOf(dataSnapshot.child(input_adname).child("adname").getValue());

                        if (adname.equals(input_adname) && !input_adname.isEmpty() && !show_file_path.isEmpty() && !input_url.isEmpty()){
                            Toast.makeText(AddAdvertisementActivity.this, R.string.Ad_name_exist, Toast.LENGTH_SHORT).show();
                        }
                        if (input_adname.isEmpty() || show_file_path.isEmpty() || input_url.isEmpty()){
                            Toast.makeText(AddAdvertisementActivity.this, R.string.Ad_data_incomplete, Toast.LENGTH_SHORT).show();
                        }
                        if (!input_adname.isEmpty() && !show_file_path.isEmpty() && !input_url.isEmpty()) {
                            AlertDialog dialog = new AlertDialog.Builder(AddAdvertisementActivity.this)
                                    .setTitle(R.string.alert_dialog_add_title)
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setMessage(R.string.alert_dialog_add_message)
                                    .setCancelable(false)  //關閉手機功能鍵
                                    .setPositiveButton(R.string.alert_dialog_positive_btn,new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            createAD(input_adname, input_url, show_file_path);
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
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        //修改廣告資訊功能
        btnRevise.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){



                final String input_adname = inputAdName.getText().toString();
                final String input_url = inputUrl.getText().toString();
                final String show_file_path = showAudioPath.getText().toString();

                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                            String adname = String.valueOf(dataSnapshot.child(input_adname).child("adname").getValue());

                            if (adname.equals(input_adname) && !input_adname.isEmpty() && !show_file_path.isEmpty() && !input_url.isEmpty()){
                                    AlertDialog dialog = new AlertDialog.Builder(AddAdvertisementActivity.this)
                                            .setTitle(R.string.alert_dialog_revise_title)
                                            .setIcon(R.mipmap.ic_launcher)
                                            .setMessage(R.string.alert_dialog_revise_message)
                                            .setCancelable(false)  //關閉手機功能鍵
                                            .setPositiveButton(R.string.alert_dialog_positive_btn,new DialogInterface.OnClickListener(){
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    createAD(input_adname, input_url, show_file_path);
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

                            if (input_adname.isEmpty() || show_file_path.isEmpty() || input_url.isEmpty()){
                                Toast.makeText(AddAdvertisementActivity.this, R.string.Ad_data_incomplete, Toast.LENGTH_SHORT).show();
                            }
                            if (!adname.equals(input_adname) && !input_adname.isEmpty() && !show_file_path.isEmpty() && !input_url.isEmpty()){
                                Toast.makeText(AddAdvertisementActivity.this, R.string.Ad_name_not_exist, Toast.LENGTH_SHORT).show();
                            }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && data != null && data.getData() !=null) {
            if (resultCode == AddAdvertisementActivity.RESULT_OK) {

                    uri = data.getData();
                    String path = uri.getPath();
                    showAudioPath.setText(path);
            }
        }
    }



    private void createAD(final String input_adname, String input_url, String input_file_path) {



            progressBar.setVisibility(View.VISIBLE);  //顯現進度條

            //上架時間設定
            SimpleDateFormat   formatter   =   new SimpleDateFormat("yyyy年MM月dd日HH:mm");  //資料格式
            Date currentDate =  new Date(System.currentTimeMillis());  //取得系統時間
            String  systemTime   =   formatter.format(currentDate);  //將時間放入資料格式

            //上傳廣告音訊
            final StorageReference riversRef = mStorageRef.child(input_adname).child(uri.getLastPathSegment());  //創建使用者專屬的該廣告資料夾
            UploadTask uploadTask = riversRef.putFile(uri);




            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressBar.setVisibility(View.GONE);  //結束進度條
                    Toast.makeText(AddAdvertisementActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);  //結束進度條
                    Toast.makeText(AddAdvertisementActivity.this, R.string.Successfully, Toast.LENGTH_SHORT).show();
                }
            });




            //上傳廣告基本資訊
            mDatabaseRef.child(input_adname).child("adname").setValue(input_adname);
            mDatabaseRef.child(input_adname).child("url").setValue(input_url);
            mDatabaseRef.child(input_adname).child("upload time").setValue(systemTime);
            mDatabaseRef.child(input_adname).child("file path").setValue(riversRef.getPath());

            //設為空白
            inputUrl.setText("");
            inputAdName.setText("");
            showAudioPath.setText("");

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

}