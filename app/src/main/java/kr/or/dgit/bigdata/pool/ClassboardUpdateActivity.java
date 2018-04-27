package kr.or.dgit.bigdata.pool;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class ClassboardUpdateActivity extends AppCompatActivity {
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
    ImageView check_Class;
    ImageView check_title;
    int bno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classboard_update);
        Intent intent = getIntent();
        bno = intent.getIntExtra("bno",0);

    }
}
