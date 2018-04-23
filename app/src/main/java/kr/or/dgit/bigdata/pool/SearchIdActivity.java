package kr.or.dgit.bigdata.pool;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class SearchIdActivity extends AppCompatActivity implements View.OnClickListener {
    private Button searhTell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_id);
        getSupportActionBar().setElevation(0);
        setTitle("아이디 찾기");
        searhTell = findViewById(R.id.tell_search);
        searhTell.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.tell_search){
            CustomDialog dialog = new CustomDialog(this);
            dialog.show();
        }
}
    public class CustomDialog extends AlertDialog implements View.OnClickListener{
        private Button cancle;
        private Button ok;

        public CustomDialog(@NonNull Context context) {
            super(context);
        }

        public CustomDialog(Context context, int themeResId) {
            super(context, themeResId);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.search_id);
            cancle = findViewById(R.id.cancel);
            ok = findViewById(R.id.send);
            cancle.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.cancel){
                dismiss();
            }
        }

    }

}
