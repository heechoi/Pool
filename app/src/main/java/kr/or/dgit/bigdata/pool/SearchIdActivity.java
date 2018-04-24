package kr.or.dgit.bigdata.pool;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class SearchIdActivity extends AppCompatActivity implements View.OnClickListener {
    private Button searchTell;
    private Button searchEmail;
    private AlertDialog.Builder mDialog;
    private String http = "http://192.168.0.239:8080/pool/restLogin/";
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_id);

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
        mDialog = new AlertDialog.Builder(SearchIdActivity.this);
        View v = getLayoutInflater().inflate(R.layout.search_tell,null);
        TextView title = (TextView)v.findViewById(R.id.main_title);
        title.setText("아이디 찾기");
        final EditText name = (EditText)v.findViewById(R.id.name);
        final EditText tell1 = (EditText)v.findViewById(R.id.tell1);
        final EditText tell2 = (EditText)v.findViewById(R.id.tell2);
        final EditText tell3 = (EditText)v.findViewById(R.id.tell3);
        final EditText age = (EditText)v.findViewById(R.id.age);

        Button send =(Button) v.findViewById(R.id.sendBtn);
        Button cancel = (Button)v.findViewById(R.id.cancelBtn);
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
                if(name.getText().toString().equals("")||tell1.getText().toString().equals("")||tell2.getText().toString().equals("")||tell3.getText().toString().equals("")||age.getText().toString().equals("")){
                    Toast toast = Toast.makeText(SearchIdActivity.this,"모두 입력해주세요",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    return;
                }

                String findIdHttp = http+"findIdBytell";

                String[] arrQueryname ={"name","tell","age"};
                String tell = tell1.getText().toString()+"-"+tell2.getText().toString()+"-"+tell3.getText().toString();
                String[] arrQuery={name.getText().toString(),tell,age.getText().toString()};

                HttpRequestTack httpRequestTack = new HttpRequestTack(SearchIdActivity.this,mHandler,arrQuery,arrQueryname,"POST","아이디 검색중...",1);
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

                    if (result.equals("no Id") || result.equals("null")) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(SearchIdActivity.this, R.style.SearchAlertDialog);
                        alert.setTitle("아이디 검색 결과");
                        alert.setMessage("일치하는 회원정보가 없습니다\n회원가입을 이용해 주세요");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                            }
                        });
                        alert.show();
                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(SearchIdActivity.this, R.style.SearchAlertDialog);
                        alert.setTitle("아이디 검색 결과");
                        String id = result.substring(0, result.length() - 4);
                        id += "***";
                        alert.setMessage("회원 아이디 : " + id);
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
                case 2:
                    String result = (String) msg.obj;
                    if (result.equals("no Id") || result.equals("null")) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(SearchIdActivity.this, R.style.SearchAlertDialog);
                        alert.setTitle("아이디 검색 결과");
                        alert.setMessage("일치하는 회원정보가 없습니다\n회원가입을 이용해 주세요");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                            }
                        });
                        alert.show();
                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(SearchIdActivity.this, R.style.SearchAlertDialog);
                        alert.setTitle("아이디 검색 결과");
                        String id = result.substring(0, result.length() - 4);
                        id += "***";
                        alert.setMessage("회원 아이디 : " + id);
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

                    break;
            }

        }
    };

    public void findId(View view) {
        mDialog = new AlertDialog.Builder(SearchIdActivity.this);
        View v = getLayoutInflater().inflate(R.layout.search_email,null);
        TextView title = (TextView)v.findViewById(R.id.title);
        title.setText("아이디 찾기");
        final EditText name = (EditText)v.findViewById(R.id.searchName);
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

                if(name.getText().toString().equals("")||email1.getText().toString().equals("")||email2.getText().toString().equals("")){
                    Toast toast = Toast.makeText(SearchIdActivity.this,"모두 입력해주세요",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    return;
                }

                String findIdHttp = http+"findIdByEmail";

                String[] arrQueryname ={"name","email"};
                String email = email1.getText().toString()+"@"+email2.getText().toString();
                String[] arrQuery={name.getText().toString(),email};

                HttpRequestTack httpRequestTack = new HttpRequestTack(SearchIdActivity.this,mHandler,arrQuery,arrQueryname,"POST","아이디 검색중...",2);
                httpRequestTack.execute(findIdHttp);
            }
        });

    }
}
