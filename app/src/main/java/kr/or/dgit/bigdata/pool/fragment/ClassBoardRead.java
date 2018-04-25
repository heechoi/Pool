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

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.or.dgit.bigdata.pool.R;

public class ClassBoardRead extends Fragment {
    ImageView imgview;
    Bitmap bmImg;
    back tack;
    Bitmap rotate;
    File filePath;
    String urlimg = "http://192.168.123.113:8080/pool/resources/upload/classboard/IMG8335610572756851618.jpg";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.classboard_read, container, false);
        imgview = root.findViewById(R.id.uplaod_img);
        tack = new back();
        tack.execute(urlimg);
        /*Picasso.with(getContext()).load(urlimg).placeholder(R.drawable.logo_white)//이미지가 존재하지 않을대체
                .resize(100, 100) // 이미지 크기를 재조정하고 싶을 경우
                .into(imgview);*/
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
                Log.d("bum",filePath.getAbsolutePath());
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
