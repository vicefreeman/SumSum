package sumsum.gates.vice.hiday;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class GreetingFragment extends Fragment {


    @BindView(R.id.tvUserGreeting)
    TextView tvUserGreeting;
    Unbinder unbinder;
    String userName;

    public GreetingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_greeting, container, false);
        unbinder = ButterKnife.bind(this, view);
        showUserName();
        return view;
    }
    private void showUserName() {


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final DatabaseReference userId = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        userId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userName = dataSnapshot.getValue().toString();
                if (userName != null)
                tvUserGreeting.setText("Welcome " + userName + " !");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
       /* Toast.makeText(getContext(), userName, Toast.LENGTH_SHORT).show();
        tvUserGreeting.setText("Welcome " + userName + "! ");*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.tvUserGreeting)
    public void onViewClicked() {
    }
}
