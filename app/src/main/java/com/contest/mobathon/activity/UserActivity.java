package com.contest.mobathon.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.contest.mobathon.R;
import com.contest.mobathon.dao.ReferalDAO;
import com.github.mikephil.charting.data.Entry;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.FloatingActionButton;

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
    private String[] referals;
    private String[] status;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Your referals");

        list = (ListView) findViewById(R.id.referals_list2);

        new TableData(this).execute();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(it, PICK_CONTACT);
            }
        });


    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT):
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
                    Dialog.Builder builder = new SimpleDialog.Builder(R.style.Material_App_Dialog_Simple_Light) {
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
                            .title("Send Referal")
                            .positiveAction("OK")
                            .negativeAction("CANCEL");
                    DialogFragment fragment = DialogFragment.newInstance(builder);
                    fragment.show(getSupportFragmentManager(), null);
                }
                break;
        }
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
                    List<ReferalDAO> referal = objectMapper.readValue(result, new TypeReference<List<ReferalDAO>>() {
                    });

                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    String sessionName = sessionManager.getUserDetails().get("name");
                    referals = new String[referal.size()];
                    status = new String[referal.size()];
                    for(int i=0,k=0; i<referal.size(); i++) {
                        if(sessionName.equals(referal.get(i).getName())) {
                            while(k<referal.get(i).getReferals().size()) {
                                referals[k] = referal
                                        .get(i)
                                        .getReferals()
                                        .get(k)
                                        .get("name");
                                status[k] = referal.get(i).getReferals().get(k).get("status");
                                k++;
                            }
                        }
                    }
                    initListView();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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

    }
    private void initListView() {

        final String[] matrix = {"_id", "name", "value"};
        final String[] columns = {"name", "value"};
        final int[] layouts = {android.R.id.text1, android.R.id.text2};

        MatrixCursor cursor = new MatrixCursor(matrix);
        int key = 0;
        for(int i=0; i<referals.length; i++) {
            cursor.addRow(new Object[]{key++, referals[i], status[i]});
        }

        SimpleCursorAdapter data =
                new SimpleCursorAdapter(this,
                        R.layout.viewlist_two_items,
                        cursor,
                        columns,
                        layouts);

        list.setAdapter(data);

    }

}
