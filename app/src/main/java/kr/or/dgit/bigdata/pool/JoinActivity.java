package kr.or.dgit.bigdata.pool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
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
    private String http ="http://rkd0519.cafe24.com/pool/restJoin/";
    private TextView nameText;
    private TextView genderText;
    private TextView tellText;
    private TextView emailText;
    private TextView ageText;
    private TextView infoLabel;
    private LinearLayout memberInfo;
    private String makeId;
    private boolean isleave;
    private int sendDate;

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
                            sendDate = object.getInt("mno");
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    public void nextClick(View view) {

        if(isleave == true){
          // Toast.makeText(this,"탈퇴회원 입니다. 6개월 후에 재가입이 가능합니다",Toast.LENGTH_SHORT).show();
            alertDialogShow("회원가입 불가","탈퇴회원입니다 6개월 후에 재가입이 가능합니다",MainActivity.class);
           return;
       }
        if(!makeId.equals("null")){

         //   Toast.makeText(this,"이미 회원가입된 회원입니다 로그인을 이용하세요",Toast.LENGTH_SHORT).show();
            alertDialogShow("회원가입 불가","이미 회원가입된 회원입니다 로그인을 이용하세요",LoginActivity.class);
            return;
        }

        Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
        intent.putExtra("mno",sendDate+"");
        startActivity(intent);
        overridePendingTransition(R.anim.login,R.anim.login_out);
    }

    private void alertDialogShow(String title, String messgae, final Class<?> activity){
        AlertDialog.Builder alert = new AlertDialog.Builder(JoinActivity.this,R.style.AlertDialog);
        alert.setTitle(title);
        alert.setMessage(messgae).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(),activity);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.login,R.anim.login_out);
            }
        });
        alert.create();
        alert.show();
    }
}
