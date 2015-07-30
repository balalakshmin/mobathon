package com.contest.mobathon.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SONY on 7/30/2015.
 */
public class SendAddress extends AsyncTask<Void, Integer, String> {

    Context context;
    String name;
    String street;
    String landmark;
    String location;
    String pincode;


    public SendAddress(Context mContext, String name, String street, String location, String landmark, String pincode) {
        this.context = mContext;
        this.name = name;
        this.street = street;
        this.location = location;
        this.landmark = landmark;
        this.pincode = pincode;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(Void... params) {

        String res = PostData();
        return res;
    }

    @Override
    protected void onPostExecute(String s) {
        Toast.makeText(context.getApplicationContext(), "Successfully sent" , Toast.LENGTH_SHORT).show();
    }

    public String PostData() {
        String s="";
        try
        {
            /** make a network call with latest time in db */
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost("http://192.168.43.15/Mobathon/verifyaddress.php");
            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", name));
            list.add(new BasicNameValuePair("street", street));
            list.add(new BasicNameValuePair("landmark", landmark));
            list.add(new BasicNameValuePair("location", location));
            list.add(new BasicNameValuePair("pincode", pincode));

            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=	httpClient.execute(httpPost);

            HttpEntity httpEntity=httpResponse.getEntity();
            s = readResponse(httpResponse);

        }
        catch(Exception exception) 	{
            Log.e("MainActivity", "Error connecting to server");
        }
        return s;

    }
    public String readResponse(HttpResponse res) {
        InputStream is=null;
        String return_text="";
        try {
            is=res.getEntity().getContent();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is));
            String line="";
            StringBuffer sb=new StringBuffer();
            while ((line=bufferedReader.readLine())!=null)
            {
                sb.append(line);
            }
            return_text=sb.toString();
        } catch (Exception e)
        {
            Toast.makeText(context.getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
        }
        return return_text;

    }

}

