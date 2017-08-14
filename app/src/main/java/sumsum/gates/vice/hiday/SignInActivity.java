package sumsum.gates.vice.hiday;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

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
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnSignIn)
    public void onEnterClicked() {

        if (etUserName.getText().toString().isEmpty()) {
            etUserName.setError("You didn't enter your name");
        } else if (etUserName.getText().toString().length() < 3) {
            etUserName.setError("Name Should contain et least 3 letters");
        } else {
            progressBar.setVisibility(View.VISIBLE);
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
