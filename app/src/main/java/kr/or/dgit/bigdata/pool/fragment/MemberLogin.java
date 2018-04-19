package kr.or.dgit.bigdata.pool.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.or.dgit.bigdata.pool.R;

/**
 * Created by DGIT3-4 on 2018-04-19.
 */

public class MemberLogin extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.member_login,container,false);
        return view;
    }
}
