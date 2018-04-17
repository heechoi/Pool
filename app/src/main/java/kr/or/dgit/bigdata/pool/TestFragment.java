package kr.or.dgit.bigdata.pool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by DGIT3-7 on 2018-04-17.
 */

public class TestFragment extends Fragment {

    public static TestFragment newInstance(int start){
        TestFragment cf = new TestFragment();
        Bundle args = new Bundle();
        args.putInt("start",start);
        cf.setArguments(args);
        return cf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_counter, container, false);
        Button btnIncrease = (Button)root.findViewById(R.id.btnIncrease);
        final TextView textCnt = (TextView)root.findViewById(R.id.txtCounter);

        Bundle args = getArguments();
        int start=0;
        if(args !=null){
            start = args.getInt("start");
        }

        textCnt.setText(Integer.toString(start));
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(textCnt.getText().toString());
                textCnt.setText(Integer.toString(count + 1));
            }
        });
        return root;
    }
}
