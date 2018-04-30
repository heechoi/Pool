package kr.or.dgit.bigdata.pool;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import org.w3c.dom.Text;

public class BarCodeActivity extends AppCompatActivity{
    private ImageView view;
    private TextView code;
    private LinearLayout layout;
    SharedPreferences member;
    private int mno;
    private TextView name;
    private LinearLayout card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code);
        setTitle("회원코드");
        code = findViewById(R.id.code);
        view = findViewById(R.id.barcode);
        name = findViewById(R.id.name);

        card = findViewById(R.id.card);


        layout = (LinearLayout)findViewById(R.id.layout);
        member = getSharedPreferences("member",MODE_PRIVATE);

        mno = member.getInt("mno",0);
        name.setText(member.getString("name","")+" 회원 님");
        MultiFormatWriter gen =  new MultiFormatWriter();
        Log.d("dahee","barcode========:"+mno);
        String data = mno+"";

        code.setText(mno+"");
        try{
            final int WIDTH = 300;
            final int HEIGHT = 150;

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
