package fypnctucs.bcar.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import fypnctucs.bcar.DataFormat;
import fypnctucs.bcar.MainActivity;
import fypnctucs.bcar.Map_Controller;
import fypnctucs.bcar.R;
import fypnctucs.bcar.device.BleDevice;
import fypnctucs.bcar.device.BleDeviceDAO;
import fypnctucs.bcar.device.DeviceListAdapter;
import fypnctucs.bcar.history.History;
import fypnctucs.bcar.history.HistoryDAO;
import fypnctucs.bcar.history.HistoryListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class list_fragment extends Fragment {

    public list_fragment() {
        // Required empty public constructor
    }

    private ArrayList<BleDevice> devicesList;
    private List<History> historyList;

    private DeviceListAdapter devicesAdapter;
    private ArrayAdapter<String> BLElistAdapter;
    private HistoryListAdapter historyListAdapter;
    public ArrayList<BluetoothDevice> foundDevicesArray;

    private View layout;

    private SwipeMenuListView devicesListView;
    private ListView historyListView;
    private ListView ScanListView;

    private View ScanDialogView;
    private View SettingDialogView;
    private View HistoryDialogView;
    private View MapDialogView;

    private AlertDialog HistoryDialog;
    private AlertDialog SettingDialog;
    private AlertDialog ScanDialog;
    private AlertDialog MapDialog;

    private BleDeviceDAO bleDeviceDAO;
    private HistoryDAO historyDAO;

    private int devicePosition = -1;

    private Map_Controller map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout =  inflater.inflate(R.layout.fragment_list, container, false);

        scan_dialog_init();
        History_dialog_init();
        map_dialog_init();

        list_init();

        ((FloatingActionButton) layout.findViewById(R.id.fab)).setOnClickListener(fab_setOnClickListener);

        ((MainActivity)getActivity()).mBLEClient.SetScanCallback(scanCallback);

        return layout;
    }

    // list init
    private void list_init() {
        devicesAdapter = new DeviceListAdapter();
        devicesAdapter.setActivity(getActivity());

        historyDAO = new HistoryDAO(getActivity());

        bleDeviceDAO = new BleDeviceDAO(this, devicesAdapter);
        devicesList = bleDeviceDAO.getAll();
        devicesAdapter.setList(devicesList);

        devicesListView = (SwipeMenuListView)layout.findViewById(R.id.listView);
        devicesListView.setAdapter(devicesAdapter);
        devicesListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        devicesListView.setMenuCreator(devicesListCreator);
        devicesListView.setOnItemClickListener(deviceItemOnClickListener);

        devicesListView.setOnMenuItemClickListener(devicesListView_OnMenuItemClickListener);
    }

    // history insert
    public void History_insert(History history) {
        historyDAO.insert(history);
        refreshHoistoryAdapter();
    }

    // history dialog
    private void History_dialog_init() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        HistoryDialogView = inflater.inflate(R.layout.dialog_history, null);

        historyListAdapter = new HistoryListAdapter();
        historyListAdapter.setList(new ArrayList<History>());
        historyListAdapter.setActivity(getActivity());

        historyListView = (ListView)HistoryDialogView.findViewById(R.id.history_list);
        historyListView.setAdapter(historyListAdapter);
        historyListView.setOnItemClickListener(historyOnClickListener);

        HistoryDialog = new AlertDialog.Builder(getActivity(),android.R.style.Animation_Translucent)
                .setCancelable(false)
                .setView(HistoryDialogView).create();
        HistoryDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Translucent;

        HistoryDialogView.findViewById(R.id.history_backButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryDialog.cancel();
            }
        });
    }

    private final AdapterView.OnItemClickListener deviceItemOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            devicePosition = position;

            historyList = historyDAO.getByBtaddress("'"+devicesList.get(position).getDevice().getAddress()+"'");
            historyListAdapter.setList(historyList);

            refreshHoistoryAdapter();
            HistoryDialog.show();
        }
    };

    // map dialog
    private void map_dialog_init() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        MapDialogView = inflater.inflate(R.layout.dialog_map, null);
        MapDialog = new AlertDialog.Builder(getActivity(),android.R.style.Animation_Translucent)
                .setCancelable(false)
                .setView(MapDialogView).create();
        MapDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Translucent;

        map = new Map_Controller(getActivity(), (MapView)MapDialogView.findViewById(R.id.history_map));

        FloatingActionButton fab = (FloatingActionButton) MapDialogView.findViewById(R.id.mylocation);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).getCurrentLocation(0, 0, MapOneTimeLocationListener);
            }
        });

        MapDialogView.findViewById(R.id.map_backButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MapDialog.cancel();
            }
        });
    }

    private final AdapterView.OnItemClickListener historyOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            map.clearMarkers();

            map.InsertMarker(new LatLng(historyList.get(position).getLat(), historyList.get(position).getLng()),
                    devicesList.get(devicePosition).getName() + " 停在這裡",
                    DataFormat.CONNECT_DEVICE_ICON[devicesList.get(devicePosition).getType()],
                    true);

            MapDialog.show();
        }
    };

    // LocationListener
    private LocationListener MapOneTimeLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                map.showCurrentLocation(location);
                ((MainActivity)getActivity()).StopLocationListener(this);
            } else {
                Log.d("onLocationChanged", "Location is null");
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    // scan dialog
    private void scan_dialog_init() {
        foundDevicesArray = new ArrayList<BluetoothDevice>();

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        ScanDialogView = inflater.inflate(R.layout.dialog_scan_list, null);
        BLElistAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        ScanListView = (ListView)ScanDialogView.findViewById(R.id.ScanList);
        ScanListView.setAdapter(BLElistAdapter);

        ScanDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setView(ScanDialogView).create();

        ScanDialogView.findViewById(R.id.scan_backButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).mBLEClient.BLEScan(false);
                ScanDialog.cancel();
            }
        });

        ScanListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice dev = foundDevicesArray.get(position);

                BleDevice newDevice = getDeviceByBTD(dev);
                if (newDevice == null) {
                    newDevice = new BleDevice(getActivity(), list_fragment.this, devicesAdapter, dev.getName(), DataFormat.OTHER, dev, false);

                    newDevice.connect((MainActivity)getActivity());
                    devicesList.add(newDevice);
                    bleDeviceDAO.insert(newDevice);

                    refreshDevicesAdapter();
                } else {
                    if (!newDevice.isConnecting() && !newDevice.isConnected()) {
                        newDevice.connect((MainActivity)getActivity());
                    }
                }

                ((MainActivity)getActivity()).mBLEClient.BLEScan(false);
                ScanDialog.dismiss();
            }
        });
    }

    private final OnClickListener fab_setOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            foundDevicesArray.clear();
            ((MainActivity)getActivity()).mBLEClient.BLEScan(true);

            BLElistAdapter.clear();

            ScanDialog.show();
        }
    };

    // setting dialog
    private OnMenuItemClickListener devicesListView_OnMenuItemClickListener = new OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
            final BleDevice device = devicesList.get(position);

            switch (index) {
                case 0:
                    //edit
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    SettingDialogView = inflater.inflate(R.layout.dialog_device_setting, null);

                    String buttonStr;
                    ((EditText)SettingDialogView.findViewById(R.id.deviceName)).setText(device.getName());
                    ((Spinner)SettingDialogView.findViewById(R.id.deviceType)).setSelection(device.getType());
                    ((TextView)SettingDialogView.findViewById(R.id.bluetoothAddress)).setText(device.getDevice().getAddress());
                    if (device.isConnected()) {
                        ((TextView)SettingDialogView.findViewById(R.id.connectStatus)).setText("已連線");
                        buttonStr = "中斷連線";
                    } else {
                        ((TextView)SettingDialogView.findViewById(R.id.connectStatus)).setText("連接已中斷");
                        buttonStr = "連線";
                    }

                    SettingDialog = new AlertDialog.Builder(getActivity())
                            .setTitle("設定")
                            .setIcon(DataFormat.CONNECT_DEVICE_ICON[device.getType()])
                            .setCancelable(false)
                            .setView(SettingDialogView)
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    device.setName(((TextView)SettingDialogView.findViewById(R.id.deviceName)).getText()+"");
                                    device.setType(((Spinner)SettingDialogView.findViewById(R.id.deviceType)).getSelectedItemPosition());

                                    bleDeviceDAO.update(device);

                                    refreshDevicesAdapter();
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
                                    if (device.isConnected())
                                        device.disconnect();
                                    else
                                        device.connect((MainActivity)list_fragment.this.getActivity());
                                }
                            }).show();

                    break;
                case 1:
                    // delete
                    new AlertDialog.Builder(list_fragment.this.getActivity())
                            .setTitle("確認刪除?")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    device.disconnect();
                                    bleDeviceDAO.delete(device.getId());
                                    devicesList.remove(position);

                                    refreshDevicesAdapter();
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

    private final SwipeMenuCreator devicesListCreator = new SwipeMenuCreator() {

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

    private BleDevice getDeviceByBTD(BluetoothDevice dev) {
        for (int i=0; i<devicesList.size(); i++)
            if (devicesList.get(i).getDevice() != null && dev.getAddress().equals(devicesList.get(i).getDevice().getAddress()))
                return devicesList.get(i);
        return null;
    }

    // BLE Scan callback
    private final ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            final BluetoothDevice device = result.getDevice();
            if (!foundDevicesArray.contains(device)) {
                foundDevicesArray.add(device);
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

    // List Fragment Handler
    public void status(String msg) {
        mHandler.obtainMessage(TOAST_STATUS, msg).sendToTarget();
    }
    public void refreshDevicesAdapter() {
        mHandler.obtainMessage(REFRESH_DEVICES_ADAPTER, null).sendToTarget();
    }
    public void refreshHoistoryAdapter() {
        mHandler.obtainMessage(REFRESH_HISTORY_ADAPTER, null).sendToTarget();
    }
    public void getLocation(LocationListener locationListener) {
        mHandler.obtainMessage(BLEDEVICE_REFRESH_LOCATION, locationListener).sendToTarget();
    }
    final static int TOAST_STATUS = 103;
    final static int REFRESH_DEVICES_ADAPTER = 104;
    final static int BLEDEVICE_REFRESH_LOCATION = 105;
    final static int REFRESH_HISTORY_ADAPTER = 106;
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TOAST_STATUS:
                    Toast.makeText((MainActivity)getActivity(), (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case REFRESH_DEVICES_ADAPTER:
                    devicesAdapter.notifyDataSetChanged();
                    break;
                case REFRESH_HISTORY_ADAPTER:
                    historyListAdapter.notifyDataSetChanged();
                    break;
                case BLEDEVICE_REFRESH_LOCATION:
                    ((MainActivity)getActivity()).getCurrentLocation(0, 0, (LocationListener) msg.obj);
                    break;
            }
        }
    };

}
