package kr.or.dgit.bigdata.pool.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import kr.or.dgit.bigdata.pool.BuildConfig;
import kr.or.dgit.bigdata.pool.MainActivity;
import kr.or.dgit.bigdata.pool.R;


public class ClassBoardInsert extends Fragment{
    CustomDialog customDialog;
    File filePath;
    int reqWidth;
    int reqHeight;
    LinearLayout mLinearLayout;
    Uri mUri;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.classboard_insert,container,false);
        Button imgBtn = (Button)root.findViewById(R.id.img_btn);
        mLinearLayout = root.findViewById(R.id.classboard_insert);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog = new CustomDialog(getContext());
                customDialog.show();
            }
        });
        reqWidth = getResources().getDimensionPixelSize(R.dimen.request_image_width);
        reqHeight = getResources().getDimensionPixelSize(R.dimen.request_image_height);
        return root;
    }
    public class CustomDialog extends android.app.AlertDialog implements View.OnClickListener{
        Button camaraBtn;
        Button galleryBtn;

        public CustomDialog(@NonNull Context context) {
            super(context);
        }

        public CustomDialog(Context context, int themeResId) {
            super(context, themeResId);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.custom_dialog);
            camaraBtn = findViewById(R.id.camara);
            galleryBtn = findViewById(R.id.gallery);
            camaraBtn.setOnClickListener(this);
            galleryBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.camara){
                if(ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    try{
                        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/pool";
                        File dir = new File(dirPath);
                        if(!dir.exists()){
                            dir.mkdir();
                        }

                        filePath = File.createTempFile("IMG",".jpg",dir);
                        if(!filePath.exists()){
                            filePath.createNewFile();
                        }
                        Uri photoURI = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider",filePath);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                        startActivityForResult(intent,10);

                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }else{
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
                }
            }else if(view.getId()==R.id.gallery){
                Toast.makeText(getContext(),"갤러리",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,20);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode ==10&& resultCode == getActivity().RESULT_OK){

            if(filePath !=null){
                customDialog.dismiss();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                try{
                    InputStream in = new FileInputStream(filePath);
                    BitmapFactory.decodeStream(in,null,options);
                    in.close();
                    in = null;
                }catch (Exception e){
                    e.printStackTrace();
                }
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                Bitmap bitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath());
                Bitmap rotated = rotateImage(bitmap);
                ImageView img = new ImageView(getContext());
                img.setImageBitmap(rotated);
                img.setLayoutParams(lp);
                mLinearLayout.addView(img);
            }
        }else if(requestCode ==20 &&resultCode == getActivity().RESULT_OK){
            Toast.makeText(getContext(),"완성",Toast.LENGTH_SHORT).show();
            mUri = data.getData();
            File fileimg = new File(mUri.getPath());
            customDialog.dismiss();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try{
                InputStream in = new FileInputStream(fileimg.getPath());
                BitmapFactory.decodeStream(in,null,options);
                in.close();
                in = null;
            }catch (Exception e){
                e.printStackTrace();
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            Bitmap bitmap = BitmapFactory.decodeFile(fileimg.getPath());
            Bitmap rotated = rotateImage(bitmap);
            ImageView img = new ImageView(getContext());
            img.setImageBitmap(rotated);
            img.setLayoutParams(lp);
            mLinearLayout.addView(img);
        }
    }

    private Bitmap rotateImage(Bitmap bitmap){
        ExifInterface exifInterface = null;
        try{
            exifInterface = new ExifInterface(filePath.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
        Matrix m = new Matrix();
        switch(orientation){
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
        Bitmap rotated = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
        return rotated;
    }
}
