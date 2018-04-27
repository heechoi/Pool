package kr.or.dgit.bigdata.pool;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class ClassboardUpdateActivity extends AppCompatActivity implements View.OnClickListener,View.OnFocusChangeListener{
    ClassBoardInsertActivity.CustomDialog customDialog;
    File filePath;
    int reqWidth;
    int reqHeight;
    String galleryPath;
    String http = "http://192.168.0.60:8080/pool/restclassboard/";
    ImageView img_btn;
    ImageView imgSelect;
    EditText etcontent;
    EditText etTitle;
    TextView tvclass;
    String time;
    String level;
    int bno;
    SharedPreferences sp;
    SharedPreferences sp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classboard_update);
        Intent intent = getIntent();
        bno = intent.getIntExtra("bno",0);
        img_btn = findViewById(R.id.img_btn);
        img_btn.setOnClickListener(this);
        imgSelect = findViewById(R.id.selectImg);
        reqWidth = getResources().getDimensionPixelSize(R.dimen.request_image_width);
        reqHeight = getResources().getDimensionPixelSize(R.dimen.request_image_height);
        etcontent = findViewById(R.id.etcontent);
        tvclass = findViewById(R.id.tvclassselect);
        tvclass.setOnClickListener(this);
        etTitle = findViewById(R.id.ettitle);
        etTitle.setOnFocusChangeListener(this);
        String read = http+"read";
        new HttpRequestTack(this,mHandler,new String[]{bno+""},new String[]{"bno"},"POST","정보를 가져오는 중입니다.").execute(read);
    }
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String result = (String) msg.obj;
                    Log.d("bum", "googogogoggoo"+result);
                    try {
                        JSONArray ja = new JSONArray(result);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    @Override
    public void onClick(View view) {

    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }
}
