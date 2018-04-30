package kr.or.dgit.bigdata.pool.fragment;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import kr.or.dgit.bigdata.pool.R;

/**
 * Created by DGIT3-4 on 2018-04-30.
 */

public class BarCodeFragment extends BottomSheetDialogFragment {
    private ImageView barCode;
    public static BottomSheetDialogFragment newInstance() {
        BarCodeFragment bc = new BarCodeFragment();
        return bc;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.barcode_fragment,container,false);
        SharedPreferences  member = this.getActivity().getSharedPreferences("member",0);

        int mno = member.getInt("mno",0);
        barCode = view.findViewById(R.id.big_code);
        String data = mno+"";
        MultiFormatWriter gen =  new MultiFormatWriter();
        try{
            LinearLayout linearLayout = view.findViewById(R.id.layout);
            Log.d("dahee",view.getMeasuredWidth()+"");
            final int WIDTH = 900;
            final int HEIGHT = 300;

            BitMatrix byteamap = gen.encode(data, BarcodeFormat.CODE_128,WIDTH,HEIGHT);
            Bitmap bitmap = Bitmap.createBitmap(WIDTH,HEIGHT,Bitmap.Config.ARGB_8888);

            for(int i=0;i<WIDTH;i++){
                for(int j=0;j<HEIGHT;j++){
                    bitmap.setPixel(i,j,byteamap.get(i,j)? Color.BLACK:Color.WHITE);
                }

                barCode.setImageBitmap(bitmap);
                barCode.invalidate();

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return view;

    }
}
