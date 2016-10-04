package fypnctucs.bcar.notification;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fypnctucs.bcar.R;

/**
 * Created by kamfu.wong on 30/9/2016.
 */

public class notificationAdapter extends BaseAdapter {

    private ArrayList<notificationItem> notificationList;
    private Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setList(ArrayList<notificationItem> notificationList) {
        this.notificationList = notificationList;
    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public notificationItem getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(activity.getApplicationContext(), R.layout.item_notification_list, null);
            new notificationAdapter.ViewHolder(convertView);
        }

        notificationAdapter.ViewHolder holder = (notificationAdapter.ViewHolder) convertView.getTag();
        notificationItem item = getItem(position);
        holder.msg.setText(item.getMsg());
        holder.date.setText(item.getDate());
        holder.icon.setImageResource(item.getIcon());
        return convertView;
    }

    class ViewHolder {
        ImageView icon;
        TextView msg;
        TextView date;

        public ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.notification_icon);
            msg = (TextView) view.findViewById(R.id.msg);
            date = (TextView) view.findViewById(R.id.date);
            view.setTag(this);
        }
    }

}
