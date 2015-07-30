package com.contest.mobathon.activity;

import android.app.Activity;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.contest.mobathon.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SONY on 7/29/2015.
 */
public class ReferalsActivity extends ActionBarActivity {

    private Toolbar mToolbar;
    private ListView list;
    private ArrayList<String> names;
    private ArrayList<String> status;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.referals_activity);

        mToolbar = (Toolbar) findViewById(R.id.referel_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        list = (ListView) findViewById(R.id.referals_list);

        TextView userName = (TextView) findViewById(R.id.user_name);
        TextView points = (TextView) findViewById(R.id.points);

        Bundle extras = getIntent().getExtras();
        String name = extras.getString("name");
        int point = extras.getInt("points");
        names = extras.getStringArrayList("refnames");
        status = extras.getStringArrayList("status");

        userName.setText(name);
        points.setText(Integer.toString(point));

        initListView();

    }

    private void initListView()
    {

        final String[] matrix  = { "_id", "name", "value" };
        final String[] columns = { "name", "value" };
        final int[]    layouts = { android.R.id.text1, android.R.id.text2 };

        MatrixCursor cursor = new MatrixCursor(matrix);
        int key=0;
        for(int i=0; i<names.size(); i++) {
            cursor.addRow(new Object[]{key++, names.get(i), status.get(i)});
        }
        SimpleCursorAdapter data =
                new SimpleCursorAdapter(this,
                        R.layout.viewlist_two_items,
                        cursor,
                        columns,
                        layouts);

        list.setAdapter(data);

    }   // end of initListView()
}
