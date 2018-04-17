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

public class ClassBoardFragment extends Fragment {

    public static ClassBoardFragment newInstance(){
        ClassBoardFragment cf = new ClassBoardFragment();
        return cf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.classboard, container, false);
        return root;
    }
}
