package com.contest.mobathon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.contest.mobathon.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * Created by SONY on 7/29/2015.
 */
public class AdminActivity extends ActionBarActivity {
    private PieChart platinumChart;
    private PieChart goldChart;
    private PieChart normalChart;
    private Toolbar mToolbar;
    SessionManager session;
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

        ArrayList<Entry> entries1 = new ArrayList<>();
        final ArrayList<String> labels1 = new ArrayList<String>();
        ArrayList<Entry> entries2 = new ArrayList<>();
        final ArrayList<String> labels2 = new ArrayList<String>();
        ArrayList<Entry> entries3 = new ArrayList<>();
        final ArrayList<String> labels3 = new ArrayList<String>();

        //TODO:temp data. change it
        labels1.add("Ram");
        labels1.add("Ram");
        labels1.add("Ram");
        labels1.add("Ram");
        labels1.add("Ram");

        labels2.add("Ram");
        labels2.add("Ram");
        labels2.add("Ram");
        labels2.add("Ram");

        labels3.add("Ram");
        labels3.add("Ram");
        labels3.add("Ram");

        //platinum dataset
        for(int i=0; i<labels1.size(); i++) {
            entries1.add(new BarEntry(500000, i));
        }
        //gold dataset
        for(int i=0; i<labels2.size(); i++) {
            entries2.add(new BarEntry(50000, i));
        }
        //normal dataset
        for(int i=0; i<labels3.size(); i++) {
            entries3.add(new BarEntry(5000, i));
        }

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

        platinumChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if (e != null) {
                    int idx = e.getXIndex();
                    String name = labels1.get(idx);
                    int points = ((int) e.getVal());
                    Intent intent = new Intent(getApplicationContext(), ReferalsActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("points", points);
                    startActivity(intent);

                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
        goldChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if(e!=null) {
                    int idx = e.getXIndex();
                    String name = labels2.get(idx);
                    int points = ((int) e.getVal());
                    Intent intent = new Intent(getApplicationContext(), ReferalsActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("points", points);
                    startActivity(intent);

                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
        normalChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if(e!=null) {
                    int idx = e.getXIndex();
                    String name = labels3.get(idx);
                    int points = ((int) e.getVal());
                    Intent intent = new Intent(getApplicationContext(), ReferalsActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("points", points);
                    startActivity(intent);

                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
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
