package kr.or.dgit.bigdata.pool.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.Locale;

import kr.or.dgit.bigdata.pool.ClassboardUpdateActivity;
import kr.or.dgit.bigdata.pool.MainActivity;
import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.TeacherInfoActivity;
import kr.or.dgit.bigdata.pool.onKeyBackPressedListener;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

import static android.speech.tts.TextToSpeech.ERROR;
/**
 * Created by DGIT3-4 on 2018-04-27.
 */

public class QnaInsertFragment extends Fragment implements View.OnClickListener,onKeyBackPressedListener {
    private Button cancel;
    private TextView writer;
    private EditText title;
    private EditText content;
    private EditText pw;
    private ScrollView scrollView;
    private LinearLayout contentLayout;
    private Button ok;
    private String http ="http://rkd0519.cafe24.com/pool/restQna/";
    SharedPreferences member;
    SharedPreferences admin;
    public static QnaInsertFragment newInstance() {
        QnaInsertFragment qna = new QnaInsertFragment();
        return qna;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.qna_insert, container, false);

        member = this.getActivity().getSharedPreferences("member", 0);
        admin = this.getActivity().getSharedPreferences("Admin",0);
        scrollView = root.findViewById(R.id.scrollView);
        contentLayout = root.findViewById(R.id.contentLayout);
        content = root.findViewById(R.id.qna);
        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.requestFocus();
            }
        });
        cancel = root.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        ok=root.findViewById(R.id.ok);
        ok.setOnClickListener(this);
        writer = root.findViewById(R.id.writer);

        if(member.getInt("mno",0)!=0){
            writer.setText(member.getString("name", ""));
        }

        if(admin.getInt("tno",0)!=0){
            writer.setText(admin.getString("name", ""));
        }



        title = root.findViewById(R.id.title);
        content.setMovementMethod(new ScrollingMovementMethod());
        content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        pw= root.findViewById(R.id.pw);

        return root;
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cancel) {
            onBack();
        }

        if(view.getId()==R.id.ok){
            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getContext(), R.style.SearchAlertDialog);
            alert.setTitle("문의하기");
            alert.setMessage("문의하신 내용이 접수하시겠습니까?");
            alert.setCancelable(false);
            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    dialogInterface.dismiss();

                    if(title.getText().toString().isEmpty()||content.getText().toString().isEmpty()||pw.getText().toString().isEmpty()){
                        Toast toast = Toast.makeText(getContext(),"공백이 존재합니다\n 모두 입력해주세요",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        return;
                    }

                    String qnaHttp = http+"insert";
                    String contentReplace = content.getText().toString();
                    String qnaContent = contentReplace.replace(System.getProperty("line.separator"),"<br>");

                    String[] arr = {writer.getText().toString(),title.getText().toString(),qnaContent,pw.getText().toString(),member.getString("id","")};
                    String[] arrname ={"writer","title","content","pw","id"};

                    new HttpRequestTack(getContext(), mHandler, arr, arrname, "POST", "문의내역 접수중...").execute(qnaHttp);
                }
            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    dialog.cancel();
                }
            });
            alert.show();

        }
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:{
                    String result = (String)msg.obj;
                    if(result.equals("success")){
                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getContext(), R.style.SearchAlertDialog);
                        alert.setTitle("문의하기");
                        alert.setMessage("문의하신 내용이 접수되었습니다\n답변까지는 최소2~3일이 소요됩니다\n감사합니다");
                        alert.setCancelable(false);
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                                getActivity().onBackPressed();
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
    public void onBack() {

        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getContext(), R.style.SearchAlertDialog);
        alert.setTitle("문의하기");
        alert.setMessage("문의하신 내용을 접수하지 않고 나가겠습니까?");
        alert.setCancelable(false);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.cancel();
                dialogInterface.dismiss();
                getActivity().onBackPressed();

            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
            }
        });

        alert.show();

    }

}
