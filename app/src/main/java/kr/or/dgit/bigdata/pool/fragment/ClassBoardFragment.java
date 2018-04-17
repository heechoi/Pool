package kr.or.dgit.bigdata.pool.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.or.dgit.bigdata.pool.R;

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
