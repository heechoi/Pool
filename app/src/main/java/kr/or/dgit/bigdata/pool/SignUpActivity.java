package kr.or.dgit.bigdata.pool;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class SignUpActivity extends AppCompatActivity {
    private int checkId=-1;
    private EditText id;
    private EditText pw1;
    private EditText pw2;
    private String http ="http://rkd0519.cafe24.com/pool/restJoin/";
    private String mno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Intent intent = getIntent();
        mno = intent.getStringExtra("mno");
        id = findViewById(R.id.id);
        pw1 = findViewById(R.id.pw1);
        pw2 = findViewById(R.id.pw2);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    public void onSignUp(View view) {
        Pattern patternPw = Pattern.compile("(^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[~!@#$%^&*])[A-Za-z0-9~!@#$%^&*]{8,20}$)");
        Matcher matcherPw = patternPw.matcher(pw1.getText().toString());

        if(checkId<0){
            Toast.makeText(this,"아이디 중복을 확인해 주세요",Toast.LENGTH_SHORT).show();
            return;
        }
        if(id.getText().toString().equals("")||pw1.getText().toString().equals("")||pw2.getText().toString().equals("")){
            Toast.makeText(this,"필수 사항입니다. 모두 입력해 주세요",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!matcherPw.find()){
            AlertDialog.Builder alert = new AlertDialog.Builder(this,R.style.AlertDialog);
            alert.setTitle("비밀번호 사용 불가");
            alert.setMessage("비밀번호 정규식이 일치하지 않습니다\n비밀번호는8~20자,최소한 한번 이상의 영문,숫자,특수문자(~!@#$%^&*)만 허용됩니다").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    dialog.dismiss();
                }
            });
            alert.create();
            alert.show();
            return;
        }
        if(!pw1.getText().toString().equals(pw2.getText().toString())){
            Toast.makeText(this,"비밀번호가 일치하지 않습니다 비밀번호를 확인해주세요",Toast.LENGTH_SHORT).show();
            pw2.requestFocus();
            return;
        }
        String signUpHttp = http+"signUp";
        String[] arrQueryname = {"id","pw","mno"};
        String[] arrQuery = {id.getText().toString(), pw1.getText().toString(),mno};

        HttpRequestTack httpRequestTack = new HttpRequestTack(this,mHandler2,arrQuery,arrQueryname,"POST","회원가입...");
        httpRequestTack.execute(signUpHttp);

    }

    public void idCheckClick(View view) {
        Pattern patternId = Pattern.compile("(^(?=.*[a-zA-Z])[A-Za-z0-9]{6,15}$)");
        Matcher matcherId = patternId.matcher(id.getText().toString());

        if(!matcherId.find()){
            AlertDialog.Builder alert = new AlertDialog.Builder(this,R.style.AlertDialog);
            alert.setTitle("아이디 사용 불가");
            alert.setMessage("아이디 정규식이 일치하지 않습니다\n아이디는6~15자,영문,숫자만 허용됩니다").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    dialog.dismiss();
                }
            });
            alert.create();
            alert.show();
            return;
        }

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

                        AlertDialog.Builder alertDialBuilder = new AlertDialog.Builder(SignUpActivity.this,R.style.AlertDialog);
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

                        AlertDialog.Builder alert = new AlertDialog.Builder(SignUpActivity.this,R.style.AlertDialog);
                        alert.setTitle("회원가입 안내");
                        alert.setMessage("회원가입 되었습니다").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                overridePendingTransition(R.anim.login,R.anim.login_out);
                            }
                        });
                        alert.create();
                        alert.show();

                    }
                }
                break;
            }
        }
    };


}
