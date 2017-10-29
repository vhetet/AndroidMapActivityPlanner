package com.project.vincent.activityplanner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * Created by Vincent on 23/04/2016.
 */
public class SavedEventFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved_event, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        List<Event> events;

        EventSqlHelper db = new EventSqlHelper(getContext());

        events = db.getAllEvents();

        ListView listSavedEvent = (ListView) view.findViewById(R.id.listSavedEvent);

        ListAdapter listAdapter = new ListAdapter(getContext(), R.layout.fragment_saved_event, events);
        listSavedEvent.setAdapter(listAdapter);
    }
}
