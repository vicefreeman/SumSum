package sumsum.gates.vice.hiday;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import sumsum.gates.vice.hiday.Modules.Gate;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;



public class UserGateListFragment extends Fragment {

    @BindView(R.id.rvUserGateList)
    RecyclerView rvUserLists;
    Unbinder unbinder;
    String s;
    ProgressBar progressBar;
    Button btnAddAnother;


    public UserGateListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_gate_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar2);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null){
            Log.e("SumSum", "Null user");
            return view;
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserGatesList").child(currentUser.getUid()); //TODO: Handle nulls

        rvUserLists.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUserLists.setAdapter(new UserGateListFragment.UserGateListAdapter(ref, this));
        btnAddAnother = (Button) view.findViewById(R.id.btnAddAnotherGate);

        btnAddAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new GateListFragment()).commit();

            }
        });
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    //2)FirebaseRecyclerAdapter
    public static class UserGateListAdapter extends FirebaseRecyclerAdapter<Gate, UserGateListAdapter.UserGateListViewHolder> {
        Fragment fragment;
        Context context;
        private String gateName = null;
        AlertDialog dialog , dialog2;
        ArrayList<String> gateData;





        public UserGateListAdapter(Query query, Fragment fragment) {
            super(Gate.class, R.layout.user_gate_list_item, UserGateListFragment.UserGateListAdapter.UserGateListViewHolder.class, query);
            this.fragment = fragment;
        }


        @Override
        public UserGateListFragment.UserGateListAdapter.UserGateListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            UserGateListFragment.UserGateListAdapter.UserGateListViewHolder vh =  super.onCreateViewHolder(parent, viewType);
            vh.userGateListFragment = fragment;
            context = parent.getContext();
            gateData = new ArrayList<>();
            return vh;
        }

        @Override
        protected void populateViewHolder(final UserGateListFragment.UserGateListAdapter
                .UserGateListViewHolder viewHolder, final Gate model, final int position) {
            viewHolder.tvUserGateName.setText(model.getName());
            viewHolder.btnDeleteGate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    gateName = viewHolder.tvUserGateName.getText().toString();
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    final DatabaseReference userList = FirebaseDatabase.getInstance().getReference("UserGatesList").child(uid);
                    userList.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                if (postSnapshot.getKey().equals(gateName)) {
                                    String lang = postSnapshot.child("lang").getValue().toString();
                                    String lat = postSnapshot.child("lat").getValue().toString();
                                    String phoneNumber = postSnapshot.child("phone").getValue().toString();
                                    String distance = postSnapshot.child("distance").getValue().toString();
                                    gateData.add(lang);
                                    gateData.add(lat);
                                    gateData.add(gateName);
                                    gateData.add(phoneNumber);
                                    gateData.add(distance);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    dialog = new AlertDialog.Builder(context)
                            .setTitle("Edit Gate")
                            .setMessage("Would you like to edit " + gateName + " ?")
                            .setPositiveButton("Edit gate", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                    dialog.dismiss();

                                    Intent intent = new Intent(context, MapsActivity.class);
                                    intent.putExtra("gateData", gateData);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(intent);

                                }
                            }).setCancelable(true).setIcon(R.drawable.blackminilogo)
                            .setNegativeButton("Remove this gate ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.dismiss();

                                    dialog2 = new AlertDialog.Builder(context).setTitle("DELETE GATE")
                                            .setMessage("You are about to delete this gate from your list. Are you sure?")
                                            .setPositiveButton("Yes. Delete it", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    userList.child(gateName).removeValue();

                                                    Intent intent = new Intent(context, MainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    context.startActivity(intent);
                                                }
                                            }).setCancelable(false).setIcon(R.drawable.ic_alert_attention)
                                            .setNegativeButton("No. I changed my mind", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog2.dismiss();
                                                }
                                            }).show();

                                }
                            })
                            .create();
                    dialog.show();
                }
            });
            viewHolder.model = model;


        }

        //1)ViewHolder
        public static class UserGateListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


            //Properties:
            ImageView ivUserGateProfile;
            TextView tvUserGateName;
            Button btnDeleteGate;
            Fragment userGateListFragment;
            Gate model;



            //Constructor:
            public UserGateListViewHolder(View itemView) {
                super(itemView);
                ivUserGateProfile = (ImageView) this.itemView.findViewById(R.id.ivUserGateProfile);
                tvUserGateName = (TextView) this.itemView.findViewById(R.id.tvUserGateName);
                btnDeleteGate = (Button) this.itemView.findViewById(R.id.btnDeleteGate);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (v == btnDeleteGate) {
                    try {
                        int adapterPosition = getAdapterPosition();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
