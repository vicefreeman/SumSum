package com.android.pribo.vice.sumsum;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddGate extends Fragment {


    Unbinder unbinder;
    @BindView(R.id.etPhoneNum)
    EditText etPhoneNum;
    @BindView(R.id.etRadius)
    EditText etRadius;
    @BindView(R.id.btnAdd)
    Button btnAdd;
    @BindView(R.id.etGateName)
    EditText etGateName;
    String lat;
    String lng;
    String phone;
    String name;

    public AddGate() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_gate, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @OnClick(R.id.btnAdd)
    public void onViewClicked() {

    }
}
