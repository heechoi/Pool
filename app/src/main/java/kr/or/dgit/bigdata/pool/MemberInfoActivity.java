package kr.or.dgit.bigdata.pool;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import kr.or.dgit.bigdata.pool.dto.Member;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class MemberInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView name;
    private TextView basic;
    private TextView mno;
    private ImageView isleaveBtn;
    private ImageView showPwBtn;
    private ImageView showEmailBtn;
    private Button btnisleave;
    private SharedPreferences member;
    private EditText isleavePw;

    private String http ="http://192.168.0.239:8080/pool/restInfoUpdate/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);
        member =  getSharedPreferences("member",MODE_PRIVATE);
        int no = member.getInt("mno",0);
        String mName = member.getString("name","");

        mno = findViewById(R.id.mno);
        name = findViewById(R.id.name);
        basic = findViewById(R.id.basic_info);

        showPwBtn = findViewById(R.id.show_pw);
        showPwBtn.setOnClickListener(this);

        showEmailBtn = findViewById(R.id.show_email);
        showEmailBtn.setOnClickListener(this);

        isleaveBtn =findViewById(R.id.show_isleave);
        isleaveBtn.setOnClickListener(this);

        btnisleave=findViewById(R.id.btnisleave);
        isleavePw = findViewById(R.id.isleqve_pw);

        mno.setText(no+"");
        name.setText(mName);
        basic.setText("성별 : "+member.getString("gender","")+"\n\n\n연락처 : \n");

    }

    @Override
    public void finish() {
        super.finish();
    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.show_isleave){
            LinearLayout layout = findViewById(R.id.isleave_layout);
            if(layout.getVisibility()==View.VISIBLE){
                isleaveBtn.setImageResource(R.drawable.arrow_down);
                layout.setVisibility(View.GONE);
            }else if(layout.getVisibility()==View.GONE){
                isleaveBtn.setImageResource(R.drawable.arrow_up);
                layout.setVisibility(View.VISIBLE);
            }
        }

        if(view.getId()==R.id.show_pw){
            LinearLayout layout = findViewById(R.id.change_layout);
            if(layout.getVisibility() ==View.VISIBLE){
                showPwBtn.setImageResource(R.drawable.arrow_down);
                layout.setVisibility(View.GONE);
            }else if(layout.getVisibility()==View.GONE){
                showPwBtn.setImageResource(R.drawable.arrow_up);
                layout.setVisibility(View.VISIBLE);
            }
        }

        if(view.getId()==R.id.show_email){
            LinearLayout layout = findViewById(R.id.change_email_layout);
            if(layout.getVisibility()==View.VISIBLE){
                showEmailBtn.setImageResource(R.drawable.arrow_down);
                layout.setVisibility(View.GONE);
            }else if(layout.getVisibility()==View.GONE){
                showEmailBtn.setImageResource(R.drawable.arrow_up);
                layout.setVisibility(View.VISIBLE);
            }
        }
    }

    public void clickIsleave(View view) {

        if(isleavePw.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;

        }
        String isleave = http+"isleave";
        Log.d("dahee","======="+isleave);

        String[] arrQueryname ={"id","pw"};
        String[] arrQuery={member.getString("id",""),isleavePw.getText().toString()};

        HttpRequestTack httpRequestTack = new HttpRequestTack(this,mHandler,arrQuery,arrQueryname,"POST","탈퇴처리중..",1);
        httpRequestTack.execute(isleave);
    }



    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1: {
                    String result = (String) msg.obj;
                    Toast.makeText(getApplicationContext(),"결과 : "+result,Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    };
}
