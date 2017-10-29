package com.project.vincent.activityplanner;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent on 23/04/2016.
 */
public class ListAdapter extends ArrayAdapter<Event> {

    private List<Event> events;

    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int resource, List<Event> events) {
        super(context, resource, events);

        this.events = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.item_list_row, null);
        }

        Event event = getItem(position);

        if (event != null) {
            TextView title = (TextView) v.findViewById(R.id.title);
            TextView address = (TextView) v.findViewById(R.id.address);
            TextView startDate = (TextView) v.findViewById(R.id.startDate);
            TextView endDate = (TextView) v.findViewById(R.id.endDate);

            if (title != null)
                title.setText(event.getName());
            if (address != null)
                address.setText(event.getAddress());
            if (startDate != null)
                startDate.setText(event.getStartingDate());
            if (endDate != null)
                endDate.setText(event.getEndingDate());
        }
        return v;
    }

}
