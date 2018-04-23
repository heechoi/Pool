package kr.or.dgit.bigdata.pool.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import kr.or.dgit.bigdata.pool.R;

public class ClassBoardInsert extends Fragment implements DialogInterface.OnClickListener{
    AlertDialog customDialog;
    Button camaraBtn;
    Button galleryBtn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.classboard_insert,container,false);
        Button imgBtn = (Button)root.findViewById(R.id.img_btn);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.custom_dialog,null);
                galleryBtn  = v.findViewById(R.id.gallery);
                camaraBtn = v.findViewById(R.id.camara);
                camaraBtn.setOnClickListener(this);
                galleryBtn.setOnClickListener(this);
                builder.setView(v);
                builder.setNegativeButton("취소",null);
                customDialog = builder.create();
                customDialog.show();
            }
        });
        return root;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }
}
