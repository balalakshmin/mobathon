package com.contest.mobathon.activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.contest.mobathon.R;
import com.rey.material.widget.Button;


public class LoanFragment extends Fragment {

    public LoanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_loan, container, false);
        final com.rey.material.widget.EditText month,loan, interest,salary,emi;
        month = (com.rey.material.widget.EditText) rootView.findViewById(R.id.month) ;
        loan = (com.rey.material.widget.EditText) rootView.findViewById(R.id.loanamt);
        interest = (com.rey.material.widget.EditText) rootView.findViewById(R.id.interest);
        salary = (com.rey.material.widget.EditText) rootView.findViewById(R.id.salary);
        emi = (com.rey.material.widget.EditText) rootView.findViewById(R.id.totemi);
        Button button = (Button) rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String m,l,i,s,e;
                m= month.getText().toString();
                l= loan.getText().toString();
                i= interest.getText().toString();
                s= salary.getText().toString();
                e= emi.getText().toString();
                int mi,li,ii,ei,loan,si,simpleInterest;
                mi = Integer.parseInt(m);
                li = Integer.parseInt(l);
                ii = Integer.parseInt(i);
                si = Integer.parseInt(s);
                ei = Integer.parseInt(e);
                loan = (si - ei)*mi;
                
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
