package kr.or.dgit.bigdata.pool;

import android.Manifest;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class BarCodeActivity extends AppCompatActivity {
    ImageView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code);
        MultiFormatWriter gen =  new MultiFormatWriter();
        SharedPreferences member = getSharedPreferences("member",MODE_PRIVATE);

        String data = "1234560";
        view = findViewById(R.id.barcode);

        try{
            final int WIDTH = view.getMaxWidth();
            final int HEIGHT = view.getMaxHeight();

            BitMatrix byteamap = gen.encode(data, BarcodeFormat.CODE_128,WIDTH,HEIGHT);
            Bitmap bitmap = Bitmap.createBitmap(WIDTH,HEIGHT,Bitmap.Config.ARGB_8888);

            for(int i=0;i<WIDTH;i++){
                for(int j=0;j<HEIGHT;j++){
                    bitmap.setPixel(i,j,byteamap.get(i,j)? Color.BLACK:Color.WHITE);
                }

                view.setImageBitmap(bitmap);
                view.invalidate();

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
