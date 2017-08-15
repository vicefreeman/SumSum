package sumsum.gates.vice.hiday;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import sumsum.gates.vice.hiday.Modules.Gate;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    SharedPreferences sharedPreferences;
    OnRadiusUpdateListener listener;
    AlertDialog n;

    Button btnBack;


    public AddGate() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnRadiusUpdateListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_gate, container, false);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();
        sharedPreferences = activity.getSharedPreferences("shred" ,Context.MODE_PRIVATE);
        btnBack = (Button) view.findViewById(R.id.backBtn);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        etRadius.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>0) {
                    listener.onRadiusUpdated(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

        String lat = sharedPreferences.getString("lat", "no data");
        String lng = sharedPreferences.getString("lng", "no data");
        String gatePhone = etPhoneNum.getText().toString();
        String gateName = etGateName.getText().toString();
        String radius = etRadius.getText().toString();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userGatesList = FirebaseDatabase.getInstance().getReference("UserGatesList").child(uid).child(gateName);
        if (gateName.equals("")){
            Toast.makeText(getContext(), "Gate name is missing", Toast.LENGTH_LONG).show();
        }else if (gatePhone.equals("") || gatePhone.length() != 10){
            Toast.makeText(getContext(), "Gate phone is incorrect", Toast.LENGTH_LONG).show();
        }else if(radius.equals("")){
            Toast.makeText(getContext(), "Gate radius is missing", Toast.LENGTH_LONG).show();
        }else if (lat.equals("no data") || lng.equals("no data")){
            Toast.makeText(getContext(), "Marker is missing in the map", Toast.LENGTH_LONG).show();
        } else {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString(gateName,gatePhone);
            Gate g = new Gate(Double.valueOf(lat), Double.valueOf(lng), Integer.valueOf(radius), gatePhone, gateName, gateName);
            userGatesList.setValue(g);
            etRadius.setText("");
            etGateName.setText("");
            etPhoneNum.setText("");
            n = new AlertDialog.Builder(getContext())
                    .setTitle("Gate added")
                    .setMessage("You have added: " + gateName + " to your list ")
                    .setPositiveButton("+ Add another", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            n.dismiss();
                        }
                    }).setCancelable(false)
                    .setNegativeButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            getContext().startActivity(intent);


                        }
                    })
                    .create();
            n.show();
        }
    }


    public interface OnRadiusUpdateListener{
        void onRadiusUpdated(String radius);
    }

}
