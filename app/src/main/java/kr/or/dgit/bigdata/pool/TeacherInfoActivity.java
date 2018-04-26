package kr.or.dgit.bigdata.pool;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class TeacherInfoActivity extends AppCompatActivity {
    private ImageView tImage;
    int tno;
    private String http ="http://192.168.0.239:8080/pool/restInfoUpdate/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);
        SharedPreferences t = getSharedPreferences("Admin",MODE_PRIVATE);
        tno = t.getInt("tno",0);

        String isleave = http+"findTeacher";

        String[] arrQueryname ={"tno"};
        String[] arrQuery={String.valueOf(tno)};

        HttpRequestTack httpRequestTack = new HttpRequestTack(this,mHandler,arrQuery,arrQueryname,"POST","탈퇴처리중..",1);
        httpRequestTack.execute(isleave);

        tImage = findViewById(R.id.tImage);

    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1: {
                    String result = (String) msg.obj;
                }
                break;
            }
        }
    };

}
