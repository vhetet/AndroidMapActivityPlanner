package com.project.vincent.activityplanner;

import android.app.DatePickerDialog;
import android.location.Location;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class InfoPanelActivity extends Fragment {


    /**
     * Retrieved the data send by the main activity as argument and convert to a JSON object then return the inflated view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            Bundle args = getArguments();
            JSONObject place = new JSONObject(args.getString("place", "No results"));
        } catch (org.json.JSONException e) {
            Log.d("JSON", e.toString());
        }
        return inflater.inflate(R.layout.activity_info_panel, container, false);
    }

    /**
     * Use the data sent as argument to display it
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        try {
            Bundle args = getArguments();
            JSONObject place = new JSONObject(args.getString("place"));

            // event creation
            final Event event = new Event();
            setEvent(event, place);

            // view modification
            setView(event);

        } catch (org.json.JSONException e) {
            Log.d("JSON", e.toString());
        }
    }

    /**
     * Update the event with information from the JSONObject that was retrieved from the marker. If the JSONObject does not have a parameter, setevent add an empty String, 0, empty Location, empty Date
     *
     * @param event
     * @param place
     * @return event
     */
    public Event setEvent(Event event, JSONObject place) {

        try {
            if (place.getString("name") != null )
                event.setName(place.getString("name"));
            else
                event.setName("");
            event.setStartingDate("");
            event.setEndingDate("");
            JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
            Location l = new Location("");
            if(location.getString("lat") != null && location.getString("lng") != null) {
                l.setLatitude(location.getDouble("lat"));
                l.setLongitude(location.getDouble("lng"));
            }
            event.setLocation(l);
            if (place.getString("rating") != null )
                event.setRating(place.getInt("rating"));
            else
                event.setRating(0);
            if (place.getString("id") != null )
                event.setIdAPI(place.getString("id"));
            else
                event.setIdAPI("");
            if (place.getString("place_id") != null )
                event.setPlaceId(place.getString("place_id"));
            else
                event.setPlaceId("");
            if (place.getString("price_level") != null )
                event.setPriceLevel(place.getInt("price_level"));
            else
                event.setPriceLevel(0);
            if (place.getString("reference") != null )
                event.setReference(place.getString("reference"));
            else
                event.setReference("");
            if (place.getString("vicinity") != null )
                event.setAddress(place.getString("vicinity"));
            else
                event.setAddress("");
        } catch (JSONException e) {
            Log.d("JSONException", e.toString());
        }
        return event;
    }

    /**
     * Display the Timepicker
     *
     * @param v
     * @param event
     * @param view
     * @param start
     */
    public void showTimePickerDialog(View v, Event event, View view, Boolean start) {
        DialogFragment newFragment = new TimePickerFragment(event, view, start);
        newFragment.show(getFragmentManager(), "timePicker");
    }

    /**
     * display the date picker
     *
     * @param v
     * @param event
     * @param view
     * @param start
     */
    public void showDatePickerDialog(View v, Event event, View view, Boolean start) {
        DialogFragment newFragment = new DatePickerFragment(event, view, start);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /**
     * Use to display the element of the event in the view
     *
     * @param event
     */
    public void setView(final Event event) {
        TextView name = (TextView) getView().findViewById(R.id.textView2);
        TextView address = (TextView) getView().findViewById(R.id.textView3);
        RatingBar ratingBar = (RatingBar) getView().findViewById(R.id.ratingBar);
        Button button = (Button) getView().findViewById(R.id.button);
        Button buttonDateStart = (Button) getView().findViewById(R.id.buttonDateStart);
        Button buttonTimeStart = (Button) getView().findViewById(R.id.buttonTimeStart);
        Button buttonDateEnd = (Button) getView().findViewById(R.id.buttonDateEnd);
        Button buttonTimeEnd = (Button) getView().findViewById(R.id.buttonTimeEnd);


        name.setText(event.getName());
        address.setText(event.getAddress());
        ratingBar.setRating(event.getRating());


        // save button click listener
        button.setOnClickListener(new OnClickListenerSave(event));
        // Date and time button listener
        buttonDateStart.setOnClickListener(new OnClickListenerDate(event, getView(), true));
        buttonTimeStart.setOnClickListener(new OnClickListenerTime(event, getView(), true));
        buttonDateEnd.setOnClickListener(new OnClickListenerDate(event, getView(), false));
        buttonTimeEnd.setOnClickListener(new OnClickListenerTime(event, getView(), false));

    }

    /**
     * Custom time picker fragment
     */
    // todo: use the setargument instead of the constructor
    // actually, I cannot send a view or an event with arguments, so obviously I could convert them to JSON or something like that but that is a lot of work
    // Maybe make my class parcelable
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        Event event;
        View viewMain;
        Boolean start;

        public TimePickerFragment() {}

        public TimePickerFragment(Event event, View view, Boolean start) {
            this.event = event;
            this.viewMain = view;
            this.start = start;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        /**
         * convert the time retrieved to the right format (HH:mm)
         *
         * @param view
         * @param hourOfDay
         * @param minute
         */
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Calendar calendar = new GregorianCalendar(0, 0, 0, hourOfDay, minute);
            if(start) {
                TextView tv = (TextView) viewMain.findViewById(R.id.timeStart);
                Log.d("date formatting", sdf.format(calendar.getTime()));
                tv.setText(sdf.format(calendar.getTime()));
            } else {
                TextView tv = (TextView) viewMain.findViewById(R.id.timeEnd);
                Log.d("date formatting", sdf.format(calendar.getTime()));
                tv.setText(sdf.format(calendar.getTime()));
            }

        }
    }

    /**
     * Custom time picker fragment
     */
    // todo: use the setargument instead of the constructor
    // actually, I cannot send a view or an event with arguments, so obviously I could convert them to JSON or something like that but that is a lot of work
    // Maybe make my class parcelable
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        Event event;
        View viewMain;
        Boolean start;

        public DatePickerFragment() {}

        public DatePickerFragment(Event event, View view, Boolean start) {
            this.event = event;
            this.viewMain = view;
            this.start = start;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        /**
         * convert the date retrieved to the right format (yyyy MMM dd)
         *
         * @param view
         * @param year
         * @param monthOfYear
         * @param dayOfMonth
         */
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd");
            Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
            if(start) {
                TextView tv = (TextView) viewMain.findViewById(R.id.dateStart);
                Log.d("date formatting", sdf.format(calendar.getTime()));
                tv.setText(sdf.format(calendar.getTime()));
            } else {
                TextView tv = (TextView) viewMain.findViewById(R.id.dateEnd);
                Log.d("date formatting", sdf.format(calendar.getTime()));
                tv.setText(sdf.format(calendar.getTime()));
            }
        }
    }

    /**
     * Custom Click listener for the button save
     */
    public class OnClickListenerSave implements View.OnClickListener {

        Event event;

        /**
         * Constructor of the custom save button click listener
         *
         * @param event
         */
        public OnClickListenerSave(Event event) {
            this.event = event;
        }

        /**
         * Save the data of the event to the database
         *
         * @param v
         */
        @Override
        public void onClick(View v) {

            EditText dateStart = (EditText) getView().findViewById(R.id.dateStart);
            EditText dateEnd = (EditText) getView().findViewById(R.id.dateEnd);
            EditText timeStart = (EditText) getView().findViewById(R.id.timeStart);
            EditText timeEnd = (EditText) getView().findViewById(R.id.timeEnd);

            String startDate = dateStart.getText().toString() + " " + timeStart.getText().toString();
            String endDate = dateEnd.getText().toString() + " " + timeEnd.getText().toString();


            if (checkFormat(startDate) && checkFormat(endDate)) {
                // removing the fragment after saving
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .remove(getActivity()
                    .getSupportFragmentManager()
                    .findFragmentById(R.id.info_panel_frame))
                    .commit();
                EventSqlHelper db = new EventSqlHelper(getContext());
                event.setStartingDate(startDate);
                event.setEndingDate(endDate);
                db.addEvent(event);
                Toast.makeText(getActivity(), "The event is saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "The date format is not correct, please change it", Toast.LENGTH_SHORT).show();
            }


        }
    }

    /**
     * Custom Click listener for the button Starting date
     */
    public class OnClickListenerDate implements View.OnClickListener {

        Event event;
        View view;
        Boolean start;
        /**
         * Constructor of the custom date button Date
         */
        public OnClickListenerDate(Event event, View view, Boolean start) {
            this.event = event;
            this.view = view;
            this.start = start;
        }

        /**
         * Display the date time picker
         *
         * @param v
         */
        @Override
        public void onClick(View v) {
            showDatePickerDialog(v, event, view, start);
        }
    }

    /**
     * Custom Click listener for the button time
     */
    public class OnClickListenerTime implements View.OnClickListener {

        Event event;
        View view;
        Boolean start;
        /**
         * Constructor of the custom date button click listener
         */
        public OnClickListenerTime(Event event, View view, Boolean start) {
            this.event = event;
            this.view = view;
            this.start = start;
        }

        /**
         * Display the date time picker
         *
         * @param v
         */
        @Override
        public void onClick(View v) {
            showTimePickerDialog(v, event, view, start);
        }
    }

    /**
     * check if the format is right
     *
     * @param calendar
     * @return true if the format is right
     */
    public Boolean checkFormat(String calendar) {
        Boolean valid = true;
//        Calendar cal = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm");
        try {
//            cal.setTime(sdf.parse(calendar));
            sdf.parse(calendar);
        } catch (java.text.ParseException e) {
            valid = false;
        }
        return valid;
    }
}
