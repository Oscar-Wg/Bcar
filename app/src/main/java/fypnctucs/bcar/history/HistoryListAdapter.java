package fypnctucs.bcar.history;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import fypnctucs.bcar.MainActivity;
import fypnctucs.bcar.R;

/**
 * Created by kamfu.wong on 4/10/2016.
 */

public class HistoryListAdapter extends BaseAdapter {
    private Activity activity;
    private List<History> historyList;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
    public void setList(List<History> historyList) {
        this.historyList = historyList;
    }

    @Override
    public int getCount() {
        return historyList.size();
    }

    @Override
    public History getItem(int position) {
        return historyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(activity.getApplicationContext(), R.layout.item_history_list, null);
            new HistoryListAdapter.ViewHolder(convertView);
        }

        HistoryListAdapter.ViewHolder holder = (HistoryListAdapter.ViewHolder) convertView.getTag();
        History item = getItem(position);

        if (item.getAddress().equals("...") && !item.busy) {
            item.busy = true;
            ((MainActivity)activity).findAddress(item);
        }
        holder.address.setText(item.getAddress());
        holder.date.setText(item.getDate());
        holder.latlng.setText("經度: " + item.getLat() + "\n緯度: " + item.getLng());

        return convertView;
    }

    class ViewHolder {
        TextView address;
        TextView date;
        TextView latlng;

        public ViewHolder(View view) {
            address = (TextView) view.findViewById(R.id.address);
            date = (TextView) view.findViewById(R.id.date);
            latlng = (TextView) view.findViewById(R.id.latlng);
            view.setTag(this);
        }
    }
}
