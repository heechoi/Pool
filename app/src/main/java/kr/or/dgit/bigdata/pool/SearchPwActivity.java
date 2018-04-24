package kr.or.dgit.bigdata.pool;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

            }
        }
    };


}
