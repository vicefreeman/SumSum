package com.android.pribo.vice.sumsum;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.pribo.vice.sumsum.Modules.Gate;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;



public class UserGateListFragment extends Fragment {

    @BindView(R.id.rvUserGateList)
    RecyclerView rvUserLists;
    Unbinder unbinder;
    String s;


    public UserGateListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_gate_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null){
            Log.e("SumSum", "Null user");
            return view;
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserGatesList").child(currentUser.getUid()); //TODO: Handle nulls

        rvUserLists.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUserLists.setAdapter(new UserGateListFragment.UserGateListAdapter(ref, this));

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




        public UserGateListAdapter(Query query, Fragment fragment) {
            super(Gate.class, R.layout.user_gate_list_item, UserGateListFragment.UserGateListAdapter.UserGateListViewHolder.class, query);
            this.fragment = fragment;
        }


        @Override
        public UserGateListFragment.UserGateListAdapter.UserGateListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            UserGateListFragment.UserGateListAdapter.UserGateListViewHolder vh =  super.onCreateViewHolder(parent, viewType);
            vh.userGateListFragment = fragment;
            context = parent.getContext();
            return vh;
        }

        @Override
        protected void populateViewHolder(final UserGateListFragment.UserGateListAdapter
                .UserGateListViewHolder viewHolder, final Gate model, int position) {
            viewHolder.tvUserGateName.setText(model.getName());
            viewHolder.btnDeleteGate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gateName = viewHolder.tvUserGateName.getText().toString();
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference userList = FirebaseDatabase.getInstance().getReference("UserGatesList").child(uid).child(gateName);
                   userList.removeValue();
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
