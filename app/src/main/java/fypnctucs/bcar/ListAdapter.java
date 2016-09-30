package fypnctucs.bcar;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kamfu.wong on 29/9/2016.
 */

public class ListAdapter extends BaseAdapter {

    private ArrayList<bleKeyRing> SaveDevicesList;
    private Activity activity;

    public void setAvtivity(Activity activity) {
        this.activity = activity;
    }

    public void setList(ArrayList<bleKeyRing> SaveDevicesList) {
        this.SaveDevicesList = SaveDevicesList;
    }

    @Override
    public int getCount() {
        return SaveDevicesList.size();
    }

    @Override
    public bleKeyRing getItem(int position) {
        return SaveDevicesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(activity.getApplicationContext(), R.layout.item_list_app, null);
            new ListAdapter.ViewHolder(convertView);
        }

        ViewHolder holder = (ListAdapter.ViewHolder) convertView.getTag();
        bleKeyRing item = getItem(position);
        holder.name.setText(item.getName());
        if (item.isConnected())
            holder.icon.setImageResource(dataFormat.CONNECT_DEVICE_ICON[item.getType()]);
        else
            holder.icon.setImageResource(dataFormat.DISCONNECT_DEVICE_ICON[item.getType()]);
        return convertView;
    }

    class ViewHolder {
        ImageView icon;
        TextView name;

        public ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.iv_icon);
            name = (TextView) view.findViewById(R.id.tv_name);
            view.setTag(this);
        }
    }

}