package kr.or.dgit.bigdata.pool;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class SignUpActivity extends AppCompatActivity {
    private int checkId=-1;
    private EditText id;
    private EditText pw1;
    private EditText pw2;
    private String http ="http://192.168.0.239:8080/pool/restJoin/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        id = findViewById(R.id.id);
        pw1 = findViewById(R.id.pw1);
        pw2 = findViewById(R.id.pw2);
    }

    public void onSignUp(View view) {
        if(checkId<0){
            Toast.makeText(this,"아이디 중복을 확인해 주세요",Toast.LENGTH_SHORT).show();
            return;
        }
        if(id.getText().toString().equals("")||pw1.getText().toString().equals("")||pw2.getText().toString().equals("")){
            Toast.makeText(this,"필수 사항입니다. 모두 입력해 주세요",Toast.LENGTH_SHORT).show();
            return;
        }
        String signUpHttp = http+"signUp";
        String[] arrQueryname = {"id","pw","mno"};
        String[] arrQuery = {id.getText().toString(), pw1.getText().toString()};

        HttpRequestTack httpRequestTack = new HttpRequestTack(this,mHandler2,arrQuery,arrQueryname,"POST","회원가입...");
        httpRequestTack.execute(signUpHttp);

    }

    public void idCheckClick(View view) {
        String idCheckHttp = http+"idCheck";
        String[] arrQueryname ={"id"};
        String[] arrQuery={id.getText().toString()};

        HttpRequestTack httpRequestTack = new HttpRequestTack(this,mHandler,arrQuery,arrQueryname,"POST","아이디 검색 중...");
        httpRequestTack.execute(idCheckHttp);
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{
                    String result = (String)msg.obj;
                    if(result.equals("ok")){

                        AlertDialog.Builder alertDialBuilder = new AlertDialog.Builder(SignUpActivity.this);
                        alertDialBuilder.setTitle("아아디 중복 확인");

                        alertDialBuilder.setMessage("사용가능한 아이디 입니다.\n사용하시겠습니까?").setCancelable(false)
                                .setPositiveButton("사용", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        dialogInterface.cancel();
                                        checkId=1;
                                        pw1.requestFocus();
                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                        id.setText("");
                                        id.requestFocus();
                                        checkId=-1;
                                    }
                                });

                        AlertDialog alertDialog = alertDialBuilder.create();
                        alertDialog.show();

                    }
                    if(result.equals("no")){
                        Toast.makeText(getApplication(),"사용불가능한 아이디 입니다\n 다른 아아디로 가입해 주세요.(중복 사용 불가)",Toast.LENGTH_SHORT).show();
                        id.setText("");
                        id.requestFocus();
                        checkId=-1;
                        return;
                    }
                }
                break;
            }
        }
    };
    @SuppressLint("HandlerLeak")
    Handler mHandler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{
                    String result = (String)msg.obj;
                    if(result.equals("success")){
                        Toast.makeText(SignUpActivity.this,"회원가입되었습니다",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    };
}
