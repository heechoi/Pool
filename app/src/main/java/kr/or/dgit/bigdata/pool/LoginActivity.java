package kr.or.dgit.bigdata.pool;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import kr.or.dgit.bigdata.pool.dto.Member;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText id;
    private EditText pw;
    private Button loginBtn;

    private String http ="http://192.168.0.239:8080/pool/restLogin/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id=findViewById(R.id.id);
        pw = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        String loginHttp = http+"login";

        String[] arrQueryname ={"id","pw"};
        String[] arrQuery={id.getText().toString(),pw.getText().toString()};

        HttpRequestTack httpRequestTack = new HttpRequestTack(this,mHandler,arrQuery,arrQueryname,"POST","로그인..");
        httpRequestTack.execute(loginHttp);

    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{
                    String result = (String)msg.obj;
                  try{

                      JSONArray arr = new JSONArray("["+result+"]");

                          JSONObject object= arr.getJSONObject(0);

                          int mno = object.getInt("mno");
                      Log.d("da","===========0"+mno+"");
                          if(mno==-1){
                              Toast.makeText(LoginActivity.this,"회원이 아닙니다.", Toast.LENGTH_LONG).show();
                          }else if(mno==-2){
                              Toast.makeText(LoginActivity.this,"비밀번호가 일치하지 않습니다", Toast.LENGTH_LONG).show();
                          }else if(mno>0){

                              Member m = new Member();

                              m.setMno(object.getInt("mno"));
                              m.setId(object.getString("id"));
                              m.setTitle(object.getString("title"));

                              SharedPreferences login = getSharedPreferences("member",MODE_PRIVATE);
                              SharedPreferences.Editor editor = login.edit();

                              editor.putString("id",object.getString("id"));
                              editor.putInt("mno",object.getInt("mno"));
                              editor.putString("title",object.getString("title"));
                              editor.commit();
                          }

                  }catch(Exception e){
                      e.printStackTrace();
                  }



                   /* Log.d("da",member[0]);
                    Toast.makeText(LoginActivity.this,member[0], Toast.LENGTH_LONG).show();*/

                }
                break;
            }
        }
    };



}
