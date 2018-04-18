package kr.or.dgit.bigdata.pool.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;

import kr.or.dgit.bigdata.pool.MainActivity;
import kr.or.dgit.bigdata.pool.R;

public class ClassInfoFragment extends Fragment implements View.OnClickListener{
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
            Toast.makeText(view.getContext(), "sdf", Toast.LENGTH_SHORT).show();
            ClassListDialog classListDialog = new ClassListDialog();
            classListDialog.getShowsDialog();
        }
    }
}
