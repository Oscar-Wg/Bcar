package fypnctucs.bcar;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class notification_fragment extends Fragment {


    public notification_fragment() {
        // Required empty public constructor
    }

    private View layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.notification_fragment, container, false);

        return layout;
    }

}
