package com.example.sensorapp.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class Failure extends RealmObject {
    @PrimaryKey
    private ObjectId _id;

    private String desc;

    private String device_id;

    private String failure;

    private String partition_key;

    private String prediction_values;

    private String ts;

    // Standard getters & setters
    public ObjectId getId() { return _id; }
    public void setId(ObjectId _id) { this._id = _id; }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }

    public String getDeviceId() { return device_id; }
    public void setDeviceId(String device_id) { this.device_id = device_id; }

    public String getFailure() { return failure; }
    public void setFailure(String failure) { this.failure = failure; }

    public String getPartitionKey() { return partition_key; }
    public void setPartitionKey(String partition_key) { this.partition_key = partition_key; }

    public String getPredictionValues() { return prediction_values; }
    public void setPredictionValues(String prediction_values) { this.prediction_values = prediction_values; }

    public String getTs() { return ts; }
    public void setTs(String ts) { this.ts = ts; }

    public Failure() {}

    public String toData() {
        return device_id;
    }


    public List<String> returnList() {
        List<String> deviceData = new ArrayList();
        deviceData.add(device_id);
        deviceData.add(failure);
        deviceData.add(desc);
        deviceData.add(prediction_values);
        deviceData.add(ts);
        return deviceData;
    }
}
