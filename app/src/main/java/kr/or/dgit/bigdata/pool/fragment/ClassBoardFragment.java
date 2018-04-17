package kr.or.dgit.bigdata.pool.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import kr.or.dgit.bigdata.pool.MainActivity;
import kr.or.dgit.bigdata.pool.R;

public class ClassBoardFragment extends Fragment {
    ArrayAdapter<CharSequence> adspin;
    public static ClassBoardFragment newInstance(){
        ClassBoardFragment cf = new ClassBoardFragment();
        return cf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.classboard, container, false);
        Spinner spinner = (Spinner)root.findViewById(R.id.cls_board_spinner);
        adspin = ArrayAdapter.createFromResource(getActivity(),R.array.classboard_selected,android.R.layout.simple_spinner_item);
        adspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adspin);
        spinner.setPrompt("반을 선택하세요");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(),adspin.getItem(i) + "을 선택 했습니다.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return root;
    }
}
