package kr.or.dgit.bigdata.pool.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.or.dgit.bigdata.pool.JsonResult;
import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class ClassInfoFragment extends Fragment implements View.OnClickListener{

    private String http = "http://192.168.0.12:8080/pool/restclassinfo/classlist";
    Button classBtn;

    public static ClassInfoFragment newInstance(){
        ClassInfoFragment cf = new ClassInfoFragment();
        return cf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.classinfo, container, false);
        TabHost host = root.findViewById(R.id.host);
        classBtn = root.findViewById(R.id.btn_class);
        classBtn.setOnClickListener(this);

        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("출석현황");
        spec.setIndicator(null, ResourcesCompat.getDrawable(
                getResources(),R.drawable.tab_icon1 ,null
        ));
        spec.setContent(R.id.check_attend);
        host.addTab(spec);

        spec = host.newTabSpec("회원정보");
        spec.setIndicator(null, ResourcesCompat.getDrawable(
                getResources(), R.drawable.tab_icon3,null
        ));
        spec.setContent(R.id.member_info);
        host.addTab(spec);

        spec = host.newTabSpec("메시지");
        spec.setIndicator(null, ResourcesCompat.getDrawable(
                getResources(), android.R.drawable.ic_dialog_email,null
        ));
        spec.setContent(R.id.message_send);
        host.addTab(spec);


        return root;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_class){
            new android.support.v7.app.AlertDialog.Builder(getActivity())
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle("반을 선택하세요")
                    .setItems(R.array.classboard_selected, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String[] arrays = getResources().getStringArray(R.array.classboard_selected);
                            Toast.makeText(getActivity(), arrays[i], Toast.LENGTH_SHORT).show();

                            new HttpRequestTack(getContext(),ClassInfoFragment.this,"GET","정보를 가져오고 있습니다...").execute(http+"?tno=1004");
                        }
                    })
                    .setNegativeButton("취소", null).create().show();
        }
    }

    @Override
    public void setResult(String result) {
        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();



    }
}
