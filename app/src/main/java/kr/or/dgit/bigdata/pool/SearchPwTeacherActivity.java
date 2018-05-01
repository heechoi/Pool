package kr.or.dgit.bigdata.pool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class SearchPwTeacherActivity extends AppCompatActivity {
    private String http = "http://rkd0519.cafe24.com/pool/restLogin/";
    private AlertDialog.Builder mBuilder;
    private AlertDialog dialog;
    private String sms;
    private String tid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_pw_teacher);
    }

    public void findPwPhone(View view) {

        mBuilder = new AlertDialog.Builder(SearchPwTeacherActivity.this);
        View v = getLayoutInflater().inflate(R.layout.search_tell_pw_teacher, null);
        TextView title = (TextView) v.findViewById(R.id.main_title);
        title.setText("비밀번호 찾기");
        final EditText name = (EditText) v.findViewById(R.id.name);
        final EditText id = (EditText)v.findViewById(R.id.searchId);
        final EditText tell1 = (EditText)v.findViewById(R.id.tell1);
        final EditText tell2 = (EditText)v.findViewById(R.id.tell2);
        final EditText tell3 = (EditText)v.findViewById(R.id.tell3);
        final EditText tno = (EditText)v.findViewById(R.id.tno);
        Button ok = (Button) v.findViewById(R.id.sendBtn);
        Button cancel = (Button) v.findViewById(R.id.cancelBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                dialog.dismiss();
            }
        });
        mBuilder.setView(v);
        dialog = mBuilder.create();
        dialog.show();


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(id.getText().toString().isEmpty()||name.getText().toString().isEmpty()||tno.getText().toString().isEmpty()||tell1.getText().toString().isEmpty()||tell2.getText().toString().isEmpty()||tell3.getText().toString().isEmpty()){
                    Toast toast = Toast.makeText(getApplication(),"공백을 모두 입력해주세요",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    return;
                }

                String searchPw = http+"searchTinfo";

                String[] arrQueryname = {"id","name","tell","tno"};
                String tell = tell1.getText().toString()+"-"+tell2.getText().toString()+"-"+tell3.getText().toString();
                tid = id.getText().toString();
                String[] arrQuery ={id.getText().toString(),name.getText().toString(),tell,tno.getText().toString()} ;

                HttpRequestTack httpRequestTack = new HttpRequestTack(SearchPwTeacherActivity.this, mHandler, arrQuery, arrQueryname, "POST", "정보확인 중...", 1);
                httpRequestTack.execute(searchPw);

            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    String result = (String) msg.obj;
                    if(result.equals("-1")){
                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(SearchPwTeacherActivity.this, R.style.SearchAlertDialog);
                        alert.setTitle("검색 결과");
                        alert.setMessage("일치하는 강사정보가 없습니다\n 입력하신 정보를 확인해주세요");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                            }
                        });
                        alert.show();
                    }else{


                        android.app.AlertDialog.Builder malert = new android.app.AlertDialog.Builder(SearchPwTeacherActivity.this, R.style.SearchAlertDialog);
                        malert.setTitle("문자발송");
                        malert.setMessage("문자가 발송되었습니다");

                        malert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                                dialog.cancel();
                                dialog.dismiss();

                                mBuilder = new AlertDialog.Builder(SearchPwTeacherActivity.this);
                                View v = getLayoutInflater().inflate(R.layout.input_temp_pw, null);
                                TextView title = (TextView) v.findViewById(R.id.title);
                                title.setText("임시번호를 입력하세요");
                                final EditText input = (EditText) v.findViewById(R.id.input);
                                Button ok = (Button) v.findViewById(R.id.sendBtn);
                                Button cancel = (Button) v.findViewById(R.id.cancelBtn);
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.cancel();
                                        dialog.dismiss();
                                    }
                                });
                                mBuilder.setView(v);
                                dialog = mBuilder.create();
                                dialog.show();
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(input.getText().toString().equals(sms)){
                                            mBuilder = new AlertDialog.Builder(SearchPwTeacherActivity.this);
                                            mBuilder.setCancelable(false);
                                            View v = getLayoutInflater().inflate(R.layout.new_pw, null);
                                            final EditText newpw = (EditText) v.findViewById(R.id.pw1);
                                            final EditText newpw2 = (EditText)v.findViewById(R.id.pw2);
                                            Button ok = (Button) v.findViewById(R.id.sendBtn);
                                            Button cancel = (Button) v.findViewById(R.id.cancelBtn);
                                            cancel.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    dialog.cancel();
                                                    dialog.dismiss();
                                                }
                                            });
                                            ok.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Pattern patternPw = Pattern.compile("(^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[~!@#$%^&*])[A-Za-z0-9~!@#$%^&*]{8,20}$)");
                                                    Matcher matcherPw = patternPw.matcher(newpw.getText().toString());

                                                    if(newpw.getText().toString().isEmpty()||newpw2.getText().toString().isEmpty()){
                                                        Toast toast = Toast.makeText(getApplication(),"공백이 존재합니다",Toast.LENGTH_SHORT);
                                                        toast.setGravity(Gravity.CENTER,0,0);
                                                        toast.show();
                                                        return;
                                                    }
                                                    if(!matcherPw.find()){
                                                        AlertDialog.Builder alert = new AlertDialog.Builder(SearchPwTeacherActivity.this,R.style.AlertDialog);
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

                                                    if(!newpw.getText().toString().equals(newpw2.getText().toString())){
                                                        Toast toast = Toast.makeText(getApplication(),"두 비밀번호가 일치하지 않습니다\n비밀번호를 확인해주세요",Toast.LENGTH_SHORT);
                                                        toast.setGravity(Gravity.CENTER,0,0);
                                                        toast.show();
                                                        newpw2.requestFocus();
                                                        return;
                                                    }


                                                    String updatePw = http+"updateTPw";
                                                    String[] arrQueryname = {"id","pw"};
                                                    String[] arrQuery = {tid,newpw.getText().toString()};

                                                    HttpRequestTack httpRequestTack = new HttpRequestTack(SearchPwTeacherActivity.this, mHandler, arrQuery, arrQueryname, "POST", "비밀번호 변경중...", 2);
                                                    httpRequestTack.execute(updatePw);

                                                    dialog.dismiss();
                                                    dialog.cancel();
                                                }
                                            });
                                            mBuilder.setView(v);
                                            dialog = mBuilder.create();
                                            dialog.show();
                                        }else{
                                            android.app.AlertDialog.Builder malert = new android.app.AlertDialog.Builder(SearchPwTeacherActivity.this, R.style.SearchAlertDialog);
                                            malert.setTitle("불일치");
                                            malert.setMessage("입력하신 내용이 일치하지 않습니다\n문자를 다시 확인해주세요");

                                            malert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.cancel();
                                                    dialogInterface.dismiss();
                                                    dialog.cancel();
                                                    dialog.dismiss();

                                                }
                                            });
                                            malert.show();
                                        }
                                    }
                                });




                            }
                        });
                        malert.show();




                        PendingIntent sentIntent = PendingIntent.getBroadcast(SearchPwTeacherActivity.this,0,new Intent("SMS_SENT_ACTION"),0);
                        SmsManager smsManager = SmsManager.getDefault();
                        sms = "";
                        String content = "[대구아이티수영장]\n비밀번호 찾기 입니다\n아래의 임시번호를 입력해주세요\n(본인이 아닐시 신고바랍니다)\n";
                        for(int i=0;i<8;i++){
                            int num = (int)(Math.random()*10);
                            sms+=num+"";
                        }
                        smsManager.sendTextMessage(result,null,content+sms,sentIntent,null);


                    }
                    break;


                }
                case 2:{
                    String result = (String) msg.obj;
                    if(result.equals("success")){
                        android.app.AlertDialog.Builder malert = new android.app.AlertDialog.Builder(SearchPwTeacherActivity.this, R.style.SearchAlertDialog);
                        malert.setTitle("비밀번호 변경");
                        malert.setMessage("비밀번호가 변경되었습니다.\n로그인을 이용하세요");

                        malert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                                dialog.cancel();
                                dialog.dismiss();
                                finish();

                            }
                        });
                        malert.show();
                    }
                    break;
                }

            }
        }

    };

    public void sendSMS(String smsNumber, String smsText){

        PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT_ACTION"), 0);

        PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED_ACTION"), 0);



        registerReceiver(new BroadcastReceiver() {

            @Override

            public void onReceive(Context context, Intent intent) {

                switch(getResultCode()){

                    case Activity.RESULT_OK:

                        // 전송 성공

                        Toast.makeText(context, "전송 완료", Toast.LENGTH_SHORT).show();

                        break;

                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:

                        // 전송 실패

                        Toast.makeText(context, "전송 실패", Toast.LENGTH_SHORT).show();

                        break;

                    case SmsManager.RESULT_ERROR_NO_SERVICE:

                        // 서비스 지역 아님

                        Toast.makeText(context, "서비스 지역이 아닙니다", Toast.LENGTH_SHORT).show();

                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:

                        // 무선 꺼짐

                        Toast.makeText(context, "무선(Radio)가 꺼져있습니다", Toast.LENGTH_SHORT).show();

                        break;

                    case SmsManager.RESULT_ERROR_NULL_PDU:

                        // PDU 실패

                        Toast.makeText(context, "PDU Null", Toast.LENGTH_SHORT).show();

                        break;

                }

            }

        }, new IntentFilter("SMS_SENT_ACTION"));



        registerReceiver(new BroadcastReceiver() {

            @Override

            public void onReceive(Context context, Intent intent) {

                switch (getResultCode()){

                    case Activity.RESULT_OK:

                        // 도착 완료

                        Toast.makeText(context, "SMS 도착 완료", Toast.LENGTH_SHORT).show();

                        break;

                    case Activity.RESULT_CANCELED:

                        // 도착 안됨

                        Toast.makeText(context, "SMS 도착 실패", Toast.LENGTH_SHORT).show();

                        break;

                }

            }

        }, new IntentFilter("SMS_DELIVERED_ACTION"));



        SmsManager mSmsManager = SmsManager.getDefault();

        mSmsManager.sendTextMessage(smsNumber, null, smsText, sentIntent, deliveredIntent);

    }

    @Override
    public void finish() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

}
