package com.s23010664.unimate;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class LostNFoundAdapter extends BaseAdapter {

    private Context context;
    private List<LostNFoundItem> items;
    private DBHelper dbHelper;

    public LostNFoundAdapter(Context context, List<LostNFoundItem> items) {
        this.context = context;
        this.items = items;
        this.dbHelper = new DBHelper(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public LostNFoundItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.lost_n_found_item, parent, false);
        }

        LostNFoundItem item = getItem(position);

        TextView titleText = convertView.findViewById(R.id.item_title);
        TextView dateLocationText = convertView.findViewById(R.id.item_date_location);
        TextView descText = convertView.findViewById(R.id.item_description);
        TextView contactText = convertView.findViewById(R.id.item_contact);
        ImageView imageView = convertView.findViewById(R.id.item_image);
        Button deleteBtn = convertView.findViewById(R.id.item_delete_btn);

        titleText.setText(item.getTitle());
        dateLocationText.setText(item.getDate() + " | " + item.getLocation());
        descText.setText(item.getDescription());
        contactText.setText("Contact: " + item.getContact());

        if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
            imageView.setVisibility(View.VISIBLE);
            try {
                imageView.setImageURI(Uri.parse(item.getImageUri()));
            } catch (Exception e) {
                imageView.setVisibility(View.GONE);
            }
        } else {
            imageView.setVisibility(View.GONE);
        }

        deleteBtn.setOnClickListener(v -> {
            boolean deleted = dbHelper.deleteLostNFoundItem(item.getId());
            if (deleted) {
                items.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Item deleted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
