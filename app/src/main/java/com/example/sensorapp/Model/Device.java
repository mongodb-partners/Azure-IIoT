package com.example.sensorapp.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

public class Device extends RealmObject {
    @PrimaryKey
    private ObjectId _id;

    private Date added_on;

    private String description;

    private String device_id;

    private String device_name;

    private String manufacturer;

    private String parent_id;

    private String partition_key;

    private String protocol;

    private Boolean status;

    private String unit;

    private Integer year_of_manufacture;

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

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getParentId() { return parent_id; }
    public void setParentId(String parent_id) { this.parent_id = parent_id; }

    public String getPartitionKey() { return partition_key; }
    public void setPartitionKey(String partition_key) { this.partition_key = partition_key; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Integer getYearOfManufacture() { return year_of_manufacture; }
    public void setYearOfManufacture(Integer year_of_manufacture) { this.year_of_manufacture = year_of_manufacture; }


    @Override
    public String toString() {
        return device_id+ " - " +device_name;
    }

    public List<String> returnList() {
        List<String> deviceData = new ArrayList<>();
        deviceData.add(device_id);
        deviceData.add(description);
        deviceData.add(parent_id);
        deviceData.add(String.valueOf(added_on));
        deviceData.add(String.valueOf(status));
        deviceData.add(protocol);
        deviceData.add(unit);
        deviceData.add(String.valueOf(year_of_manufacture));
        deviceData.add(manufacturer);
        return deviceData;
    }
}

