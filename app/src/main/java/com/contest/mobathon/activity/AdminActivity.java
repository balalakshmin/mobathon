package com.contest.mobathon.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.contest.mobathon.R;
import com.contest.mobathon.dao.ReferalDAO;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.rey.material.widget.SnackBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.TypeReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by SONY on 7/29/2015.
 */
public class AdminActivity extends ActionBarActivity {
    private PieChart platinumChart;
    private PieChart goldChart;
    private PieChart normalChart;
    private Toolbar mToolbar;
    private ArrayList<String> labels1;
    private ArrayList<Entry> entries1;
    private ArrayList<Entry> entries2;
    private ArrayList<Entry> entries3;
    private ArrayList<String> labels2;
    private ArrayList<String> labels3;
    private List<Map<String, String>> referals;
    private ArrayList<String> names;
    private ArrayList<String> status;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);



        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Referals");
        session = new SessionManager(getApplicationContext());

        platinumChart = (PieChart) findViewById(R.id.platinum);
        goldChart = (PieChart) findViewById(R.id.gold);
        normalChart = (PieChart) findViewById(R.id.normal);

        platinumChart.setCenterText("Platinum users");
        goldChart.setCenterText("Gold users");
        normalChart.setCenterText("Normal users");
        platinumChart.setDescription(null);
        goldChart.setDescription(null);
        normalChart.setDescription(null);
        platinumChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);
        goldChart.animateY(1800, Easing.EasingOption.EaseInOutQuad);
        normalChart.animateY(2000, Easing.EasingOption.EaseInOutQuad);

        entries1 = new ArrayList<>();
        labels1 = new ArrayList<String>();
        entries2 = new ArrayList<>();
        labels2 = new ArrayList<String>();
        entries3 = new ArrayList<>();
        labels3 = new ArrayList<String>();

        new TableData(this).execute();
    }
    public class TableData extends AsyncTask<Void, Integer, String> {

        Context mContext;
        private boolean internetcon;

        TableData(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Void... params) {

            /** check network connection and make call to the server */
                internetcon = true;
                String res = PostData();
                return res;
        }

        @Override
        protected void onPostExecute(String result) {
                if (result != null && result != "") {
                    ObjectMapper objectMapper = new ObjectMapper();
                    TypeFactory typeFactory;
                    try {
                        List<ReferalDAO> referal = objectMapper.readValue(result, new TypeReference<List<ReferalDAO>>() { });
                        for(int i=0,a=0,b=0,c=0; i<referal.size(); i++) {
                            if(Integer.parseInt(referal.get(i).getTotal_purchase())>10000000) {
                                labels1.add(referal.get(i).getName());
                                entries1.add(new Entry(Float.parseFloat(referal.get(i).getPoints()),a));
                                a++;

                            }
                            else if(Integer.parseInt(referal.get(i).getTotal_purchase())>5000000)
                            {
                                labels2.add(referal.get(i).getName());
                                entries2.add(new Entry(Float.parseFloat(referal.get(i).getPoints()),b));
                                b++;
                            }
                            else {
                                labels3.add(referal.get(i).getName());
                                entries3.add(new Entry(Float.parseFloat(referal.get(i).getPoints()),c));
                                c++;
                            }
                            referals = referal.get(i).getReferals();
                            names = new ArrayList<String>();
                            status = new ArrayList<String>();
                            for(int k=0; k<referals.size(); k++) {
                                names.add(referals.get(k).get("name"));
                                status.add(referals.get(k).get("status"));
                            }
                        }
                        setDataSet();
                        setClickListener(platinumChart, labels1);
                        setClickListener(goldChart, labels2);
                        setClickListener(normalChart, labels3);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }

        public void setClickListener(PieChart chart, final ArrayList<String> label) {
            chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                    if (e != null) {
                        int idx = e.getXIndex();
                        String name = label.get(idx);
                        int points = ((int) e.getVal());
                        Intent intent = new Intent(getApplicationContext(), ReferalsActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("points", points);
                        intent.putExtra("refnames",names);
                        intent.putExtra("status",status);

                        startActivity(intent);

                    }
                }

                @Override
                public void onNothingSelected() {

                }
            });

        }
        public void setDataSet() {

            PieDataSet dataset1 = new PieDataSet(entries1, "platinum");
            dataset1.setColors(ColorTemplate.VORDIPLOM_COLORS);
            PieData pieData1 = new PieData(labels1, dataset1);
            PieDataSet dataset2 = new PieDataSet(entries2, "platinum");
            dataset2.setColors(ColorTemplate.JOYFUL_COLORS);
            PieData pieData2 = new PieData(labels2, dataset2);
            PieDataSet dataset3 = new PieDataSet(entries3, "platinum");
            dataset3.setColors(ColorTemplate.LIBERTY_COLORS);
            PieData pieData3 = new PieData(labels3, dataset3);

            platinumChart.setData(pieData1);
            goldChart.setData(pieData2);
            normalChart.setData(pieData3);

        }

        public String PostData() {
            String s="";
            try
            {
                /** make a network call with latest time in db */
                HttpClient httpClient=new DefaultHttpClient();
                HttpPost httpPost=new HttpPost("http://192.168.43.15/Mobathon/getallreferals.php");
                List<NameValuePair> list=new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("name", "test"));
                list.add(new BasicNameValuePair("pass", "test"));

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
                Toast.makeText(mContext.getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
            }
            return return_text;

        }
        public void setUpSnackBar() {
            if(mContext instanceof  Activity) {

                SnackBar mSnackbar = SnackBar.make(mContext)
                        .applyStyle(R.style.Material_Widget_SnackBar_Mobile_MultiLine);

                mSnackbar.singleLine(false)
                        .actionText("CLOSE")
                        .actionClickListener(new SnackBar.OnActionClickListener() {
                            @Override
                            public void onActionClick(SnackBar snackBar, int i) {
                                snackBar.dismiss();
                            }
                        })
                        .actionTextColor(mContext.getResources().getColor(R.color.colorPrimary))
                        .text("No Internet Connection.\nLoading with existing data...")
                        .show((Activity) mContext);
            }

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(!session.isLoggedIn()) {
            menu.getItem(0).setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if(id == R.id.action_signout){
            session.logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
