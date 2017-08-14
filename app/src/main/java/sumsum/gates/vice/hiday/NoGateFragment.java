package sumsum.gates.vice.hiday;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoGateFragment extends Fragment {


    @BindView(R.id.btnAddGatefromList)
    Button btnAddGatefromList;
    Unbinder unbinder;

    public NoGateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_no_gate, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnAddGatefromList)
    public void onViewClicked() {
        getActivity().getSupportFragmentManager().
                beginTransaction().
                replace(R.id.mainContainer, new GateListFragment()).
                addToBackStack("GateListFragment").
                commit();
    }
}
