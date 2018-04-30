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

import org.w3c.dom.Text;

import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class SearchPwActivity extends AppCompatActivity {
    private AlertDialog.Builder mBuilder;
    private AlertDialog dialog;
    private String http = "http://192.168.0.239:8080/pool/restLogin/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_pw);
    }

    public void findPwEmail(View view) {
        mBuilder = new AlertDialog.Builder(SearchPwActivity.this);
        View v = getLayoutInflater().inflate(R.layout.search_email_pw, null);
        TextView title = (TextView) v.findViewById(R.id.title);
        title.setText("비밀번호 찾기");
        final EditText name = (EditText) v.findViewById(R.id.searchName);
        final EditText id = (EditText)v.findViewById(R.id.searchId);
        final EditText email1 = (EditText) v.findViewById(R.id.email1);
        final EditText email2 = (EditText) v.findViewById(R.id.email2);
        Button ok = (Button) v.findViewById(R.id.send);
        Button cancel = (Button) v.findViewById(R.id.cancel);
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
                if (name.getText().toString().equals("") || email1.getText().toString().equals("") || email2.getText().toString().equals("")||id.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(SearchPwActivity.this, "모두 입력해주세요", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

                String tempPw = http+"findPwByEmail";

                String[] arrQueryname = {"name", "email","id"};
                String email = email1.getText().toString() + "@" + email2.getText().toString();
                String[] arrQuery = {name.getText().toString(), email,id.getText().toString()};

                HttpRequestTack httpRequestTack = new HttpRequestTack(SearchPwActivity.this, mHandler, arrQuery, arrQueryname, "POST", "정보확인 중...", 1);
                httpRequestTack.execute(tempPw);
            }
        });

    }
    public void findPwPhone(View view) {
        mBuilder = new AlertDialog.Builder(SearchPwActivity.this);
        View v = getLayoutInflater().inflate(R.layout.search_tell_pw, null);
        TextView title = (TextView) v.findViewById(R.id.main_title);
        title.setText("비밀번호 찾기");
        final EditText name = (EditText) v.findViewById(R.id.name);
        final EditText id = (EditText)v.findViewById(R.id.searchId);
        final EditText tell1 = (EditText)v.findViewById(R.id.tell1);
        final EditText tell2 = (EditText)v.findViewById(R.id.tell2);
        final EditText tell3 = (EditText)v.findViewById(R.id.tell3);
        final EditText age = (EditText)v.findViewById(R.id.age);
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

                if(id.getText().toString().isEmpty()||name.getText().toString().isEmpty()||age.getText().toString().isEmpty()||tell1.getText().toString().isEmpty()||tell2.getText().toString().isEmpty()||tell3.getText().toString().isEmpty()){
                    Toast toast = Toast.makeText(getApplication(),"공백을 모두 입력해주세요",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    return;
                }

                String searchPw = http+"searchPw";

                String[] arrQueryname = {"id","name","tell","age"};
                String tell = tell1.getText().toString()+"-"+tell2.getText().toString()+"-"+tell3.getText().toString();
                String[] arrQuery ={id.getText().toString(),name.getText().toString(),tell,age.getText().toString()} ;

                HttpRequestTack httpRequestTack = new HttpRequestTack(SearchPwActivity.this, mHandler, arrQuery, arrQueryname, "POST", "정보확인 중...", 2);
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
                    if(result.equals("no id")){
                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(SearchPwActivity.this, R.style.SearchAlertDialog);
                        alert.setTitle("검색 결과");
                        alert.setMessage("일치하는 회원정보가 없습니다\n 입력하신 정보를 확인해주세요");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                            }
                        });
                        alert.show();
                    }else if(result.equals("success")){

                        android.app.AlertDialog.Builder malert = new android.app.AlertDialog.Builder(SearchPwActivity.this, R.style.SearchAlertDialog);
                        malert.setTitle("임시 비밀번호");
                        malert.setMessage("임시 비밀번호가 메일로 발송되었습니다\n임시비밀번호로 로그인을 이용해주세요");

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


                        break;


                }
                case 2:{

                    String result = (String) msg.obj;
                    if(result.equals("-1")){

                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(SearchPwActivity.this, R.style.SearchAlertDialog);
                        alert.setTitle("검색 결과");
                        alert.setMessage("일치하는 회원정보가 없습니다\n 입력하신 정보를 확인해주세요");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                            }
                        });
                        alert.show();

                    }else{

                        android.app.AlertDialog.Builder malert = new android.app.AlertDialog.Builder(SearchPwActivity.this, R.style.SearchAlertDialog);
                        malert.setTitle("문자발송");
                        malert.setMessage("문자가 발송되었습니다");

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

                        PendingIntent sentIntent = PendingIntent.getBroadcast(SearchPwActivity.this,0,new Intent("SMS_SENT_ACTION"),0);
                        SmsManager smsManager = SmsManager.getDefault();
                        sms = "";
                        for(int i=0;i<8;i++){
                            int num = (int)(Math.random()*10);
                            sms+=num+"";
                        }
                        smsManager.sendTextMessage(result,null,sms,sentIntent,null);

                        mBuilder = new AlertDialog.Builder(SearchPwActivity.this);
                        View v = getLayoutInflater().inflate(R.layout.input_temp_pw, null);
                        TextView title = (TextView) v.findViewById(R.id.title);
                        title.setText("비밀번호 찾기");
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
                                    mBuilder = new AlertDialog.Builder(SearchPwActivity.this);
                                    mBuilder.setCancelable(false);
                                    View v = getLayoutInflater().inflate(R.layout.new_pw, null);
                                    TextView title = (TextView) v.findViewById(R.id.main_title);
                                    title.setText("비밀번호 변경");
                                    final EditText newpw = (EditText) v.findViewById(R.id.newPw);
                                    final EditText newpw2 = (EditText)v.findViewById(R.id.newPw2);
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

                                        }
                                    });
                                    mBuilder.setView(v);
                                    dialog = mBuilder.create();
                                    dialog.show();
                                }else{
                                    android.app.AlertDialog.Builder malert = new android.app.AlertDialog.Builder(SearchPwActivity.this, R.style.SearchAlertDialog);
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
}
