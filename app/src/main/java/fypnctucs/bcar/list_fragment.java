package fypnctucs.bcar;


import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class list_fragment extends Fragment {


    public list_fragment() {
        // Required empty public constructor
    }

    private ListAdapter SaveDevicesAdapter;
    private ArrayList<bleKeyRing> SaveDevicesList;

    private ArrayAdapter<String> BLElistAdapter;

    private View layout;
    private View ScanDialogView;

    private AlertDialog ScanDialog;

    SwipeMenuListView SaveDevicesListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout =  inflater.inflate(R.layout.list_fragment, container, false);

        BLElistAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);

        SaveDevicesList = new ArrayList<bleKeyRing>();
        SaveDevicesAdapter = new ListAdapter();
        SaveDevicesAdapter.setList(SaveDevicesList);
        SaveDevicesAdapter.setActivity(getActivity());

        SaveDevicesListView= (SwipeMenuListView)layout.findViewById(R.id.listView);
        SaveDevicesListView.setAdapter(SaveDevicesAdapter);
        SaveDevicesListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        SaveDevicesListView.setMenuCreator(SaveDevicesListCreator);

        SaveDevicesListView.setOnMenuItemClickListener(SaveDevicesListView_OnMenuItemClickListener);

        FloatingActionButton fab = (FloatingActionButton) layout.findViewById(R.id.fab);
        fab.setOnClickListener(fab_setOnClickListener);

        ((MainActivity)getActivity()).mBLEClient.SetScanCallback(scanCallback);

        //SaveDevicesList.add(new bleKeyRing(getActivity(), this, SaveDevicesAdapter, "915-LPZ", dataFormat.SCOOTER, null, false));
        //SaveDevicesList.add(new bleKeyRing(getActivity(), this, SaveDevicesAdapter, "AAC-6364", dataFormat.CAR, null, false));
        //SaveDevicesList.add(new bleKeyRing(getActivity(), this, SaveDevicesAdapter, "以上都是假item", dataFormat.MOTORCYCLE, null, false));

        return layout;
    }

    @Override
    public void onResume() {
        Log.d("DEBUG", "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d("DEBUG", "onPause");
        super.onPause();
    }


    @Override
    public void onStop() {
        Log.d("DEBUG", "onStop");
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        Log.d("DEBUG", "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d("DEBUG", "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d("DEBUG", "onDetach");
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d("DEBUG", "onSaveInstanceState");
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        Log.d("DEBUG", "onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
    }

    // BLE Scan callback
    private final ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            final BluetoothDevice device = result.getDevice();
            if (!((MainActivity)getActivity()).foundDevicesArray.contains(device)) {
                ((MainActivity)getActivity()).foundDevicesArray.add(device);
                BLElistAdapter.add(device.getAddress() + " | " + device.getName());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }

    };

    private final OnClickListener fab_setOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            ((MainActivity)getActivity()).mBLEClient.BLEScan(true);

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            ScanDialogView = inflater.inflate(R.layout.scan_list_dialog, null);

            BLElistAdapter.clear();
            ListView ScanList = (ListView)ScanDialogView.findViewById(R.id.ScanList);
            ScanList.setAdapter(BLElistAdapter);

            ScanList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BluetoothDevice dev = ((MainActivity)getActivity()).foundDevicesArray.get(position);

                    bleKeyRing newKeyRing = getKeyRingByDevice(dev);
                    if (newKeyRing == null) {
                        newKeyRing = new bleKeyRing(getActivity(), list_fragment.this, SaveDevicesAdapter, dev.getAddress(), dataFormat.OTHER, dev, false);

                        newKeyRing.connect((MainActivity)getActivity());
                        SaveDevicesList.add(newKeyRing);
                        SaveDevicesAdapter.notifyDataSetChanged();
                    } else {
                        if (!newKeyRing.isConnecting() && !newKeyRing.isConnected()) {
                            newKeyRing.connect((MainActivity)getActivity());
                        }
                    }

                    ((MainActivity)getActivity()).mBLEClient.BLEScan(false);
                    ScanDialog.dismiss();
                }
            });

            ScanDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("正在搜尋藍芽設備...")
                    .setIcon(R.drawable.ic_ble)
                    .setCancelable(false)
                    .setView(ScanDialogView)
                    .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((MainActivity)getActivity()).mBLEClient.BLEScan(false);
                        }
                    }).show();
        }
    };

    private View SettingDialogView;
    private  AlertDialog SettingDialog;

    private OnMenuItemClickListener SaveDevicesListView_OnMenuItemClickListener = new OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
            final bleKeyRing keyRing = SaveDevicesList.get(position);

            switch (index) {
                case 0:
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    SettingDialogView = inflater.inflate(R.layout.keyring_setting_dialog, null);

                    String buttonStr;
                    ((EditText)SettingDialogView.findViewById(R.id.KeyRingName)).setText(keyRing.getName());
                    ((Spinner)SettingDialogView.findViewById(R.id.KeyRingType)).setSelection(keyRing.getType());
                    ((TextView)SettingDialogView.findViewById(R.id.bluetoothAddress)).setText(keyRing.getDevice().getAddress());
                    if (keyRing.isConnected()) {
                        ((TextView)SettingDialogView.findViewById(R.id.connectStatus)).setText("已連線");
                        buttonStr = "中斷連線";
                    } else {
                        ((TextView)SettingDialogView.findViewById(R.id.connectStatus)).setText("連接已中斷");
                        buttonStr = "連線";
                    }

                    SettingDialog = new AlertDialog.Builder(getActivity())
                            .setTitle("設定")
                            .setIcon(dataFormat.CONNECT_DEVICE_ICON[keyRing.getType()])
                            .setCancelable(false)
                            .setView(SettingDialogView)
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    keyRing.setName(((TextView)SettingDialogView.findViewById(R.id.KeyRingName)).getText()+"");
                                    keyRing.setType(((Spinner)SettingDialogView.findViewById(R.id.KeyRingType)).getSelectedItemPosition());
                                    refrestAdapter();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setNeutralButton(buttonStr, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (keyRing.isConnected())
                                        keyRing.disconnect();
                                    else
                                        keyRing.connect((MainActivity)list_fragment.this.getActivity());
                                }
                            }).show();

                    break;
                case 1:
                    // delete

                    new AlertDialog.Builder(list_fragment.this.getActivity())
                            .setTitle("確認刪除?")
                            .setPositiveButton("是", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    keyRing.disconnect();
                                    SaveDevicesList.remove(position);
                                    SaveDevicesAdapter.notifyDataSetChanged();
                                }

                            })
                            .setNegativeButton("否", null)
                            .show();


                    break;
            }
            // false : close the menu; true : not close the menu
            return false;
        }
    };

    private final SwipeMenuCreator SaveDevicesListCreator = new SwipeMenuCreator() {

        private int dp2px(int dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        }

        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem editItem = new SwipeMenuItem(getActivity().getApplicationContext());
            editItem.setBackground(new ColorDrawable(Color.LTGRAY));
            editItem.setWidth(dp2px(60));
            editItem.setIcon(R.drawable.ic_swipe_setting);
            menu.addMenuItem(editItem);

            SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xef, 0x6c, 0)));
            deleteItem.setWidth(dp2px(60));
            deleteItem.setIcon(R.drawable.ic_swipe_delete);
            menu.addMenuItem(deleteItem);
        }
    };

    private bleKeyRing getKeyRingByDevice(BluetoothDevice dev) {
        for (int i=0; i<SaveDevicesList.size(); i++)
            if (SaveDevicesList.get(i).getDevice() != null && dev.getAddress().equals(SaveDevicesList.get(i).getDevice().getAddress()))
                return SaveDevicesList.get(i);
        return null;
    }

    // List Fragment Handler
    public void status(String msg) {
        mHandler.obtainMessage(TOAST_STATUS, msg).sendToTarget();
    }
    public void refrestAdapter() {
        mHandler.obtainMessage(REFRESH_ADAPTER, null).sendToTarget();
    }
    protected final static int TOAST_STATUS = 103;
    protected final static int REFRESH_ADAPTER = 104;
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TOAST_STATUS:
                    Toast.makeText((MainActivity)getActivity(), (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case REFRESH_ADAPTER:
                    SaveDevicesAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

}
