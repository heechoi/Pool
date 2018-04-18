package kr.or.dgit.bigdata.pool;

import android.app.ProgressDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

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

        HttpRequestTack httpRequestTack = new HttpRequestTack(this,mHandler,arrQuery,arrQueryname,"POST","fddfsds..");
        httpRequestTack.execute(loginHttp);

    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{
                    String result = (String)msg.obj;
                    Toast.makeText(LoginActivity.this,result, Toast.LENGTH_LONG).show();
                    if(result.equals("no member")){
                        Toast.makeText(LoginActivity.this,"회원이 아닙니다. 아이디를 확인해 주세요",Toast.LENGTH_SHORT).show();
                        id.setText("");
                        pw.setText("");
                        id.requestFocus();
                    }
                    if(result.equals("wrong pw")){
                        Toast.makeText(LoginActivity.this,"비밀번호를 확인해주세요",Toast.LENGTH_SHORT).show();
                        pw.setText("");
                        pw.requestFocus();
                    }

                    if(result.equals("member")){

                    }
                }
                break;
            }
        }
    };

}
