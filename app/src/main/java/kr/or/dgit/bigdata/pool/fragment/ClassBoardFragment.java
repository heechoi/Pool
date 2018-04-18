package kr.or.dgit.bigdata.pool.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.or.dgit.bigdata.pool.LoginActivity;
import kr.or.dgit.bigdata.pool.MainActivity;
import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class ClassBoardFragment extends Fragment {
    private String http = "http://192.168.0.60:8080/pool/restclassboard/classlist";

    public static ClassBoardFragment newInstance() {
        ClassBoardFragment cf = new ClassBoardFragment();
        return cf;
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{

                        String result = (String)msg.obj;
                        Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.classboard, container, false);
        Button cls_board_btn = (Button) root.findViewById(R.id.cls_board_btn);
        cls_board_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(getActivity())
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("반을 선택하세요")
                        .setItems(R.array.classboard_selected, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String[] arrays = getResources().getStringArray(R.array.classboard_selected);
                                String[] arrname = new String[] {"test"};
                                String[] arr  = new String[] {"1"};

                                new HttpRequestTack(getContext(),mHandler,"GET","message...").execute(http);
                            }
                        })
                        .setNegativeButton("취소", null).create().show();
            }
        });

        return root;
    }

}