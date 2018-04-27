package kr.or.dgit.bigdata.pool.fragment;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import org.w3c.dom.Text;

import kr.or.dgit.bigdata.pool.MainActivity;
import kr.or.dgit.bigdata.pool.R;

/**
 * Created by DGIT3-4 on 2018-04-27.
 */

public class QnaInsertFragment extends Fragment implements View.OnClickListener {
     private Button cancel;
    private TextView writer;
    private TextView email;
     public static QnaInsertFragment newInstance(){
        QnaInsertFragment qna = new QnaInsertFragment();
        return qna;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.qna_insert,container,false);

        SharedPreferences member = this.getActivity().getSharedPreferences("member",0);

        cancel = root.findViewById(R.id.cancel);
       cancel.setOnClickListener(this);

       writer = root.findViewById(R.id.writer);
       email = root.findViewById(R.id.email);
       writer.setText(member.getString("name",""));
       email.setText(member.getString("email",""));
        return root;
    }

    @Override
    public void onClick(View view) {
        getActivity().onBackPressed();
    }
}
