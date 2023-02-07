package com.example.sensorapp.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Failure extends RealmObject {
    @PrimaryKey
    private ObjectId _id;

    private String device_id;

    private String failure;

    private String partition_key;

    private Date ts;

//    private Date ts;

    private String ack;

    // Standard getters & setters
    public ObjectId getId() { return _id; }
    public void setId(ObjectId _id) { this._id = _id; }

    public String getDeviceId() { return device_id; }
    public void setDeviceId(String device_id) { this.device_id = device_id; }

    public String getFailure() { return failure; }
    public void setFailure(String failure) { this.failure = failure; }

    public String getPartitionKey() { return partition_key; }
    public void setPartitionKey(String partition_key) { this.partition_key = partition_key; }

    public String getTs() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy kk:mm");
        return sdf.format(ts);
    }
    public void setTs(Date ts) { this.ts = ts; }

    public Failure() {}

    public String toData() {
        return device_id;
    }

    public String getAck() {
        return ack;
    }

    public void setAck(String ack) {
        this.ack = ack;
    }

    public List<String> returnList() {
        List<String> deviceData = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy kk:mm");
        deviceData.add(device_id);
        deviceData.add(failure);
        deviceData.add(sdf.format(ts));
        deviceData.add(ack);
        return deviceData;
    }
}
