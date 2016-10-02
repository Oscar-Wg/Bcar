package fypnctucs.bcar;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.*;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class notification_fragment extends Fragment {


    public notification_fragment() {
        // Required empty public constructor
    }

    private View layout;
    private SwipeMenuListView notificationListView;

    private notificationAdapter notificationListAdapter;
    private ArrayList<notificationItem> notificationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.notification_fragment, container, false);

        notificationList = new ArrayList<notificationItem>();
        notificationListAdapter = new notificationAdapter();
        notificationListAdapter.setList(notificationList);
        notificationListAdapter.setActivity(getActivity());

        notificationListView= (SwipeMenuListView)layout.findViewById(R.id.notificationListView);
        notificationListView.setAdapter(notificationListAdapter);
        notificationListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        notificationListView.setMenuCreator(notificationListCreator);

        notificationListView.setOnMenuItemClickListener(notificationListView_OnMenuItemClickListener);

        notificationList.add(new notificationItem(R.drawable.ic_scooter, "2016-09-30 11:44", "915-LPZ 停靠在光復路附近"));
        notificationList.add(new notificationItem(R.drawable.ic_notice, "2016-09-30 11:52", "915-LPZ 鑰匙圈連線中斷! 請檢查並重新連接"));

        return layout;
    }

    private final SwipeMenuCreator notificationListCreator = new SwipeMenuCreator() {

        private int dp2px(int dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        }

        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xef, 0x6c, 0)));
            deleteItem.setWidth(dp2px(60));
            deleteItem.setIcon(R.drawable.ic_swipe_delete);
            menu.addMenuItem(deleteItem);
        }
    };

    private OnMenuItemClickListener notificationListView_OnMenuItemClickListener = new OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
            final notificationItem notification = notificationList.get(position);
            switch (index) {
                case 0:
                    // delete
                    new AlertDialog.Builder(notification_fragment.this.getActivity())
                            .setTitle("確認刪除?")
                            .setPositiveButton("是", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    notificationList.remove(position);
                                    notificationListAdapter.notifyDataSetChanged();
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


}
