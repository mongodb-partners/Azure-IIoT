package com.example.trackerapp.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import java.util.Date;

public class TrackingGeoSpatial extends RealmObject {
    @PrimaryKey
    @Required
    private String _id;

    private Date _modifiedTS;

    private String city;

    private TrackingGeoSpatial_location location;

    private String owner;

    private String partition_key;

    // Standard getters & setters
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public Date get_modifiedTS() { return _modifiedTS; }
    public void set_modifiedTS(Date Timestamp) { this._modifiedTS = Timestamp; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public TrackingGeoSpatial_location getLocation() { return location; }
    public void setLocation(TrackingGeoSpatial_location location) { this.location = location; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public String getPartition_key() { return partition_key; }
    public void setPartition_key(String partition_key) { this.partition_key = partition_key; }

    public TrackingGeoSpatial() {}
    @Override
    public String toString() {
        return owner + " - " + _id;
    }
}
