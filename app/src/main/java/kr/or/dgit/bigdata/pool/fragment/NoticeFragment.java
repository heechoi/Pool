package kr.or.dgit.bigdata.pool.fragment;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.SendException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import kr.or.dgit.bigdata.pool.MainActivity;
import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class NoticeFragment extends Fragment implements View.OnClickListener{

    private String http = "http://rkd0519.cafe24.com/pool";
    EditText noticeText;
    Button btn;



    public static NoticeFragment newInstance(){
        NoticeFragment cf = new NoticeFragment();
        return cf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.notice_send, container, false);
        noticeText = root.findViewById(R.id.notice_text);
        btn = root.findViewById(R.id.send);
        btn.setOnClickListener(this);
        return root;
    }


    @Override
    public void onClick(View view) {


        if(view.getId() == R.id.send){
            String notice_text = String.valueOf(noticeText.getText());
            new HttpRequestTack(getContext(),mHandler,"GET","정보를 가져오고 있습니다...").execute(http+"/messaging/send?text="+notice_text);
        }

    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{
                    String result = (String)msg.obj;
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
/*
                    try {
                        JSONObject order = new JSONObject(result);
                        Object name = order.get("name");
                        Object tel = order.get("tell");
                        TextView nameText = getView().findViewById(R.id.name);
                        nameText.setText((String)name);

                    } catch (JSONException e) {
                        Log.i("Json_parser", e.getMessage());
                    }*/
                }
                break;
            }
        }
    };




}
