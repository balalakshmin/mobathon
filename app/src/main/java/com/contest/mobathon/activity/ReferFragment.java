package com.contest.mobathon.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.contest.mobathon.R;
import com.contest.mobathon.dao.UserLoginDao;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class ReferFragment extends Fragment {

    private EditText usernameField,passwordField;
    private TextView status,role;
    private Button loginButton;
    SessionManager session;
    public ReferFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.login_activity, container, false);
        session = new SessionManager(getActivity().getApplicationContext());
        usernameField = (EditText)view.findViewById(R.id.editText1);
        passwordField = (EditText)view.findViewById(R.id.editText2);
        loginButton = (Button) view.findViewById(R.id.button1);
        Toast.makeText(getActivity().getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
        status = (TextView)view.findViewById(R.id.textView6);
        role = (TextView)view.findViewById(R.id.textView7);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();
                new SigninActivity(getActivity(),status,role).execute(username, password);
            }
        });

        return view;
    }

    public void handleResponse(UserLoginDao userLoginDao){
        if(userLoginDao.getError()) {
            Toast.makeText(getActivity(),userLoginDao.getError_msg(),Toast.LENGTH_LONG).show();
        }
        else {
            session.createLoginSession(userLoginDao.getUid(),
                    userLoginDao.getUser().get("name"), userLoginDao.getUser().get("email"));
            if(userLoginDao.getUser().get("admin").equals("1")) {
                Toast.makeText(getActivity(),"admin logged in",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(),AdminActivity.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getActivity(),userLoginDao.getUser().get("name")+" logged in",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(),UserActivity.class);
                startActivity(intent);
            }
        }
    }
    public class SigninActivity  extends AsyncTask<String,Void,String> {
        private TextView statusField,roleField;
        private Context context;

        //flag 0 means get and 1 means post.(By default it is get.)
        public SigninActivity(Context context,TextView statusField,TextView roleField) {
            this.context = context;
            this.statusField = statusField;
            this.roleField = roleField;
        }

        protected void onPreExecute(){

        }

        @Override
        protected String doInBackground(String... arg0) {

            try{
                String username = (String)arg0[0];
                String password = (String)arg0[1];

                String link="http://192.168.43.15/Mobathon/login.php";
                //String link="http://stackoverflow.com";
                String data  = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write( data );
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null)
                {
                    sb.append(line);
                    break;
                }
                return sb.toString();
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result){
            this.statusField.setText("Login Successful");
            ObjectMapper mapper = new ObjectMapper();
            this.roleField.setText(result);
            try {
                UserLoginDao userLoginDao= mapper.readValue(result,UserLoginDao.class);
                System.out.println(userLoginDao);
                handleResponse(userLoginDao);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
