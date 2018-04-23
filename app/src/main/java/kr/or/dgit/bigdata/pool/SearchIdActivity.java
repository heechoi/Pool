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
    private Button searhTell;
    private Dialog mDialog;
    private String http = "http://192.168.0.239:8080/pool/restLogin/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_id);
        getSupportActionBar().setElevation(0);
        setTitle("아이디 찾기");
        searhTell = findViewById(R.id.tell_search);
        searhTell.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        final EditText write;
        final EditText name;
        if (view.getId() == R.id.tell_search) {
            mDialog = new Dialog(this, R.style.SearchAlertDialog);
            mDialog.setContentView(R.layout.search_id);
            write = (EditText) mDialog.findViewById(R.id.searchKey);
            name = (EditText) mDialog.findViewById(R.id.searchName);
            write.setInputType(InputType.TYPE_CLASS_PHONE);
            write.setHint("010-1234-1234");
            Button ok = (Button) mDialog.findViewById(R.id.send);
            Button cancel = (Button) mDialog.findViewById(R.id.cancel);
            TextView label = (TextView) mDialog.findViewById(R.id.label);
            label.setText("연락처");
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
                    if (name.getText().toString().equals("") || write.getText().toString().equals("")) {
                        Toast.makeText(getApplication(), "공백을 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String findId = http + "findIdBytell";

                    String[] arrQueryname = {"name", "tell"};
                    String[] arrQuery = {name.getText().toString(), write.getText().toString()};

                    HttpRequestTack httpRequestTack = new HttpRequestTack(SearchIdActivity.this, mHandler, arrQuery, arrQueryname, "POST", "회원정보 확인중...");
                    httpRequestTack.execute(findId);
                    //  HttpRequestTack httpRequestTack = new HttpRequestTack();
                }
            });
            write.setEnabled(true);
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
                    mDialog.dismiss();
                    String result = (String) msg.obj;
                    if(result.equals("no Id")){
                        AlertDialog.Builder alert = new AlertDialog.Builder(getApplication(),R.style.SearchAlertDialog);
                        alert.setTitle("아이디 검색 결과");
                        alert.setMessage("일치하는 회원정보가 없습니다\n회원가입을 이용해 주세요");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                            }
                        });
                    }else{

                    }
                   /* Log.d("da",member[0]);
                    Toast.makeText(LoginActivity.this,member[0], Toast.LENGTH_LONG).show();*/

                }
                break;
            }
        }
    };

}
