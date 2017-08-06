package com.android.pribo.vice.sumsum;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.pribo.vice.sumsum.Modules.Gate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

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
    Activity activity;


    public AddGate() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_gate, container, false);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();
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
        SharedPreferences sharedPreferences = activity.getSharedPreferences("data", Context.MODE_PRIVATE);
        String lan = sharedPreferences.getString("lat", "");
        String lng = sharedPreferences.getString("lng", "");
        String gatePhone = etPhoneNum.getText().toString();
        String gateName = etGateName.getText().toString();
        String radius = etRadius.getText().toString();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userGatesList = FirebaseDatabase.getInstance().getReference("UserGatesList").child(uid).child(gateName);
        if (gateName.equals("") || gatePhone.equals("") || radius.equals("")) {
            Toast.makeText(getContext(), "gate data is missing", Toast.LENGTH_LONG).show();
        } else {
            Gate g = new Gate(Double.valueOf(lan), Double.valueOf(lng), Integer.valueOf(radius), gatePhone, gateName, gateName);
            userGatesList.setValue(g);
            etRadius.setText("");
            etGateName.setText("");
            etPhoneNum.setText("");
        }
    }

    public interface OnBtnClickedAddPrivetGateListener {
        public void OnBtnClickedAddPrivetGate(List<String> data);
    }

}
