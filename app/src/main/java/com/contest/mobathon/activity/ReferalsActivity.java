package com.contest.mobathon.activity;

import android.app.Activity;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.contest.mobathon.R;

import java.util.List;

/**
 * Created by SONY on 7/29/2015.
 */
public class ReferalsActivity extends ActionBarActivity {

    private Toolbar mToolbar;
    private ListView list;
    SessionManager session;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.referals_activity);
        session = new SessionManager(getApplicationContext());
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

        userName.setText(name);
        points.setText(Integer.toString(point));

        initListView();

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


    private void initListView()
    {
        final String   referal    = "Referral person";
        final String   status = "Status";

        final String[] matrix  = { "_id", "name", "value" };
        final String[] columns = { "name", "value" };
        final int[]    layouts = { android.R.id.text1, android.R.id.text2 };

        MatrixCursor cursor = new MatrixCursor(matrix);
        int key=0;
        cursor.addRow(new Object[] { key++, referal, status });
        cursor.addRow(new Object[] { key++, referal, status });

        SimpleCursorAdapter data =
                new SimpleCursorAdapter(this,
                        R.layout.viewlist_two_items,
                        cursor,
                        columns,
                        layouts);

        list.setAdapter(data);

    }   // end of initListView()
}
