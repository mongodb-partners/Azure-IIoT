import logging
import pymongo
import pickle
import pandas as pd
import azure.functions as func
import datetime
from sklearn import tree

def tranform(data):
    try:
        del data['device_id']
        df_inputdata = pd.DataFrame([data])
        df_inputdata[['Air temperature','Process temperature','Rotational speed','Torque','Tool wear']] = df_inputdata[['Air temperature','Process temperature','Rotational speed','Torque','Tool wear']].astype(float)
        df_inputdata = df_inputdata[['Type', 'Air temperature', 'Process temperature', 'Rotational speed','Torque', 'Tool wear']]
        return df_inputdata 
    except Exception as e:
        print("EXCEPTION", e)

def find_latest(client):
    db = client['IOT']
    collection = db['Sensors_TS']
    # Find the latest document inserted
    result = collection.find({"device_id" : "d001"}, projection={'_id': 0, 'ts': 0}, sort=[('_id', -1)], limit=1)
    return next(result) 

def add_latest_failure(client, prediction, device_id):
    db = client['IOT']
    collection = db['Failure']
    failures = ['No Failure', 'Power Failure', 'Tool Wear Failure','Overstrain Failure', 'Random Failures','Heat Dissipation Failure']
    print(device_id)
    failure_doc = {
        "device_id" : device_id,
        "failure" : failures[prediction],
        "ts": datetime.datetime.utcnow(),
        "partition_key" : "sensor"
    }
    doc = collection.insert_one(failure_doc)
    return doc


def main(req: func.HttpRequest) -> func.HttpResponse:
    try:
        logging.info('Python HTTP trigger function processed a request.')
        print("HTTP Trigger")

        client = pymongo.MongoClient("mongodb+srv://main_user:test123@demo-cluster.fmxyq.mongodb.net/test")
        db = client['IIOT']
        coll = db['models']

        # Find data from model dump
        model_out = coll.find_one({"tag": "DecisionTree"})
        model_bin = pickle.loads(model_out['model_ckpt'])

        data = find_latest(client)
        device_id = data['device_id']
        print(data)
        df = tranform(data)
        prediction = model_bin.predict(df)
        print("pred {}".format(prediction))
        # Update for latest 
        if prediction[0] > 0:
            add_latest_failure(client, prediction[0], device_id)
            return func.HttpResponse("Inserted latest failure")
        else:
            return func.HttpResponse("No failure")
    except Exception as e:
        return func.HttpResponse(f"{e}") 