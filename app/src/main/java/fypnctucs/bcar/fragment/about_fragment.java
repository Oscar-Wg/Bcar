package fypnctucs.bcar.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fypnctucs.bcar.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class about_fragment extends Fragment {

    public about_fragment() {
        // Required empty public constructor
    }

    private View layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout =  inflater.inflate(R.layout.fragment_about, container, false);

        return layout;
    }

}
