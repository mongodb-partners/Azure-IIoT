package com.example.sensorapp.Model;




import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

public class User extends RealmObject {
    @PrimaryKey
    private ObjectId _id;

    private Date added_on;

    private String description;

    private String device_id;

    private String device_name;

    private String parent_id;

    private String partition_key;

    // Standard getters & setters
    public ObjectId getId() { return _id; }
    public void setId(ObjectId _id) { this._id = _id; }

    public Date getAddedOn() { return added_on; }
    public void setAddedOn(Date added_on) { this.added_on = added_on; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDeviceId() { return device_id; }
    public void setDeviceId(String device_id) { this.device_id = device_id; }

    public String getDeviceName() { return device_name; }
    public void setDeviceName(String device_name) { this.device_name = device_name; }

    public String getParentId() { return parent_id; }
    public void setParentId(String parent_id) { this.parent_id = parent_id; }

    public String getPartitionKey() { return partition_key; }
    public void setPartitionKey(String partition_key) { this.partition_key = partition_key; }

    public String deviceID() {
        return device_id;
    }

    @Override
    public String toString() {
        return description;
    }

    public List<String> returnList() {
        List<String> deviceData = new ArrayList<>();
        deviceData.add(device_id);
        deviceData.add(description);
        deviceData.add(parent_id);
        deviceData.add(String.valueOf(added_on));
        return deviceData;
    }
}


