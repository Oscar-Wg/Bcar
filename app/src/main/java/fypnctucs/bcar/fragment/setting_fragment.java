package fypnctucs.bcar.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import fypnctucs.bcar.MainActivity;
import fypnctucs.bcar.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class setting_fragment extends Fragment {


    public setting_fragment() {
        // Required empty public constructor
    }

    private View layout;
    private Switch BluetoothSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout =  inflater.inflate(R.layout.fragment_setting, container, false);

        BluetoothSwitch = (Switch)layout.findViewById(R.id.bluetoothSwitch);

        BluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ((MainActivity)getActivity()).mBLEClient.enable();
                    ((MainActivity)getActivity()).setWarming(false);
                } else {
                    ((MainActivity)getActivity()).mBLEClient.disable();
                    ((MainActivity)getActivity()).setWarming(true);
                }
            }
        });


        return layout;
    }

    @Override
    public void onResume() {
        BluetoothSwitch.setChecked(((MainActivity)getActivity()).mBLEClient.isEnabled());
        super.onResume();
    }

}
