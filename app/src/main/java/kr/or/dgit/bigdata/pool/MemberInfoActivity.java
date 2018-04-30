package kr.or.dgit.bigdata.pool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private Button btnChangePw;
    private SharedPreferences member;
    private EditText isleavePw;
    private EditText nowPw;
    private EditText newPw;
    private EditText newPw2;
    private Button checkEmail;
    private EditText chageEmail;
    private TextView nowEmail;
    private String http ="http://192.168.0.239:8080/pool/restInfoUpdate/";
    private ImageView showTell;
    private TextView tell;
    private EditText tell1;
    private EditText tell2;
    private EditText tell3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);
        member =  getSharedPreferences("member",MODE_PRIVATE);
        int no = member.getInt("mno",0);

        mno = findViewById(R.id.mno);
        name = findViewById(R.id.name);
        basic = findViewById(R.id.basic_info);

        nowPw = findViewById(R.id.nowPw);
        newPw = findViewById(R.id.newPw);
        newPw2 = findViewById(R.id.newPw2);

        showPwBtn = findViewById(R.id.show_pw);
        showPwBtn.setOnClickListener(this);

        btnChangePw = findViewById(R.id.pwChageBtn);

        showEmailBtn = findViewById(R.id.show_email);
        showEmailBtn.setOnClickListener(this);
        chageEmail = findViewById(R.id.changeEmail);
        checkEmail = findViewById(R.id.checkEmail);

        isleaveBtn =findViewById(R.id.show_isleave);
        isleaveBtn.setOnClickListener(this);

        btnisleave=findViewById(R.id.btnisleave);
        isleavePw = findViewById(R.id.isleqve_pw);
        nowEmail = findViewById(R.id.nowEmail);

        mno.setText(no+"");

        showTell= findViewById(R.id.show_tell);
        showTell.setOnClickListener(this);

        tell = findViewById(R.id.tell);
        tell1 = findViewById(R.id.tell1);
        tell2 = findViewById(R.id.tell2);
        tell3 = findViewById(R.id.tell3);

        String findHttp = http+"findMember";

        String[] arrQueryname ={"mno"};
        String[] arrQuery={String.valueOf(no)};

        HttpRequestTack httpRequestTack = new HttpRequestTack(this,mHandler,arrQuery,arrQueryname,"POST","회원정보 가져오는 중..",4);
        httpRequestTack.execute(findHttp);

    }

    @Override
    public void finish() {
        super.finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }


    @Override
    public void onClick(View view) {


        if(view.getId()==R.id.show_isleave){

            LinearLayout layout = findViewById(R.id.isleave_layout);
            EditText editText = layout.findViewById(R.id.isleqve_pw);
            if(layout.getVisibility()==View.VISIBLE){
                isleaveBtn.setImageResource(R.drawable.arrow_down);
                layout.setVisibility(View.GONE);
                editText.setFocusable(false);
            }else if(layout.getVisibility()==View.GONE){
                isleaveBtn.setImageResource(R.drawable.arrow_up);
                layout.setVisibility(View.VISIBLE);
                editText.requestFocus();
            }

        }

        if(view.getId()==R.id.show_pw){
            LinearLayout layout = findViewById(R.id.change_layout);
            EditText editText = layout.findViewById(R.id.nowPw);
            if(layout.getVisibility() ==View.VISIBLE){
                showPwBtn.setImageResource(R.drawable.arrow_down);
                layout.setVisibility(View.GONE);
                editText.setFocusable(false);
            }else if(layout.getVisibility()==View.GONE){
                showPwBtn.setImageResource(R.drawable.arrow_up);
                layout.setVisibility(View.VISIBLE);
                editText.requestFocus();

            }

        }

        if(view.getId()==R.id.show_email){
            LinearLayout layout = findViewById(R.id.change_email_layout);
            EditText editText = layout.findViewById(R.id.changeEmail);
            if(layout.getVisibility()==View.VISIBLE){
                showEmailBtn.setImageResource(R.drawable.arrow_down);
                layout.setVisibility(View.GONE);
                editText.setFocusable(false);
            }else if(layout.getVisibility()==View.GONE){
                showEmailBtn.setImageResource(R.drawable.arrow_up);
                layout.setVisibility(View.VISIBLE);
                editText.requestFocus();
            }

        }

        if(view.getId()==R.id.show_tell){
            LinearLayout layout = findViewById(R.id.change_tell_layout);
            EditText editText = layout.findViewById(R.id.tell1);
            if(layout.getVisibility()==View.VISIBLE){
                showTell.setImageResource(R.drawable.arrow_down);
                layout.setVisibility(View.GONE);
                editText.setFocusable(false);
            }else if(layout.getVisibility()==View.GONE){
                showTell.setImageResource(R.drawable.arrow_up);
                layout.setVisibility(View.VISIBLE);
                editText.requestFocus();
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
                    if(result.equals("fail")){
                        Toast toast = Toast.makeText(MemberInfoActivity.this,"비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        isleavePw.setText("");
                        isleavePw.requestFocus();
                    }else if(result.equals("bye")){
                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(MemberInfoActivity.this, R.style.SearchAlertDialog);
                        alert.setTitle("탈퇴신청");
                        alert.setMessage("탈퇴신청 되었습니다\n홈페이지,모바일앱 이용이 제한됩니다\n");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                                SharedPreferences m = getSharedPreferences("member",MODE_PRIVATE);
                                SharedPreferences.Editor editor = m.edit();
                                editor.clear();
                                editor.commit();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                overridePendingTransition(R.anim.login,R.anim.login_out);
                            }
                        });
                        alert.show();
                    }
                }
                case 2:{
                    String result = (String) msg.obj;
                    if(result.equals("fail")){
                        Toast toast = Toast.makeText(getApplicationContext(),"기존 비밀번호가 일치하지 않습니다\n비밀번호를 확인해 주세요",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        nowPw.setText("");
                        nowPw.requestFocus();
                        newPw.setText("");
                        newPw2.setText("");
                        toast.show();
                    }else if(result.equals("success")){
                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(MemberInfoActivity.this, R.style.SearchAlertDialog);
                        alert.setTitle("비밀번호 변경");
                        alert.setMessage("비밀번호가 변경 되었습니다");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                                finish();
                                startActivity(getIntent());
                            }
                        });
                        alert.show();
                    }
                }
                case 3:{
                    String result = (String) msg.obj;
                    if(result.equals("fail")){
                        Toast toast = Toast.makeText(getApplicationContext(),"중복 이메일입니다 사용할 수 없습니다",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }else if(result.equals("success")){
                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(MemberInfoActivity.this, R.style.SearchAlertDialog);
                        alert.setTitle("이메일 변경");
                        alert.setMessage("이메일이 변경 되었습니다");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                                SharedPreferences.Editor  editor = member.edit();
                               //기존꺼 삭제 후
                                editor.remove("email");
                                editor.putString("email",chageEmail.getText().toString());
                                editor.commit();
                                finish();
                                startActivity(getIntent());
                            }
                        });
                        alert.show();
                    }
                }
                case 4:{
                    String result = (String) msg.obj;
                    try{
                        JSONObject object = new JSONObject(result);

                        String ttell = object.getString("tell");
                        String t1 = ttell.substring(0,ttell.indexOf("-")+1);
                        String t2 = ttell.substring(ttell.indexOf("-")+1,ttell.indexOf("-")+2)+"***-";
                        String t3 = ttell.substring(ttell.lastIndexOf("-")+1,ttell.lastIndexOf("-")+2)+"***";

                        tell.setText(t1+t2+t3);
                        name.setText(object.getString("name"));
                        basic.setText(object.getString("gender"));

                        String e = object.getString("email");
                        String email1 = e.substring(2,e.indexOf("@"));
                        String email2 = e.substring(0,2);
                        String email3 = e.substring(e.indexOf("@"));
                        String str="";
                        for(int i=0;i<email1.length();i++){
                            str +="*";
                        }

                        nowEmail.setText(email2+str+email3);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                case 5:{
                    String result = (String) msg.obj;
                    if(result.equals("success")){
                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(MemberInfoActivity.this, R.style.SearchAlertDialog);
                        alert.setTitle("연락처 변경");
                        alert.setMessage("연락처가 변경 되었습니다");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                                SharedPreferences.Editor  editor = member.edit();
                                //기존꺼 삭제 후
                                editor.remove("tell");
                                String ttell = tell1.getText().toString()+"-"+tell2.getText().toString()+"-"+tell3.getText().toString();
                                editor.putString("tell",ttell);
                                editor.commit();
                                finish();
                                startActivity(getIntent());
                            }
                        });
                        alert.show();
                    }
                }
                break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this,R.style.AlertDialog);
        alert.setTitle("개인정보 수정");
        alert.setMessage("개인정보 수정을 종료하시겠습니까?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
                finish();
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                dialogInterface.dismiss();
            }
        });
        alert.create();
        alert.show();

    }

    public void clickChangePw(View view) {
        if(nowPw.getText().toString().isEmpty()||newPw.getText().toString().isEmpty()||newPw2.getText().toString().isEmpty()){
            Toast toast = Toast.makeText(this,"비밀번호를 모두 입력해주세요",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }

        Pattern patternPw = Pattern.compile("(^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[~!@#$%^&*])[A-Za-z0-9~!@#$%^&*]{8,20}$)");
        Matcher matcherPw = patternPw.matcher(newPw2.getText().toString());

        if(!matcherPw.find()){
            AlertDialog.Builder alert = new AlertDialog.Builder(this,R.style.AlertDialog);
            alert.setTitle("비밀번호 사용 불가");
            alert.setMessage("비밀번호 정규식이 일치하지 않습니다\n비밀번호는8~20자,최소한 한번 이상의 영문,숫자,특수문자(~!@#$%^&*)만 허용됩니다").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    newPw.setText("");
                    newPw2.setText("");
                    newPw.requestFocus();
                    dialog.cancel();
                    dialog.dismiss();
                }
            });
            alert.create();
            alert.show();
            return;
        }

        if(!newPw.getText().toString().equals(newPw2.getText().toString())){
            Toast toast = Toast.makeText(this,"비밀번호가 일치하지 않습니다\n비밀번호를 확인해주세요",Toast.LENGTH_SHORT);
            newPw.requestFocus();
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        String updatePw = http+"updatePw";
        String[] arrQuery = {member.getString("id",""),nowPw.getText().toString(),newPw.getText().toString()};
        String[] arrQueryname = {"id","pw","newPw"};
        HttpRequestTack httpRequestTack = new HttpRequestTack(this,mHandler,arrQuery,arrQueryname,"POST","비밀번호 변경중..",2);
        httpRequestTack.execute(updatePw);
    }

    public void clickCheckEmail(View view) {
        if(chageEmail.getText().toString().isEmpty()){
            Toast toast = Toast.makeText(this,"이메일을 입력해 주세요",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }

        String updateEmail = http+"changeEmail";
        String[] arrQueryname={"id","email"};
        String[] arrQuery = {member.getString("id",""),chageEmail.getText().toString()};
        HttpRequestTack httpRequestTack = new HttpRequestTack(this,mHandler,arrQuery,arrQueryname,"POST","이메일 변경중..",3);
        httpRequestTack.execute(updateEmail);


    }

    public void clickVoice(View view) {
    }

    public void clickCheckTell(View view) {
        if(tell1.getText().toString().isEmpty()||tell2.getText().toString().isEmpty()||tell3.getText().toString().isEmpty()){
            Toast toast = Toast.makeText(getApplicationContext(),"연락처를 모두 입력해주세요",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }

        Pattern patternTell = Pattern.compile("(^[0-9]{3,4}$)");
        Matcher matcherTell1 = patternTell.matcher(tell1.getText().toString());
        Matcher matcherTell2 = patternTell.matcher(tell2.getText().toString());
        Matcher matcherTell3 = patternTell.matcher(tell3.getText().toString());

        if(!matcherTell1.find()||!matcherTell2.find()||!matcherTell3.find()){
            Toast toast = Toast.makeText(this,"연락처가 형식에 맞지 않습니다\n연락처를 확인해주세요",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            tell1.requestFocus();
            return;
        }

        String ttell = tell1.getText().toString()+"-"+tell2.getText().toString()+"-"+tell3.getText().toString();
        String tellHttp = http+"updateTell";
        String[] arrQuery ={mno.getText().toString(),ttell};
        String[] arrQueryname ={"mno","tell"};
        HttpRequestTack httpRequestTack = new HttpRequestTack(this,mHandler,arrQuery,arrQueryname,"POST","연락처 변경 중..",5);
        httpRequestTack.execute(tellHttp);
    }
}
