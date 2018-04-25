package kr.or.dgit.bigdata.pool.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.or.dgit.bigdata.pool.R;

public class ClassBoardRead extends Fragment {
    TextView tvTitle;
    TextView tvWriter;
    TextView tvRegdate;
    TextView tvContent;
    ImageView imgview;
    Bitmap bmImg;
    back tack;
    Bitmap rotate;
    File filePath;
    String url = "http://192.168.0.60:8080";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.classboard_read, container, false);
        imgview = root.findViewById(R.id.imgview);
        tvTitle = root.findViewById(R.id.title);
        tvWriter = root.findViewById(R.id.writer);
        tvRegdate = root.findViewById(R.id.tvRegdate);
        tvContent = root.findViewById(R.id.content);

        Bundle bundle = getArguments();
        tvTitle.setText((String)bundle.get("title"));
        tvContent.setText((String) bundle.get("content"));
        tvWriter.setText((String)bundle.get("id"));
        Date date = new Date((int)bundle.get("regdate"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        String text = sdf.format(date) + " ㆍ " + bundle.get("readcnt")+" 읽음";
        String imgpath = (String)bundle.get("imgpath");
        tvRegdate.setText(text);
        Log.d("bum","이미지 경로"+imgpath);
        if (imgpath !=null && !imgpath.equalsIgnoreCase("") && !imgpath.equalsIgnoreCase("null")){
            Log.d("bum","이미지 경로널 아님");
            tack = new back();
            tack.execute(url+imgpath);
        }
        return root;
    }
    private class back extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pool";
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdir();
                }

                filePath = new File("/storage/emulated/0/pool/test.jpg");
                filePath.createNewFile();
                Log.d("bum","파일패스 "+filePath.getAbsolutePath());
                OutputStream out = new FileOutputStream(filePath);
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = is.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                //out.close();
                bmImg = BitmapFactory.decodeFile(filePath.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
            }
            rotate = rotateImage(bmImg);
            return rotate;
        }

        protected void onPostExecute(Bitmap img) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1200);
            imgview.setLayoutParams(lp);
            imgview.setImageBitmap(rotate);
        }
    }

    private Bitmap rotateImage(Bitmap bitmap) {
        ExifInterface exifInterface = null;
        Log.d("bum","===========시작");
        Log.d("bum","===========1");
        try {
            if (filePath != null) {
                Log.d("bum","===========2");
                exifInterface = new ExifInterface(filePath.getAbsolutePath());
                Log.d("bum","===========3");
            } else {
              //  exifInterface = new ExifInterface(galleryPath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix m = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                m.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                m.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                m.setRotate(270);
                break;
            default:
                m.setRotate(0);
        }
        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        return rotated;
    }
}
