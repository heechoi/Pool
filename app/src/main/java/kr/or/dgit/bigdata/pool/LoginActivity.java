package kr.or.dgit.bigdata.pool;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText id;
    private EditText pw;
    private Button loginBtn;

    private String http ="http://192.168.0.239:8080/pool/restLogin/loginId";
    private HttpURLConnection urlConnection = null;
    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id=findViewById(R.id.id);
        pw = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        new HttpRequestTack().execute(http);
    }

    private class HttpRequestTack extends AsyncTask<String,Void,String>{
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            progressDlg = ProgressDialog.show(LoginActivity.this,"Wait","Login....");
        }

        @Override
        protected void onPostExecute(String result) {
            progressDlg.dismiss();
            Toast.makeText(LoginActivity.this,result,Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer sb = new StringBuffer();
            BufferedReader br = null;
            HttpURLConnection con = null;
            String line = null;

            try{
                URL url = new URL(strings[0]);
                System.out.print(strings[0]);

                con =(HttpURLConnection) url.openConnection();

                con.setRequestMethod("GET");

                con.setDoInput(true);
                con.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter("id",id.getText().toString());
                String query = builder.build().getEncodedQuery();


                OutputStream os = con.getOutputStream();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();


                os.close();


                if(con!=null){

                    if(con.getResponseCode() == HttpURLConnection.HTTP_OK){
                        br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    }
                }
                while((line = br.readLine())!=null){
                    sb.append(line);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try{
                    br.close();
                    con.disconnect();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }
}
