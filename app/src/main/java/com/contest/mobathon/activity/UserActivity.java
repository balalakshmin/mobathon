package com.contest.mobathon.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.contest.mobathon.R;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.FloatingActionButton;

/**
 * Created by SONY on 7/29/2015.
 */
public class UserActivity extends ActionBarActivity {
    private Toolbar mToolbar;
    private ListView list;
    private FloatingActionButton fab;
    private static final int PICK_CONTACT = 1234;
    private String name;
    private String number;
    private SessionManager session;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
        session = new SessionManager(getApplicationContext());
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Your referals");

        list = (ListView) findViewById(R.id.referals_list2);

        initListView();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it= new Intent(Intent.ACTION_PICK,  ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(it, PICK_CONTACT);
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

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT) :
                name = null;
                number = null;
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                        if (phones.moveToNext()) {
                            number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }

                    }
                    Dialog.Builder builder = new SimpleDialog.Builder(R.style.Material_App_Dialog_Simple_Light){
                        @Override
                        public void onPositiveActionClicked(DialogFragment fragment) {
                            Toast.makeText(getApplicationContext(), "name : " + name + "Number: " + number, Toast.LENGTH_LONG).show();
                            super.onPositiveActionClicked(fragment);
                        }

                        @Override
                        public void onNegativeActionClicked(DialogFragment fragment) {
                            super.onNegativeActionClicked(fragment);
                        }
                    };

                    ((SimpleDialog.Builder) builder).message(name + "\n" + number)
                            .title("Refer")
                            .positiveAction("OK")
                            .negativeAction("CANCEL");
                    DialogFragment fragment = DialogFragment.newInstance(builder);
                    fragment.show(getSupportFragmentManager(), null);
                 }
                break;
        }
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
        cursor.addRow(new Object[]{key++, referal, status});
        cursor.addRow(new Object[]{key++, referal, status});

        SimpleCursorAdapter data =
                new SimpleCursorAdapter(this,
                        R.layout.viewlist_two_items,
                        cursor,
                        columns,
                        layouts);

        list.setAdapter(data);

    }
}
