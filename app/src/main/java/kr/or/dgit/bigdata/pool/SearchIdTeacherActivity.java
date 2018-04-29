package kr.or.dgit.bigdata.pool;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class SearchIdTeacherActivity extends AppCompatActivity implements View.OnClickListener {
    private Button searchTell;
    private Button searchEmail;
    private AlertDialog.Builder mDialog;
    private String http = "http://211.107.115.62:8080/pool/restFindTeacher/";
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_id_teacher);

        searchTell = findViewById(R.id.tell_search);
        searchTell.setOnClickListener(this);
        searchEmail = findViewById(R.id.email_search);
    }

    @Override
    public void finish() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void onClick(View view) {
        mDialog = new AlertDialog.Builder(SearchIdTeacherActivity.this);
        View v = getLayoutInflater().inflate(R.layout.search_tell_teacher, null);
        TextView title = (TextView) v.findViewById(R.id.main_title);
        title.setText("아이디 찾기");
        final EditText name = (EditText) v.findViewById(R.id.name);
        final EditText tell1 = (EditText) v.findViewById(R.id.tell1);
        final EditText tell2 = (EditText) v.findViewById(R.id.tell2);
        final EditText tell3 = (EditText) v.findViewById(R.id.tell3);
        final EditText tno = (EditText) v.findViewById(R.id.tno);

        Button send = (Button) v.findViewById(R.id.sendBtn);
        Button cancel = (Button) v.findViewById(R.id.cancelBtn);

        mDialog.setView(v);
        dialog = mDialog.create();
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.cancel();
                dialog.dismiss();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("") || tell1.getText().toString().equals("") || tell2.getText().toString().equals("") || tell3.getText().toString().equals("") || tno.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(SearchIdTeacherActivity.this, "모두 입력해주세요", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

                String findIdHttp = http + "findIdBytell";

                String[] arrQueryname = {"name", "tell", "tno"};
                String tell = tell1.getText().toString() + "-" + tell2.getText().toString() + "-" + tell3.getText().toString();
                String[] arrQuery = {name.getText().toString(), tell, tno.getText().toString()};

                HttpRequestTack httpRequestTack = new HttpRequestTack(SearchIdTeacherActivity.this, mHandler, arrQuery, arrQueryname, "POST", "아이디 검색중...", 1);
                httpRequestTack.execute(findIdHttp);
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
                    if(result.equals("fail")|| result.equals("null")){
                        AlertDialog.Builder alert = new AlertDialog.Builder(SearchIdTeacherActivity.this, R.style.SearchAlertDialog);
                        alert.setTitle("아이디 검색 결과");
                        alert.setMessage("일치하는 강사정보가 없습니다\n입력하신 정보를 확인해주세요");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                            }
                        });
                        alert.show();
                    }else{
                        AlertDialog.Builder alert = new AlertDialog.Builder(SearchIdTeacherActivity.this, R.style.SearchAlertDialog);
                        alert.setTitle("아이디 검색 결과");
                        String d1 = result.substring(0, result.length() - 4);
                        String d2 = result.substring(result.length()-4);
                        String str="";
                        for(int i=0;i<d2.length();i++){
                            str+="*";
                        }
                        alert.setMessage("강사 아이디 : " + d1+str);
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                    }
                }
                break;
                case 2:{
                    String result = (String) msg.obj;

                    if(result.equals("fail") || result.equals("null")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(SearchIdTeacherActivity.this, R.style.SearchAlertDialog);
                        alert.setTitle("아이디 검색 결과");
                        alert.setMessage("일치하는 강사정보가 없습니다\n입력하신 정보를 확인해주세요");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                            }
                        });
                        alert.show();
                    }else{
                        AlertDialog.Builder alert = new AlertDialog.Builder(SearchIdTeacherActivity.this, R.style.SearchAlertDialog);
                        alert.setTitle("아이디 검색 결과");
                        alert.setMessage("강사 아이디를 이메일로 발송했습니다.");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                    }
                }
                break;
            }




        }
    };

    public void findId(View view) {
        mDialog = new AlertDialog.Builder(SearchIdTeacherActivity.this);
        View v = getLayoutInflater().inflate(R.layout.search_email_teacher,null);
        TextView title = (TextView)v.findViewById(R.id.title);
        title.setText("아이디 찾기");
        final EditText tno = (EditText)v.findViewById(R.id.tno);
        final EditText name = (EditText)v.findViewById(R.id.name);
        final EditText email1 = (EditText)v.findViewById(R.id.email1);
        final EditText email2 = (EditText)v.findViewById(R.id.email2);
        Button send =(Button) v.findViewById(R.id.send);
        Button cancel = (Button)v.findViewById(R.id.cancel);
        mDialog.setView(v);
        dialog = mDialog.create();
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                dialog.cancel();
                dialog.dismiss();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tno.getText().toString().isEmpty()||name.getText().toString().equals("")||email1.getText().toString().equals("")||email2.getText().toString().equals("")){
                    Toast toast = Toast.makeText(SearchIdTeacherActivity.this,"모두 입력해주세요",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    return;
                }

                String findIdHttp = http+"findIdByEmail";

                String[] arrQueryname ={"name","email","tno"};
                String email = email1.getText().toString()+"@"+email2.getText().toString();
                String[] arrQuery={name.getText().toString(),email,tno.getText().toString()};

                HttpRequestTack httpRequestTack = new HttpRequestTack(SearchIdTeacherActivity.this,mHandler,arrQuery,arrQueryname,"POST","아이디 검색중...",2);
                httpRequestTack.execute(findIdHttp);
            }
        });

    }


}
