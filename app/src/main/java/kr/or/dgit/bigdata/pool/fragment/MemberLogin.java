package kr.or.dgit.bigdata.pool.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import kr.or.dgit.bigdata.pool.JoinActivity;
import kr.or.dgit.bigdata.pool.LoginActivity;
import kr.or.dgit.bigdata.pool.MainActivity;
import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.SearchIdActivity;
import kr.or.dgit.bigdata.pool.SearchPwActivity;
import kr.or.dgit.bigdata.pool.dto.Member;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class MemberLogin extends Fragment implements View.OnClickListener {
    private EditText id;
    private EditText pw;
    private Button loginBtn;
    private String http ="http://rkd0519.cafe24.com/pool/restLogin/";
    private SharedPreferences login;
    private SharedPreferences tlogin;
    private SharedPreferences state;
    private TextView join;
    private TextView searchId;
    private TextView searchPw;
    private ImageView stateImg;
    private TextView stateLable;
    private LinearLayout stateLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.member_login,container,false);
        id = view.findViewById(R.id.idtext);
        pw = view.findViewById(R.id.pw);
        loginBtn = view.findViewById(R.id.loginBtn);
        searchId = view.findViewById(R.id.searchId);
        searchId.setOnClickListener(this);
        join = view.findViewById(R.id.join);
        loginBtn.setOnClickListener(this);
        join.setOnClickListener(this);
        searchPw = view.findViewById(R.id.searchPw);
        searchPw.setOnClickListener(this);

        stateImg = view.findViewById(R.id.state_img);
        stateLable = view.findViewById(R.id.state_label);
        stateLayout = view.findViewById(R.id.state);

        stateLayout.setOnClickListener(this);

        login = this.getActivity().getSharedPreferences("member", Context.MODE_PRIVATE);
        tlogin = this.getActivity().getSharedPreferences("Admin",Context.MODE_PRIVATE);
        state = this.getActivity().getSharedPreferences("state",Context.MODE_PRIVATE);



        return view;

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.loginBtn){
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
        if(view.getId()==R.id.join){
          Intent intent = new Intent(getActivity(),JoinActivity.class);
          startActivity(intent);
          getActivity().overridePendingTransition(R.anim.login,R.anim.login_out);
        }

        if(view.getId()==R.id.searchId){
            Intent intent = new Intent(getActivity(),SearchIdActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.login,R.anim.login_out);
        }

        if(view.getId()==R.id.searchPw){
            Intent intent = new Intent(getActivity(),SearchPwActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.login,R.anim.login_out);
        }

        if(view.getId()==R.id.state){

            if(stateLable.getHint().toString().equals("전")){
                stateLable.setHint("후");
                stateImg.setImageResource(R.drawable.login_check_after);
                stateLable.setTextColor(Color.WHITE);
            }else if(stateLable.getHint().toString().equals("후")){
              stateLable.setHint("전");
              stateLable.setTextColor(Color.parseColor("#e4e4e4"));
                stateImg.setImageResource(R.drawable.login_check_before);
            }

        }
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
                         if(mno==-2||mno==-1) {
                            Toast.makeText(getContext(), "아이디,비밀번호 중 일치하지 않은 정보가 있습니다.", Toast.LENGTH_LONG).show();
                            id.setText("");
                            pw.setText("");
                            id.requestFocus();
                        }else if(mno==-3){
                            Toast.makeText(getContext(), "탈퇴한 회원입니다 로그인 할 수 없습니다", Toast.LENGTH_LONG).show();
                            id.requestFocus();
                            pw.setText("");
                        } else if(mno>0){

                            Member m = new Member();

                            m.setMno(object.getInt("mno"));
                            m.setId(object.getString("id"));
                            m.setTitle(object.getString("title"));
                            m.setName(object.getString("name"));

                            //강사정보 삭제
                            SharedPreferences.Editor tEditor = tlogin.edit();
                            tEditor.clear();
                            tEditor.commit();
                            SharedPreferences.Editor editor = login.edit();
                           //일단 삭제 후 데이터 저장
                            editor.clear();

                            editor.putString("id",object.getString("id"));
                            editor.putInt("mno",object.getInt("mno"));
                            editor.putString("title",object.getString("title"));
                            editor.putString("name",object.getString("name"));
                            editor.putString("age",object.getString("age"));
                            editor.putString("gender",object.getString("gender"));
                            editor.putString("tell",object.getString("tell"));
                            editor.putString("email",object.getString("email"));

                            editor.commit();

                             if(stateLable.getHint().toString().equals("전")){
                                 SharedPreferences.Editor edit = state.edit();
                                 edit.clear();
                                 edit.putInt("state",1);
                                 edit.commit();
                             }else if(stateLable.getHint().toString().equals("후")){
                                 SharedPreferences.Editor edit = state.edit();
                                 edit.clear();
                                 edit.putInt("state",1);
                                 edit.commit();
                             }



                             Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            getActivity().overridePendingTransition(R.anim.login,R.anim.login_out);
                            startActivity(intent);
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
