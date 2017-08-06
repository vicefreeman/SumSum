package com.android.pribo.vice.sumsum;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends AppCompatActivity {


    @BindView(R.id.btnSignIn)
    Button btnSignIn;
    @BindView(R.id.etUserName)
    EditText etUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnSignIn)
    public void onEnterClicked() {
        if (etUserName.getText().equals("")) {
            Toast.makeText(this, "User name cant be empty", Toast.LENGTH_LONG).show();
        } else {
            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(this,
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //FirebaseUser us = FirebaseAuth.getInstance().getCurrentUser();
                                FirebaseUser user = task.getResult().getUser();
                                //ref the database
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                                //setValue
                                ref.setValue(etUserName.getText().toString());

                                Intent intent = new Intent(SignInActivity.this /*context*/, MainActivity.class);
                                //new Task (No Activities in the new task)
                                //clear task  (deletes the former stack of activities)
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });
        }
    }
}
