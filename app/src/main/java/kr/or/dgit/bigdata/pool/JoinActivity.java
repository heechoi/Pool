package kr.or.dgit.bigdata.pool;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class JoinActivity extends AppCompatActivity {
    private EditText mno;
    private Button mnoCheck;
    private String http ="http://192.168.0.239:8080/pool/restJoin/";
    private TextView nameText;
    private TextView genderText;
    private TextView tellText;
    private TextView emailText;
    private TextView ageText;
    private TextView infoLabel;
    private LinearLayout memberInfo;
    private String makeId;
    private boolean isleave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        mno = findViewById(R.id.mno);
        mnoCheck = findViewById(R.id.checkMno);
        nameText = findViewById(R.id.name);
        genderText = findViewById(R.id.gender);
        tellText = findViewById(R.id.tell);
        emailText = findViewById(R.id.email);
        ageText = findViewById(R.id.age);
        infoLabel = findViewById(R.id.info_label);
        memberInfo = findViewById(R.id.member_info);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    public void mCheck(View view) {
        if(mno.getText().toString().equals("")){
            Toast.makeText(this,"회원번호를 입력해 주세요",Toast.LENGTH_SHORT).show();
            mno.requestFocus();
            return;
        }

        String checkMnoHttp = http+"checkMno";
        String[] arrQueryname ={"mno"};
        String[] arrQuery={mno.getText().toString()};

        HttpRequestTack httpRequestTack = new HttpRequestTack(this,mHandler,arrQuery,arrQueryname,"POST","회원정보 확인중...");
        httpRequestTack.execute(checkMnoHttp);
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{
                    String result = (String)msg.obj;
                    try{
                        JSONArray arr = new JSONArray("["+result+"]");
                        JSONObject object= arr.getJSONObject(0);
                        int m = object.getInt("mno");

                        if(m<0){
                            Toast.makeText(JoinActivity.this,"회원정보가 존재하지 않습니다",Toast.LENGTH_SHORT).show();
                            mno.requestFocus();
                        }

                        if(m>0){
                            nameText.setText(object.getString("name"));
                            genderText.setText(object.getString("gender"));
                            ageText.setText(object.getString("age"));
                            tellText.setText(object.getString("tell"));
                            emailText.setText(object.getString("email"));
                            makeId = object.getString("id");
                            isleave = object.getBoolean("isleave");
                            Toast.makeText(JoinActivity.this,"회원정보를 찾았습니다",Toast.LENGTH_SHORT).show();
                            infoLabel.setVisibility(View.VISIBLE);
                            memberInfo.setVisibility(View.VISIBLE);
                        }

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    };

    public void prevClick(View view) {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    public void nextClick(View view) {

        if(isleave == true){
           Toast.makeText(this,"탈퇴회원 입니다. 6개월 후에 재가입이 가능합니다",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.login,R.anim.login_out);
           return;
       }
        if(!makeId.equals("null")){
            Toast.makeText(this,"이미 회원가입된 회원입니다 로그인을 이용하세요",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.login,R.anim.login_out);
            return;
        }

        Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.login,R.anim.login_out);
    }
}
