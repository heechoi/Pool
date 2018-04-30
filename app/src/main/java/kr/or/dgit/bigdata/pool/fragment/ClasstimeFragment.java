package kr.or.dgit.bigdata.pool.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.or.dgit.bigdata.pool.R;

/**
 * Created by DGIT3-7 on 2018-04-30.
 */

public class ClasstimeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.classtime,container,false);
        return root;
    }
}
