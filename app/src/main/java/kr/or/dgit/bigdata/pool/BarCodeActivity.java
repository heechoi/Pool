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
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import org.w3c.dom.Text;

public class BarCodeActivity extends AppCompatActivity {
    private ImageView view;
    private TextView code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code);
        setTitle("회원코드");
        code = findViewById(R.id.code);
        MultiFormatWriter gen =  new MultiFormatWriter();
        SharedPreferences member = getSharedPreferences("member",MODE_PRIVATE);
        int mno = member.getInt("mno",0);

        String data = mno+"";
        view = findViewById(R.id.barcode);
        code.setText(mno+"");
        try{
            final int WIDTH = 300;
            final int HEIGHT = 130;

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
