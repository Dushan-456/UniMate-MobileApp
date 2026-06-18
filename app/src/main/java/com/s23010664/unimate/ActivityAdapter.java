package com.s23010664.unimate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Custom adapter for displaying ActivityItem objects in a ListView
 * using the activity_list1.xml card layout.
 * Handles the "Completed" button to delete activities from the database.
 */
public class ActivityAdapter extends BaseAdapter {

    private Context context;
    private List<ActivityItem> items;
    private DBHelper dbHelper;

    public ActivityAdapter(Context context, List<ActivityItem> items) {
        this.context = context;
        this.items = items;
        this.dbHelper = new DBHelper(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ActivityItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_list1, parent, false);
        }

        ActivityItem item = getItem(position);

        // Populate the card fields
        TextView titleText = convertView.findViewById(R.id.activity_text);
        TextView timeText = convertView.findViewById(R.id.activity_time);
        TextView dateText = convertView.findViewById(R.id.textView10);
        TextView completedBtn = convertView.findViewById(R.id.textView7);

        titleText.setText(item.getTypeLabel() + " " + item.getName());
        timeText.setText(item.getTime() != null ? item.getTime() : "");
        dateText.setText(item.getDate() != null ? item.getDate() : "");

        // Handle "Completed" button click — deletes the activity
        completedBtn.setOnClickListener(v -> {
            boolean deleted = false;
            switch (item.getType()) {
                case "event":
                    deleted = dbHelper.deleteEvent(item.getId());
                    break;
                case "lecture":
                    deleted = dbHelper.deleteLecture(item.getId());
                    break;
                case "assignment":
                    deleted = dbHelper.deleteAssignment(item.getId());
                    break;
            }

            if (deleted) {
                items.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Activity completed!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to remove activity", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
