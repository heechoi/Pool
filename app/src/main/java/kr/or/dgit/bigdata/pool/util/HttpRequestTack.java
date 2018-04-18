package kr.or.dgit.bigdata.pool.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequestTack extends AsyncTask<String,Void,String> {
    private Context mContext;
    ProgressDialog progressDlg;
    private String[] arrQuery;
    private String[] arrQueryname;
    private String type;
    private static String result;

    public HttpRequestTack(Context context, String type) {
        mContext = context;
        this.type = type;
    }

    public HttpRequestTack(Context context, String[] arrQuery, String[] arrQueryname, String type) {
        mContext = context;
        this.arrQuery = arrQuery;
        this.arrQueryname = arrQueryname;
        this.type = type;
    }

    @Override
    protected void onPreExecute() {
        progressDlg = ProgressDialog.show(mContext,"Wait","Login....");
    }

    @Override
    protected void onPostExecute(String result2) {
        progressDlg.dismiss();
        Toast.makeText(mContext,result,Toast.LENGTH_SHORT).show();
        this.result = result2;
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

            con.setRequestMethod(type);

            con.setDoInput(true);
            con.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder();
            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            if(arrQuery !=null){
                for(int i=0; i <arrQuery.length; i++){
                    builder.appendQueryParameter(arrQueryname[i],arrQuery[i]);
                }
                String query = builder.build().getEncodedQuery();
                writer.write(query);
            }
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

