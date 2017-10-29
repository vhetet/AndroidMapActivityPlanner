package com.project.vincent.activityplanner;

import android.location.Location;

import com.google.android.gms.location.places.Place;

import java.util.Date;

/**
 * Created by Vincent on 15/04/2016.
 * An event is a location/place and a timing. For example it could be a restaurant for lunch ==> restaurant name, starting hour, ending hour
 */
public class Event {
    private int id;
    private String name;
    private String startingDate;
    private String endingDate;
    private Location location;
    private int rating;
    private String idAPI;
    private String placeId;
    private int priceLevel;
    private String reference;
    private String address;

    // Constructor

    public Event() {
    }

    public Event(String name) {
        this.name = name;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getIdAPI() {
        return idAPI;
    }

    public void setIdAPI(String idAPI) {
        this.idAPI = idAPI;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(int priceLevel) {
        this.priceLevel = priceLevel;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
