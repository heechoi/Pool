package kr.or.dgit.bigdata.pool;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SearchPwActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_pw);
    }

    public void findPwEmail(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SearchPwActivity.this);
        View v = getLayoutInflater().inflate(R.layout.search_tell,null);
        TextView title = (TextView)v.findViewById(R.id.main_title);
        title.setText("비밀번호 찾기");
        EditText name = (EditText)v.findViewById(R.id.name);
        mBuilder.setView(v);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }
}
