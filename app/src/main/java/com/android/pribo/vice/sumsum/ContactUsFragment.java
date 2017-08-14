package com.android.pribo.vice.sumsum;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class ContactUsFragment extends Fragment {


    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etPublisher)
    EditText etPublisher;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.linearLayout2)
    LinearLayout linearLayout2;
    Button btnSend , btnCancel;

    Unbinder unbinder;

    public ContactUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        unbinder = ButterKnife.bind(this, view);

        btnSend = (Button) view.findViewById(R.id.btnSend);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity() , MainActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    protected void sendEmail() {
        Log.d("Send email", "");

        String[] TO = {"vicepribo@gmail.com"};
        String[] CC = {"xyz@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        //emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, etTitle.getText().toString());
        emailIntent.putExtra(Intent.EXTRA_TEXT, etContent.getText().toString());

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            Log.d("Finished Email", " ");
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(getActivity(),
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public EditText getEtTitle() {
        return etTitle;
    }

    public void setEtTitle(EditText etTitle) {
        this.etTitle = etTitle;
    }

    public EditText getEtPhone() {
        return etPhone;
    }

    public void setEtPhone(EditText etPhone) {
        this.etPhone = etPhone;
    }

    public EditText getEtPublisher() {
        return etPublisher;
    }

    public void setEtPublisher(EditText etPublisher) {
        this.etPublisher = etPublisher;
    }

    public EditText getEtEmail() {
        return etEmail;
    }

    public void setEtEmail(EditText etEmail) {
        this.etEmail = etEmail;
    }

    public EditText getEtContent() {
        return etContent;
    }

    public void setEtContent(EditText etContent) {
        this.etContent = etContent;
    }

    @Override
    public String toString() {
        return "ContactUsFragment{" +
                "etTitle=" + etTitle +
                ", etPhone=" + etPhone +
                ", etPublisher=" + etPublisher +
                ", etEmail=" + etEmail +
                ", etContent=" + etContent +
                '}';
    }
}
