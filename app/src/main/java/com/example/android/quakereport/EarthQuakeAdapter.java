package com.example.android.quakereport;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.example.android.quakereport.R.id.date;
import static com.example.android.quakereport.R.id.magnitude;

/**
 * Created by thara on 10/12/17.
 */

public class EarthQuakeAdapter extends ArrayAdapter<EarthQuake> {
    public EarthQuakeAdapter(Activity context, List<EarthQuake> words) {
        super(context, 0, words);
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.quake_list, parent, false);
        }
        EarthQuake currentEarthQuake = (EarthQuake) getItem(position);

        TextView magnitudeView = (TextView) listItemView.findViewById(magnitude);
        String sMagnitude = formatMagnitude(currentEarthQuake.getMagnitude());
        magnitudeView.setText(sMagnitude);

        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeView.getBackground();
        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentEarthQuake.getMagnitude());
        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        String placeStr = currentEarthQuake.getPlace();
        String offset = "";
        String primaryLocation = "";

        TextView offsetView = (TextView) listItemView.findViewById(R.id.location_offset);
        TextView primaryLocView = (TextView) listItemView.findViewById(R.id.primary_location);

        if(placeStr.contains("of")) {
            String[] placeList = placeStr.split("of");
            offset = placeList[0];
            primaryLocation = placeList[1];
            offsetView.setText(offset+" of");
            primaryLocView.setText(primaryLocation);
        }
        else{
            offsetView.setText("Near The ");
            primaryLocView.setText(placeStr);
        }


        Date dateObj = new Date(currentEarthQuake.getTimeinMilliSecs());
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        String dateToDisplay = formatDate(dateObj);

        dateView.setText(dateToDisplay);

        TextView timeView = (TextView) listItemView.findViewById(R.id.time);
        String timeToDisplay = formatTime(dateObj);

        timeView.setText(timeToDisplay);

        return listItemView;
    }
    /**
     * Helper method to format date
     */
    private String formatDate(Date dateObj){
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObj);
    }

    /**
     * Helper method to format time
     */
    private String formatTime(Date dateObj){
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObj);
    }

    /**
     * Helper method to format magnitude
     */
    private String formatMagnitude(double magnitude) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(magnitude);
    }

    /**
     * This methods returns color based on value of the maginitude.
     */
    private int getMagnitudeColor( double magnitude){
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}
