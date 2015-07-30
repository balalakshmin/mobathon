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
    private String[] referals;
    private String[] status;
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
