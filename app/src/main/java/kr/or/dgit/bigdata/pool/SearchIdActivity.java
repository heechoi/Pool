package kr.or.dgit.bigdata.pool;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.zip.Inflater;

import kr.or.dgit.bigdata.pool.dto.Member;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class SearchIdActivity extends AppCompatActivity implements View.OnClickListener {
    private Button searchTell;
    private Button searchEmail;
    private Dialog mDialog;
    private String http = "http://220.89.0.222:8080/pool/restLogin/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_id);
        getSupportActionBar().setElevation(0);
        setTitle("아이디 찾기");
        searchTell = findViewById(R.id.tell_search);
        searchTell.setOnClickListener(this);
        searchEmail=findViewById(R.id.email_search);
    }

    @Override
    public void finish() {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    @Override
    public void onClick(View view) {

        final EditText tell1;
        final EditText tell2;
        final EditText tell3;
        final LinearLayout tell_layout;
        final LinearLayout email_layout;
        final EditText name;


        if (view.getId() == R.id.tell_search) {
            mDialog = new Dialog(this, R.style.SearchAlertDialog);
            mDialog.setContentView(R.layout.search_id);
            tell1 = (EditText) mDialog.findViewById(R.id.tell1);
            tell2 = (EditText)mDialog.findViewById(R.id.tell2);
            tell3 =(EditText)mDialog.findViewById(R.id.tell3);
        ;
            name = (EditText) mDialog.findViewById(R.id.searchName);
            tell_layout = (LinearLayout)mDialog.findViewById(R.id.layout_tell);
            tell_layout.setVisibility(View.VISIBLE);

            email_layout = (LinearLayout)mDialog.findViewById(R.id.email);
            email_layout.setVisibility(View.GONE);

            Button ok = (Button) mDialog.findViewById(R.id.send);
            Button cancel = (Button) mDialog.findViewById(R.id.cancel);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.cancel();
                    mDialog.dismiss();
                }
            });

            ok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (name.getText().toString().equals("") || tell1.getText().toString().equals("")||tell2.getText().toString().equals("")||tell3.getText().toString().equals("")) {
                        Toast toast = Toast.makeText(getApplication(), "공백을 모두 입력해주세요", Toast.LENGTH_SHORT);
                        int offsetX=0;
                        int offsetY=0;
                        toast.setGravity(Gravity.CENTER,offsetX,offsetY);
                        toast.show();
                        return;
                    }

                    String findId = http + "findIdBytell";

                    String[] arrQueryname = {"name", "tell"};
                    String tell = tell1.getText().toString()+"-"+tell2.getText().toString()+"-"+tell3.getText().toString();
                    String[] arrQuery = {name.getText().toString(), tell};

                    HttpRequestTack httpRequestTack = new HttpRequestTack(SearchIdActivity.this, mHandler, arrQuery, arrQueryname, "POST", "회원정보 확인중...",1);
                   httpRequestTack.execute(findId);

                }
            });
            tell1.setEnabled(true);
            tell2.setEnabled(true);
            tell3.setEnabled(true);
            ok.setEnabled(true);
            mDialog.show();

        }

    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    String result = (String) msg.obj;

                    if(result.equals("no Id")||result.equals("null")){

                        AlertDialog.Builder alert = new AlertDialog.Builder(SearchIdActivity.this,R.style.SearchAlertDialog);
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
                    }else{
                        AlertDialog.Builder alert = new AlertDialog.Builder(SearchIdActivity.this,R.style.SearchAlertDialog);
                        alert.setTitle("아이디 검색 결과");
                        String id = result.substring(0,result.length()-4);
                        id+="***";
                        alert.setMessage("회원 아이디 : "+id);
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                                mDialog.dismiss();
                            }
                        });
                        alert.show();

                    }
                   /* Log.d("da",member[0]);
                    Toast.makeText(LoginActivity.this,member[0], Toast.LENGTH_LONG).show();*/

                }
                break;
                case 2:
                    String result = (String) msg.obj;
                    if(result.equals("no Id")||result.equals("null")){

                        AlertDialog.Builder alert = new AlertDialog.Builder(SearchIdActivity.this,R.style.SearchAlertDialog);
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
                    }else{
                        AlertDialog.Builder alert = new AlertDialog.Builder(SearchIdActivity.this,R.style.SearchAlertDialog);
                        alert.setTitle("아이디 검색 결과");
                        String id = result.substring(0,result.length()-4);
                        id+="***";
                        alert.setMessage("회원 아이디 : "+id);
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                                mDialog.dismiss();
                            }
                        });
                        alert.show();

                    }
                   /* Log.d("da",member[0]);
                    Toast.makeText(LoginActivity.this,member[0], Toast.LENGTH_LONG).show();*/

                    break;
            }

        }
    };

    public void findId(View view) {

        mDialog = new Dialog(this, R.style.SearchAlertDialog);
        mDialog.setContentView(R.layout.search_id);
        LinearLayout tell_layout = (LinearLayout)mDialog.findViewById(R.id.layout_tell);
        tell_layout.setVisibility(View.GONE);
        LinearLayout email_layout = (LinearLayout)mDialog.findViewById(R.id.email);
        email_layout.setVisibility(View.VISIBLE);
        final EditText email1 = (EditText)mDialog.findViewById(R.id.email1);
        final EditText email2 = (EditText)mDialog.findViewById(R.id.email2);
        final EditText name = (EditText)mDialog.findViewById(R.id.searchName);
        Button ok = (Button)mDialog.findViewById(R.id.send);
        Button cancel = (Button)mDialog.findViewById(R.id.cancel);

        mDialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
                mDialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().equals("")||email1.getText().toString().equals("")||email2.getText().toString().equals("")){
                    Toast toast = Toast.makeText(getApplication(), "공백을 모두 입력해주세요", Toast.LENGTH_SHORT);
                    int offsetX=0;
                    int offsetY=0;
                    toast.setGravity(Gravity.CENTER,offsetX,offsetY);
                    toast.show();
                    return;
                }
                String findId = http + "findIdByEmail";

                String[] arrQueryname = {"name", "email"};
               String email = email1.getText().toString()+"@"+email2.getText().toString();
                String[] arrQuery = {name.getText().toString(), email};

                HttpRequestTack httpRequestTack = new HttpRequestTack(SearchIdActivity.this, mHandler, arrQuery, arrQueryname, "POST", "회원정보 확인중...",2);
                httpRequestTack.execute(findId);

            }
        });
    }
}
