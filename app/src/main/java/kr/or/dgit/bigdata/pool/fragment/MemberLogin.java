package kr.or.dgit.bigdata.pool.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import kr.or.dgit.bigdata.pool.LoginActivity;
import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.dto.Member;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

/**
 * Created by DGIT3-4 on 2018-04-19.
 */

public class MemberLogin extends Fragment implements View.OnClickListener {
    private EditText id;
    private EditText pw;
    private Button loginBtn;
    private String http ="http://192.168.0.239:8080/pool/restLogin/";
    private SharedPreferences login;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.member_login,container,false);
        id = view.findViewById(R.id.idtext);
        pw = view.findViewById(R.id.pw);
        loginBtn = view.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
        login = this.getActivity().getSharedPreferences("member", Context.MODE_PRIVATE);
        return view;
    }

    @Override
    public void onClick(View view) {

        if(id.getText().toString().equals("")||pw.getText().toString().equals("")){
            Toast.makeText(getContext(),"아이디와 비밀번호를 모두 입력해 주세요",Toast.LENGTH_SHORT).show();
            id.requestFocus();
            return;
        }

        String loginHttp = http+"login";

        String[] arrQueryname ={"id","pw"};
        String[] arrQuery={id.getText().toString(),pw.getText().toString()};

        HttpRequestTack httpRequestTack = new HttpRequestTack(getContext(),mHandler,arrQuery,arrQueryname,"POST","로그인..");
        httpRequestTack.execute(loginHttp);
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{
                    String result = (String)msg.obj;
                    try{

                        JSONArray arr = new JSONArray("["+result+"]");

                        JSONObject object= arr.getJSONObject(0);

                        int mno = object.getInt("mno");
                        Log.d("da","===========0"+mno+"");
                        if(mno==-1){
                            Toast.makeText(getContext(),"회원이 아닙니다.", Toast.LENGTH_LONG).show();
                            id.setText("");
                            id.requestFocus();
                        }else if(mno==-2){
                            Toast.makeText(getContext(),"비밀번호가 일치하지 않습니다", Toast.LENGTH_LONG).show();
                            pw.setText("");
                            pw.requestFocus();
                        }else if(mno>0){

                            Member m = new Member();

                            m.setMno(object.getInt("mno"));
                            m.setId(object.getString("id"));
                            m.setTitle(object.getString("title"));

                            SharedPreferences.Editor editor = login.edit();
                           //일단 삭제 후 데이터 저장
                            editor.clear();

                            editor.putString("id",object.getString("id"));
                            editor.putInt("mno",object.getInt("mno"));
                            editor.putString("title",object.getString("title"));
                            editor.commit();

                        }

                    }catch(Exception e){
                        e.printStackTrace();
                    }



                   /* Log.d("da",member[0]);
                    Toast.makeText(LoginActivity.this,member[0], Toast.LENGTH_LONG).show();*/

                }
                break;
            }
        }
    };
}
